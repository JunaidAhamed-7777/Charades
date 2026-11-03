"""Developer-only picture importer. The Android app never calls this script.

Uses each subject's Wikipedia lead image and preserves the source file's
description page and author metadata. Run from the repository root.
Existing verified assets are reused; use --refresh to fetch all sources again.
"""
from __future__ import annotations

import argparse
from datetime import date
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

# Exact source articles prevent ambiguous or unrelated image-search matches.
from catalog_screen_titles import MOVIES, ANIMES, SCREEN_FILE_OVERRIDES, SCREEN_DIRECT_SOURCES

ANIMALS = [
    ("Lion", "Lion"), ("Tiger", "Tiger"), ("Elephant", "African bush elephant"),
    ("Giraffe", "Giraffe"), ("Panda", "Giant panda"), ("Penguin", "Emperor penguin"),
    ("Dolphin", "Common bottlenose dolphin"), ("Zebra", "Plains zebra"),
    ("Kangaroo", "Red kangaroo"), ("Peacock", "Indian peafowl"),
    ("Dog", "Dog"), ("Cat", "Cat"), ("Horse", "Horse"), ("Cow", "Cattle"),
    ("Pig", "Pig"), ("Sheep", "Sheep"), ("Goat", "Goat"), ("Rabbit", "Rabbit"),
    ("Chicken", "Chicken"), ("Duck", "Mallard"), ("Owl", "Barn owl"),
    ("Eagle", "Bald eagle"), ("Parrot", "Scarlet macaw"), ("Flamingo", "Greater flamingo"),
    ("Swan", "Mute swan"), ("Ostrich", "Common ostrich"), ("Gorilla", "Western gorilla"),
    ("Chimpanzee", "Chimpanzee"), ("Orangutan", "Bornean orangutan"), ("Koala", "Koala"),
    ("Sloth", "Brown-throated sloth"), ("Hippopotamus", "Hippopotamus"),
    ("Rhinoceros", "White rhinoceros"), ("Crocodile", "Nile crocodile"),
    ("Tortoise", "Galápagos tortoise"), ("Frog", "Agalychnis callidryas"),
    ("Cobra", "Indian cobra"), ("Chameleon", "Veiled chameleon"),
    ("Shark", "Great white shark"), ("Octopus", "Common octopus"),
    ("Jellyfish", "Moon jellyfish"), ("Seahorse", "Hippocampus kuda"),
    ("Butterfly", "Monarch butterfly"), ("Bee", "Western honey bee"),
    ("Ant", "Ant"), ("Camel", "Dromedary"), ("Polar bear", "Polar bear"),
    ("Wolf", "Wolf"), ("Fox", "Red fox"), ("Raccoon", "Raccoon"),
]

# Names are the clue: this deck deliberately contains no portrait assets.
# Contemporary selection checked 2026-11-03 against the official 2026 award pages:
# https://www.grammy.com/awards/68th-annual-grammy-awards-2025/
# https://www.oscars.org/oscars/ceremonies/2026
# These establish current public visibility, not a popularity ranking.
FAMOUS_PEOPLE = [
    ("Albert Einstein", "Albert Einstein"), ("Isaac Newton", "Isaac Newton"),
    ("Marie Curie", "Marie Curie"), ("Charles Darwin", "Charles Darwin"),
    ("Leonardo da Vinci", "Leonardo da Vinci"), ("William Shakespeare", "William Shakespeare"),
    ("Cleopatra", "Cleopatra"), ("Julius Caesar", "Julius Caesar"),
    ("Napoleon Bonaparte", "Napoleon"), ("Abraham Lincoln", "Abraham Lincoln"),
    ("Mahatma Gandhi", "Mahatma Gandhi"), ("Nelson Mandela", "Nelson Mandela"),
    ("Martin Luther King Jr.", "Martin Luther King Jr."), ("Mother Teresa", "Mother Teresa"),
    ("A. P. J. Abdul Kalam", "A. P. J. Abdul Kalam"), ("Neil Armstrong", "Neil Armstrong"),
    ("Amelia Earhart", "Amelia Earhart"), ("Charlie Chaplin", "Charlie Chaplin"),
    ("Michael Jackson", "Michael Jackson"), ("Queen Elizabeth II", "Elizabeth II"),
    ("Taylor Swift", "Taylor Swift"), ("Beyoncé", "Beyoncé"),
    ("Billie Eilish", "Billie Eilish"), ("Lady Gaga", "Lady Gaga"),
    ("Bad Bunny", "Bad Bunny"), ("Kendrick Lamar", "Kendrick Lamar"),
    ("Ariana Grande", "Ariana Grande"), ("Rihanna", "Rihanna"),
    ("Shakira", "Shakira"), ("Ed Sheeran", "Ed Sheeran"),
    ("Shah Rukh Khan", "Shah Rukh Khan"), ("Amitabh Bachchan", "Amitabh Bachchan"),
    ("Deepika Padukone", "Deepika Padukone"), ("Priyanka Chopra", "Priyanka Chopra"),
    ("Tom Cruise", "Tom Cruise"), ("Leonardo DiCaprio", "Leonardo DiCaprio"),
    ("Dwayne Johnson", "Dwayne Johnson"), ("Zendaya", "Zendaya"),
    ("Timothée Chalamet", "Timothée Chalamet"), ("Jackie Chan", "Jackie Chan"),
    ("Lionel Messi", "Lionel Messi"), ("Cristiano Ronaldo", "Cristiano Ronaldo"),
    ("Virat Kohli", "Virat Kohli"), ("MS Dhoni", "MS Dhoni"),
    ("Sachin Tendulkar", "Sachin Tendulkar"), ("Serena Williams", "Serena Williams"),
    ("Simone Biles", "Simone Biles"), ("Usain Bolt", "Usain Bolt"),
    ("Elon Musk", "Elon Musk"), ("Oprah Winfrey", "Oprah Winfrey"),
]

