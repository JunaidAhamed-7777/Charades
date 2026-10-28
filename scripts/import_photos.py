"""Developer-only picture importer. The Android app never calls this script.

Uses each subject's Wikipedia lead image and preserves the source file's
description page and author metadata. Run from the repository root.
Existing verified assets are reused; use --refresh to fetch all sources again.
"""
from __future__ import annotations

import argparse
from concurrent.futures import ThreadPoolExecutor, as_completed
import hashlib
from html import unescape
from html.parser import HTMLParser
from io import BytesIO
import json
from pathlib import Path
import re
import time
import unicodedata
from urllib.parse import quote

from PIL import Image, ImageOps
import requests

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "app/src/main/assets"
MANIFEST = ROOT / "docs/photo_sources.json"
UA = "CharadesContentBuilder/1.0 (personal offline game; https://github.com/JunaidAhamed-7777/Charades)"

# Exact Wikipedia articles avoid ambiguous or unrelated image-search matches.
CATEGORIES = [
    ("animals", "Animals", "Big cats, tiny waddles, and everything in between.", "#E97451", [
        ("Lion", "Lion"), ("Tiger", "Tiger"), ("Elephant", "African bush elephant"),
        ("Giraffe", "Giraffe"), ("Panda", "Giant panda"), ("Penguin", "Emperor penguin"),
        ("Dolphin", "Common bottlenose dolphin"), ("Zebra", "Plains zebra"),
        ("Kangaroo", "Red kangaroo"), ("Peacock", "Indian peafowl")]),
    ("movies", "Movies", "Big-screen favorites, one unforgettable clue at a time.", "#BC687C", [
        ("Jurassic Park", "Jurassic Park"), ("Titanic", "Titanic (1997 film)"),
        ("The Lion King", "The Lion King"), ("Finding Nemo", "Finding Nemo"),
        ("Avatar", "Avatar (2009 film)"), ("Sholay", "Sholay"),
        ("Dilwale Dulhania Le Jayenge", "Dilwale Dulhania Le Jayenge"),
        ("3 Idiots", "3 Idiots"), ("The Dark Knight", "The Dark Knight"),
        ("Home Alone", "Home Alone")]),
    ("famous_people", "Famous People", "The stars, legends, and icons everyone knows.", "#9E76AB", [
        ("Shah Rukh Khan", "Shah Rukh Khan"), ("Virat Kohli", "Virat Kohli"),
        ("MS Dhoni", "MS Dhoni"), ("A. P. J. Abdul Kalam", "A. P. J. Abdul Kalam"),
        ("Sachin Tendulkar", "Sachin Tendulkar"), ("Taylor Swift", "Taylor Swift"),
        ("Lionel Messi", "Lionel Messi"), ("Cristiano Ronaldo", "Cristiano Ronaldo"),
        ("Albert Einstein", "Albert Einstein"), ("Charlie Chaplin", "Charlie Chaplin")]),
    ("animes", "Animes", "Beloved series and films from the world of anime.", "#DDA343", [
        ("Naruto", "Naruto season 1"), ("One Piece", "One Piece season 1"),
        ("Dragon Ball Z", "Dragon Ball Z season 1"), ("Cowboy Bebop", "Cowboy Bebop"),
        ("Bleach", "Bleach season 1"),
        ("Demon Slayer", "Demon Slayer: Kimetsu no Yaiba season 1"),
        ("My Hero Academia", "My Hero Academia season 1"),
        ("Pokémon", "Pokémon: Indigo League"),
        ("Fullmetal Alchemist: Brotherhood", "List of Fullmetal Alchemist: Brotherhood episodes"),
        ("Spirited Away", "Spirited Away")]),
    ("anime_characters", "Anime Characters", "Heroes, rivals, and a few very familiar faces.", "#458E8A", [
        ("Naruto Uzumaki", "Naruto Uzumaki"), ("Monkey D. Luffy", "Monkey D. Luffy"),
        ("Goku", "Goku"), ("Pikachu", "Pikachu"), ("Eren Yeager", "Eren Yeager"),
        ("Light Yagami", "Light Yagami"), ("Levi Ackerman", "Levi Ackerman"),
        ("Tanjiro Kamado", "Tanjiro Kamado"), ("Sailor Moon", "Sailor Moon (character)"),
        ("Doraemon", "Doraemon (character)")]),
    ("countries", "Countries", "Recognize countries from their national flags.", "#4E8BA7", [
        ("India", "India"), ("Japan", "Japan"), ("United States", "United States"),
        ("France", "France"), ("Brazil", "Brazil"), ("Australia", "Australia"),
        ("Egypt", "Egypt"), ("Italy", "Italy"), ("South Korea", "South Korea"),
        ("Canada", "Canada")]),
    ("household_objects", "Household Objects", "Familiar things, found right at home.", "#C98D65", [
        ("Chair", "Chair"), ("Guitar", "Guitar"), ("Umbrella", "Umbrella"),
        ("Camera", "Camera"), ("Scissors", "Scissors"), ("Mug", "Mug"),
        ("Alarm clock", "Alarm clock"), ("Toothbrush", "Toothbrush"),
        ("Headphones", "Headphones"), ("Piano", "Piano")]),
]

