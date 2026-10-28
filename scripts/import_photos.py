"""Developer-only photo importer. The Android app never calls this script.

Uses each subject's Wikipedia lead image and preserves the source file's
description page and author metadata. Run from the repository root.
Existing verified assets are reused; use --refresh to fetch all sources again.
"""
from __future__ import annotations

import argparse
from concurrent.futures import ThreadPoolExecutor, as_completed
import hashlib
from html import unescape
from io import BytesIO
import json
from pathlib import Path
import re
import time
from urllib.parse import quote

from PIL import Image, ImageOps
import requests

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "app/src/main/assets"
MANIFEST = ROOT / "docs/photo_sources.json"
UA = "CharadesContentBuilder/1.0 (personal offline game; https://github.com/JunaidAhamed-7777/Charades)"

# Exact Wikipedia articles avoid ambiguous or unrelated image-search matches.
CATEGORIES = [
    ("animals", "Wild things", "Big cats, tiny waddles, and everything in between.", "#E97451", [
        ("Lion", "Lion"), ("Tiger", "Tiger"), ("Elephant", "African bush elephant"),
        ("Giraffe", "Giraffe"), ("Panda", "Giant panda"), ("Penguin", "Emperor penguin"),
        ("Dolphin", "Common bottlenose dolphin"), ("Zebra", "Plains zebra"),
        ("Kangaroo", "Red kangaroo"), ("Peacock", "Indian peafowl")]),
    ("landmarks", "Around the world", "Iconic places. No passport required.", "#4E8BA7", [
        ("Taj Mahal", "Taj Mahal"), ("Eiffel Tower", "Eiffel Tower"),
        ("Great Wall of China", "Great Wall of China"), ("Statue of Liberty", "Statue of Liberty"),
        ("Colosseum", "Colosseum"), ("Great Pyramid of Giza", "Great Pyramid of Giza"),
        ("Big Ben", "Big Ben"), ("Sydney Opera House", "Sydney Opera House"),
        ("Mount Fuji", "Mount Fuji"), ("Burj Khalifa", "Burj Khalifa")]),
    ("food", "Good taste", "A deliciously difficult round of food favorites.", "#DDA343", [
        ("Pizza", "Pizza"), ("Burger", "Hamburger"), ("Sushi", "Sushi"),
        ("Ice cream", "Ice cream"), ("Samosa", "Samosa"), ("Dosa", "Dosa (food)"),
        ("Biryani", "Biryani"), ("Croissant", "Croissant"),
        ("Doughnut", "Doughnut"), ("Pancakes", "Pancake")]),
    ("sports", "Game on", "Serve, swing, swim. Get your whole body into it.", "#678F68", [
        ("Football", "Association football"), ("Cricket", "Cricket"),
        ("Basketball", "Basketball"), ("Tennis", "Tennis"), ("Badminton", "Badminton"),
        ("Swimming", "Swimming (sport)"), ("Boxing", "Boxing"), ("Surfing", "Surfing"),
        ("Skiing", "Alpine skiing"), ("Yoga", "Yoga as exercise")]),
    ("famous_faces", "Famous faces", "The stars, legends, and icons everyone knows.", "#9E76AB", [
        ("Shah Rukh Khan", "Shah Rukh Khan"), ("Virat Kohli", "Virat Kohli"),
        ("MS Dhoni", "MS Dhoni"), ("A. P. J. Abdul Kalam", "A. P. J. Abdul Kalam"),
        ("Sachin Tendulkar", "Sachin Tendulkar"), ("Taylor Swift", "Taylor Swift"),
        ("Lionel Messi", "Lionel Messi"), ("Cristiano Ronaldo", "Cristiano Ronaldo"),
        ("Albert Einstein", "Albert Einstein"), ("Charlie Chaplin", "Charlie Chaplin")]),
    ("movies", "Movie night", "Big-screen favorites, one unforgettable clue at a time.", "#BC687C", [
        ("Jurassic Park", "Jurassic Park"), ("Titanic", "Titanic (1997 film)"),
        ("The Lion King", "The Lion King"), ("Finding Nemo", "Finding Nemo"),
        ("Avatar", "Avatar (2009 film)"), ("Sholay", "Sholay"),
        ("Dilwale Dulhania Le Jayenge", "Dilwale Dulhania Le Jayenge"),
        ("3 Idiots", "3 Idiots"), ("The Dark Knight", "The Dark Knight"),
        ("Home Alone", "Home Alone")]),
    ("everyday", "Everyday magic", "Ordinary things. Extraordinary acting.", "#C98D65", [
        ("Bicycle", "Bicycle"), ("Guitar", "Guitar"), ("Umbrella", "Umbrella"),
        ("Camera", "Camera"), ("Sunglasses", "Sunglasses"), ("Backpack", "Backpack"),
        ("Alarm clock", "Alarm clock"), ("Toothbrush", "Toothbrush"),
        ("Headphones", "Headphones"), ("Piano", "Piano")]),
    ("nature", "Great outdoors", "Natural wonders that are larger than life.", "#458E8A", [
        ("Volcano", "Volcano"), ("Waterfall", "Waterfall"), ("Rainbow", "Rainbow"),
        ("Desert", "Desert"), ("Iceberg", "Iceberg"), ("Sunflower", "Sunflower"),
        ("Palm tree", "Coconut"), ("Coral reef", "Coral reef"),
        ("Northern lights", "Aurora"), ("Full moon", "Full moon")]),
]