COUNTRIES = [
    "India", "Japan", "United States", "France", "Brazil", "Australia", "Egypt", "Italy",
    "South Korea", "Canada", "United Kingdom", "China", "Germany", "Spain", "Mexico",
    "Argentina", "South Africa", "New Zealand", "Russia", "Ukraine", "Sweden", "Norway",
    "Denmark", "Finland", "Switzerland", "Greece", "Portugal", "Netherlands", "Belgium",
    "Ireland", "Poland", "Austria", "Turkey", "Saudi Arabia", "United Arab Emirates",
    "Israel", "Pakistan", "Bangladesh", "Sri Lanka", "Nepal", "Bhutan", "Thailand",
    "Vietnam", "Indonesia", "Malaysia", "Singapore", "Philippines", "Kenya", "Nigeria", "Morocco",
]

HOUSEHOLD_OBJECTS = [
    ("Chair", "Chair"), ("Guitar", "Guitar"), ("Umbrella", "Umbrella"),
    ("Camera", "Camera"), ("Scissors", "Scissors"), ("Mug", "Mug"),
    ("Alarm clock", "Alarm clock"), ("Toothbrush", "Toothbrush"),
    ("Headphones", "Headphones"), ("Piano", "Piano"),
    ("Sofa", "Couch"), ("Table", "Table (furniture)"), ("Bed", "Bed"),
    ("Pillow", "Pillow"), ("Blanket", "Blanket"), ("Curtains", "Curtain"),
    ("Mirror", "Mirror"), ("Lamp", "Light fixture"), ("Fan", "Fan (machine)"),
    ("Television", "Television set"), ("Remote control", "Remote control"),
    ("Refrigerator", "Refrigerator"), ("Microwave", "Microwave oven"),
    ("Toaster", "Toaster"), ("Kettle", "Kettle"), ("Blender", "Blender (appliance)"),
    ("Washing machine", "Washing machine"), ("Vacuum cleaner", "Vacuum cleaner"),
    ("Iron", "Clothes iron"), ("Hair dryer", "Hair dryer"), ("Comb", "Comb"),
    ("Towel", "Towel"), ("Soap", "Soap"), ("Broom", "Broom"), ("Mop", "Mop"),
    ("Bucket", "Bucket"), ("Dustpan", "Dustpan"), ("Spoon", "Spoon"), ("Fork", "Fork"),
    ("Plate", "Plate (dishware)"), ("Bowl", "Bowl"), ("Frying pan", "Frying pan"),
    ("Rolling pin", "Rolling pin"), ("Colander", "Colander"), ("Whisk", "Whisk"),
    ("Corkscrew", "Corkscrew"), ("Can opener", "Can opener"), ("Stapler", "Stapler"),
    ("Flashlight", "Flashlight"), ("Key", "Key (lock)"),
]