# Exact official character art and national flags avoid unrelated lead images.
# Values are (Wikimedia host, filename). All are visually checked after import.
FILE_OVERRIDES = {
    "anime_characters_eren_yeager": ("en.wikipedia.org", "ErenYeagerIsayama.png"),
    "anime_characters_levi_ackerman": ("en.wikipedia.org", "Leviackerman.png"),
    **{"countries_" + country: ("commons.wikimedia.org", filename) for country, filename in [
        ("india", "Flag of India.svg"), ("japan", "Flag of Japan.svg"),
        ("united_states", "Flag of the United States.svg"), ("france", "Flag of France.svg"),
        ("brazil", "Flag of Brazil.svg"), ("australia", "Flag of Australia.svg"),
        ("egypt", "Flag of Egypt.svg"), ("italy", "Flag of Italy.svg"),
        ("south_korea", "Flag of South Korea.svg"), ("canada", "Flag of Canada.svg")
    ]},
}

# Verified against the original file-description pages, not inferred authors.
# Some Commons author templates include contact details and sponsor navigation.
CREDIT_OVERRIDES = {
    "Giraffe Mikumi National Park.jpg": "Muhammad Mahdi Karim",
    "Red kangaroo - melbourne zoo.jpg": "Fir0002/Flagstaffotos",
    "Sholay-poster.jpg": "Source: London Hollywood; poster artist not identified",
    "GuitareClassique5.png": "Martin Möller",
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
    return " ".join(unescape(re.sub(r"<[^>]+>", "", value)).split())


class ArtistHTMLParser(HTMLParser):
    """Find creator identity links inside expanded Commons author templates."""

    def __init__(self):
        super().__init__(convert_charrefs=True)
        self.has_table = False
        self.active_author: list[str] | None = None
        self.authors: list[str] = []
        self.text: list[str] = []
        self.skip_link = False

    def handle_starttag(self, tag, attrs):
        attributes = dict(attrs)
        if tag == "table":
            self.has_table = True
        if tag in ("br", "p", "div", "li"):
            self.text.append(" ")
        if tag == "a":
            href = attributes.get("href") or ""
            self.skip_link = any(marker in href for marker in ("/wiki/User_talk:", "/wiki/Special:"))
            if any(marker in href for marker in ("/wiki/User:", "/wiki/Creator:")):
                self.active_author = []

    def handle_data(self, data):
        if not self.skip_link:
            self.text.append(data)
        if not self.skip_link and self.active_author is not None:
            self.active_author.append(data)

    def handle_endtag(self, tag):
        if tag == "a" and self.active_author is not None:
            author = " ".join("".join(self.active_author).split())
            if author and author not in self.authors:
                self.authors.append(author)
            self.active_author = None
        if tag == "a":
            self.skip_link = False
        if tag in ("p", "div", "li"):
            self.text.append(" ")


def credit_from_metadata(metadata: dict, source_title: str) -> str:
    override = CREDIT_OVERRIDES.get(source_title.replace("_", " "))
    if override:
        return override
    raw_artist = metadata.get("Artist", {}).get("value", "")
    artist = plain(raw_artist)
    if raw_artist:
        parser = ArtistHTMLParser()
        parser.feed(raw_artist)
        artist = " ".join("".join(parser.text).split())
        if parser.has_table and parser.authors:
            artist = "; ".join(parser.authors)
        # Do not truncate a real institutional/multiple-author attribution. A
        # complex template without extractable identity requires human review.
        if len(artist) > 180:
            raise RuntimeError(f"Review the expanded author template for {source_title}")
    source_credit = plain(metadata.get("Credit", {}).get("value", ""))
    if not artist and len(source_credit) > 180:
        return "Source: Wikipedia file page; creator not identified"
    return artist or source_credit or f"{source_title} — source file page"


def slug(value: str) -> str:
    ascii_value = unicodedata.normalize("NFKD", value).encode("ascii", "ignore").decode("ascii")
    return re.sub(r"[^a-z0-9]+", "_", ascii_value.lower()).strip("_")


def fetch_word(category_id: str, label: str, article: str, cached: dict, refresh: bool):
    word_id = category_id + "_" + slug(label)
    local_path = f"images/{word_id}.webp"
    destination = ASSETS / local_path
    override = FILE_OVERRIDES.get(word_id)
    if not refresh and word_id in cached and destination.is_file() and (not override or cached[word_id]["sourceTitle"] == override[1]) and hashlib.sha256(destination.read_bytes()).hexdigest() == cached[word_id]["sha256"]:
        return cached[word_id]

    data = api("en.wikipedia.org", titles=article, redirects=1, prop="pageimages",
               piprop="thumbnail|name|original", pithumbsize=960, pilicense="any")
    page = next(iter(data["query"]["pages"].values()))
    if not override and not page.get("pageimage"):
        raise RuntimeError(f"No verified lead image for {article}")
    image_title = "File:" + (override[1] if override else page["pageimage"])
    image_host = override[0] if override else ("en.wikipedia.org" if "/wikipedia/en/" in page["original"]["source"] else "commons.wikimedia.org")
    image_data = api(image_host, titles=image_title, prop="imageinfo", iiprop="url|extmetadata", iiurlwidth=960)
    info = next(iter(image_data["query"]["pages"].values()))["imageinfo"][0]
    metadata = info.get("extmetadata", {})
    license_name = plain(metadata.get("LicenseShortName", {}).get("value", ""))
    description = plain(metadata.get("ImageDescription", {}).get("value", ""))
    credit = credit_from_metadata(metadata, image_title.removeprefix("File:"))
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
        "rawAttribution": {key: metadata[key]["value"] for key in ("Artist", "Credit") if key in metadata},
        "downloadUrl": download_url, "license": license_name, "description": description[:1000],
        "width": width, "height": height, "bytes": destination.stat().st_size,
        "sha256": hashlib.sha256(destination.read_bytes()).hexdigest(),
    }


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--refresh", action="store_true", help="Refresh previously imported photos")
    parser.add_argument("--prune", action="store_true", help="Remove retired pictures after the complete catalog is written")
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
    if args.prune:
        retained = {photo["imagePath"] for photo in results.values()}
        image_directory = (ASSETS / "images").resolve()
        for path in image_directory.glob("*.webp"):
            if path.resolve().parent != image_directory:
                raise RuntimeError(f"Refusing to remove a picture outside the image directory: {path}")
            if "images/" + path.name not in retained:
                path.unlink()
                print(f"Removed retired image {path.name}", flush=True)
    print(f"Imported {len(results)} photos in {len(categories)} sections; {sum(p['bytes'] for p in results.values()) / 1048576:.2f} MiB")


if __name__ == "__main__":
    main()