# Lead images can be logos, diagrams, or pictograms. These exact file choices
# were visually checked to keep all non-film clues photographic.
FILE_OVERRIDES = {
    "landmarks_eiffel_tower": "Eiffel Tower in 2022 02.jpg",
    "sports_skiing": "Airforce skiing at keystone colorado.jpg",
    "nature_palm_tree": "Coconut palms. Cambodia.jpg",
}


def get(url: str, **kwargs):
    last = None
    for attempt in range(4):
        try:
            response = requests.get(url, headers={"User-Agent": UA}, timeout=45, **kwargs)
            response.raise_for_status()
            return response
        except (requests.RequestException, ValueError) as error:
            last = error
            time.sleep(1 + attempt * 2)
    raise RuntimeError(f"Could not read {url}: {last}")


def api(host: str, **params):
    return get(f"https://{host}/w/api.php", params={"action": "query", "format": "json", **params}).json()


def plain(value: str) -> str:
    return unescape(re.sub(r"<[^>]+>", "", value)).strip()


def slug(value: str) -> str:
    return re.sub(r"[^a-z0-9]+", "_", value.lower()).strip("_")


def fetch_word(category_id: str, label: str, article: str, cached: dict, refresh: bool):
    word_id = category_id + "_" + slug(label)
    local_path = f"images/{word_id}.webp"
    destination = ASSETS / local_path
    override = FILE_OVERRIDES.get(word_id)
    if not refresh and word_id in cached and destination.is_file() and (not override or cached[word_id]["sourceTitle"] == override) and hashlib.sha256(destination.read_bytes()).hexdigest() == cached[word_id]["sha256"]:
        return cached[word_id]

    data = api("en.wikipedia.org", titles=article, redirects=1, prop="pageimages",
               piprop="thumbnail|name|original", pithumbsize=960, pilicense="any")
    page = next(iter(data["query"]["pages"].values()))
    if not page.get("pageimage"):
        raise RuntimeError(f"No verified lead image for {article}")
    image_title = "File:" + (override or page["pageimage"])
    image_host = "en.wikipedia.org" if not override and "/wikipedia/en/" in page["original"]["source"] else "commons.wikimedia.org"
    image_data = api(image_host, titles=image_title, prop="imageinfo", iiprop="url|extmetadata", iiurlwidth=960)
    info = next(iter(image_data["query"]["pages"].values()))["imageinfo"][0]
    metadata = info.get("extmetadata", {})
    artist = plain(metadata.get("Artist", {}).get("value", ""))
    license_name = plain(metadata.get("LicenseShortName", {}).get("value", ""))
    description = plain(metadata.get("ImageDescription", {}).get("value", ""))
    credit = artist or plain(metadata.get("Credit", {}).get("value", "")) or f"{image_title.removeprefix('File:')} — source file page"
    # Wikipedia gives correctly scaled server thumbnails; preserve complete image.
    download_url = info.get("thumburl", info["url"]) if override else page.get("thumbnail", page["original"])["source"]
    raw = get(download_url).content
    with Image.open(BytesIO(raw)) as source:
        photo = ImageOps.exif_transpose(source).convert("RGB")
        photo.thumbnail((960, 960), Image.Resampling.LANCZOS)
        if min(photo.size) < 100:
            raise RuntimeError(f"Photo too small for {article}: {photo.size}")
        destination.parent.mkdir(parents=True, exist_ok=True)
        photo.save(destination, "WEBP", quality=84, method=6)
        width, height = photo.size
    return {
        "id": word_id, "text": label, "imagePath": local_path,
        "sourceTitle": image_title.removeprefix("File:"), "sourceUrl": info["descriptionurl"],
        "credit": credit, "articleUrl": "https://en.wikipedia.org/wiki/" + quote(page["title"].replace(" ", "_")),
        "downloadUrl": download_url, "license": license_name, "description": description[:1000],
        "width": width, "height": height, "bytes": destination.stat().st_size,
        "sha256": hashlib.sha256(destination.read_bytes()).hexdigest(),
    }


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--refresh", action="store_true", help="Refresh previously imported photos")
    args = parser.parse_args()
    cached = {}
    if MANIFEST.exists():
        cached = {image["id"]: image for image in json.loads(MANIFEST.read_text(encoding="utf-8"))["images"]}
    results = {}
    errors = []
    jobs = [(category[0], label, article) for category in CATEGORIES for label, article in category[4]]
    with ThreadPoolExecutor(max_workers=5) as pool:
        pending = {pool.submit(fetch_word, *job, cached, args.refresh): job for job in jobs}
        for future in as_completed(pending):
            job = pending[future]
            try:
                photo = future.result()
                results[photo["id"]] = photo
                print(f"OK {photo['id']} ({photo['bytes']:,} bytes)", flush=True)
            except Exception as error:
                errors.append((job, str(error)))
                print(f"FAILED {job}: {error}", flush=True)
    # Always preserve completed imports so an interrupted/failed import resumes.
    MANIFEST.parent.mkdir(parents=True, exist_ok=True)
    MANIFEST.write_text(json.dumps({"version": 1, "retrievedOn": "2026-10-28", "images": [results[key] for key in sorted(results)]}, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
    if errors:
        raise SystemExit(f"{len(errors)} photos failed. Fix exact article choices or connectivity and rerun.")
    categories = []
    public_fields = ("id", "text", "imagePath", "sourceTitle", "sourceUrl", "credit")
    for category_id, title, description, accent, prompts in CATEGORIES:
        words = [{key: results[category_id + "_" + slug(label)][key] for key in public_fields} for label, _ in prompts]
        categories.append({"id": category_id, "title": title, "description": description,
                           "imagePath": words[0]["imagePath"], "accent": accent, "words": words})
    catalog = ASSETS / "categories/bundled_categories.json"
    catalog.parent.mkdir(parents=True, exist_ok=True)
    catalog.write_text(json.dumps({"version": 1, "categories": categories}, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
    print(f"Imported {len(results)} photos in {len(categories)} sections; {sum(p['bytes'] for p in results.values()) / 1048576:.2f} MiB")


if __name__ == "__main__":
    main()