# Accent remains in the existing data model for compatibility; the UI uses its one theme accent.
CATEGORIES = [
    ("animals", "Animals", "From familiar pets to wildlife around the world.", "#A9B8CF", ANIMALS),
    ("movies", "Movies", "Hollywood favorites, from animated classics to modern blockbusters.", "#A9B8CF", MOVIES),
    ("famous_people", "Famous People", "Historic figures and familiar names from today's world. Name clues only.", "#A9B8CF", FAMOUS_PEOPLE),
    ("animes", "Animes", "Recognizable anime series and films, old and new.", "#A9B8CF", ANIMES),
    ("countries", "Countries", "Recognize countries from their national flags.", "#A9B8CF", [(name, name) for name in COUNTRIES]),
    ("household_objects", "Household Objects", "Everyday things and a few trickier finds from home.", "#A9B8CF", HOUSEHOLD_OBJECTS),
]

# Exact flags avoid coat-of-arms or unrelated lead images on country articles.
FILE_OVERRIDES = {
    **SCREEN_FILE_OVERRIDES,
    "household_objects_blanket": ("commons.wikimedia.org", "Light-blanket-3 nat.jpg"),
    "household_objects_lamp": ("commons.wikimedia.org", "Lamp and lampshade Tiffany.jpg"),
    "household_objects_television": ("commons.wikimedia.org", "LCD generic tv.jpg"),
    "household_objects_refrigerator": ("commons.wikimedia.org", "A Samsung Refrigerator.jpg"),
    "household_objects_bed": ("commons.wikimedia.org", "Bed in hotel room 5.jpg"),
    "household_objects_table": ("commons.wikimedia.org", "Kitchen table.jpg"),
    **{"countries_" + re.sub(r"[^a-z0-9]+", "_", country.lower()).strip("_"):
       ("commons.wikimedia.org", "Flag of " + ("the " if country in {"United States", "United Kingdom", "United Arab Emirates", "Netherlands", "Philippines"} else "") + country + ".svg")
       for country in COUNTRIES},
    "famous_people_cover": ("commons.wikimedia.org", "Astatic crystal mic.jpg"),
}

# Verified against the original file-description pages, not inferred authors.
# Some Commons author templates include contact details and sponsor navigation.
CREDIT_OVERRIDES = {
    "Flag of Israel.svg": "Israel Belkind and Fanny Abramovitch (original design)",
    "Giraffe Mikumi National Park.jpg": "Muhammad Mahdi Karim",
    "Red kangaroo - melbourne zoo.jpg": "Fir0002/Flagstaffotos",
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
    direct = SCREEN_DIRECT_SOURCES.get(word_id)
    override = None if direct else FILE_OVERRIDES.get(word_id)
    if not refresh and word_id in cached and max(cached[word_id]["width"], cached[word_id]["height"]) <= 720 and destination.is_file() and (not override or cached[word_id]["sourceTitle"] == override[1]) and (not direct or cached[word_id]["downloadUrl"] == direct["downloadUrl"]) and hashlib.sha256(destination.read_bytes()).hexdigest() == cached[word_id]["sha256"]:
        return cached[word_id]

    if direct:
        raw = get(direct["downloadUrl"]).content
        with Image.open(BytesIO(raw)) as source:
            photo = ImageOps.exif_transpose(source).convert("RGB")
            photo.thumbnail((720, 720), Image.Resampling.LANCZOS)
            if min(photo.size) < 100:
                raise RuntimeError(f"Photo too small for {article}: {photo.size}")
            destination.parent.mkdir(parents=True, exist_ok=True)
            photo.save(destination, "WEBP", quality=84, method=6)
            width, height = photo.size
        return {"id": word_id, "text": label, "imagePath": local_path, **direct,
                "articleUrl": "https://en.wikipedia.org/wiki/" + quote(article.replace(" ", "_")),
                "width": width, "height": height, "bytes": destination.stat().st_size,
                "sha256": hashlib.sha256(destination.read_bytes()).hexdigest()}

    data = api("en.wikipedia.org", titles=article, redirects=1, prop="pageimages",
               piprop="thumbnail|name|original", pithumbsize=720, pilicense="any")
    page = next(iter(data["query"]["pages"].values()))
    if not override and not page.get("pageimage"):
        raise RuntimeError(f"No verified lead image for {article}")
    image_title = "File:" + (override[1] if override else page["pageimage"])
    image_host = override[0] if override else ("en.wikipedia.org" if "/wikipedia/en/" in page["original"]["source"] else "commons.wikimedia.org")
    image_data = api(image_host, titles=image_title, prop="imageinfo", iiprop="url|extmetadata", iiurlwidth=720)
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
        photo.thumbnail((720, 720), Image.Resampling.LANCZOS)
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


def reencode(cached: dict) -> dict:
    """Re-encode only files already present; this path never contacts a source."""
    for photo in cached.values():
        destination = (ASSETS / photo["imagePath"]).resolve()
        if not destination.is_relative_to((ASSETS / "images").resolve()):
            raise RuntimeError(f"Picture path is outside the bundled image directory: {photo['imagePath']}")
        if not destination.is_file():
            raise RuntimeError(f"Missing bundled picture: {destination}")
        with Image.open(destination) as source:
            image = ImageOps.exif_transpose(source).convert("RGB")
            # Do not enlarge low-resolution source artwork.
            if source.format != "WEBP" or max(image.size) > 720:
                image.thumbnail((720, 720), Image.Resampling.LANCZOS)
                image.save(destination, "WEBP", quality=84, method=6)
            photo["width"], photo["height"] = image.size
        photo["bytes"] = destination.stat().st_size
        photo["sha256"] = hashlib.sha256(destination.read_bytes()).hexdigest()
    return cached


def write_manifest(results: dict, bundle: bool = True, retrieved_on: str | None = None):
    MANIFEST.parent.mkdir(parents=True, exist_ok=True)
    document = json.dumps({"version": 1, "retrievedOn": retrieved_on or str(date.today()),
                           "images": [results[key] for key in sorted(results)]},
                          indent=2, ensure_ascii=False) + "\n"
    MANIFEST.write_text(document, encoding="utf-8")
    if bundle:
        (ASSETS / "photo_sources.json").write_text(document, encoding="utf-8")


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--refresh", action="store_true", help="Refresh previously imported photos")
    parser.add_argument("--prune", action="store_true", help="Remove retired pictures after the complete catalog is written")
    parser.add_argument("--reencode-only", action="store_true", help="Offline: re-encode bundled pictures to WebP, max 720px, and update checksums")
    args = parser.parse_args()
    cached = {}
    retrieved_on = None
    if MANIFEST.exists():
        existing = json.loads(MANIFEST.read_text(encoding="utf-8"))
        retrieved_on = existing.get("retrievedOn")
        cached = {image["id"]: image for image in existing["images"]}
    if args.reencode_only:
        if args.refresh or args.prune:
            parser.error("--reencode-only cannot be combined with --refresh or --prune")
        if not cached:
            parser.error("--reencode-only requires an existing nonempty photo source manifest")
        write_manifest(reencode(cached), retrieved_on=retrieved_on)
        print(f"Re-encoded {len(cached)} bundled pictures offline (maximum long edge: 720px)")
        return
    results = {}
    errors = []
    jobs = [(category[0], label, article) for category in CATEGORIES
            if category[0] != "famous_people" for label, article in category[4]]
    jobs.append(("famous_people", "Cover", "Microphone"))
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
    # Preserve completed imports so an interrupted/failed import resumes.
    write_manifest(results, bundle=not errors)
    if errors:
        raise SystemExit(f"{len(errors)} photos failed. Fix exact article choices or connectivity and rerun.")
    categories = []
    public_fields = ("id", "text", "imagePath", "sourceTitle", "sourceUrl", "credit")
    for category_id, title, description, accent, prompts in CATEGORIES:
        if category_id == "famous_people":
            words = [{"id": category_id + "_" + slug(label), "text": label, "imagePath": "",
                      "sourceTitle": article,
                      "sourceUrl": "https://en.wikipedia.org/wiki/" + quote(article.replace(" ", "_")),
                      "credit": ""} for label, article in prompts]
            cover = results["famous_people_cover"]["imagePath"]
        else:
            words = [{key: results[category_id + "_" + slug(label)][key] for key in public_fields} for label, _ in prompts]
            cover = words[0]["imagePath"]
        categories.append({"id": category_id, "title": title, "description": description,
                           "imagePath": cover, "accent": accent, "words": words})
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
