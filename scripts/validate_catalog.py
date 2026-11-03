"""Validate the complete offline catalog without contacting any web service."""
from __future__ import annotations

from collections import Counter
import hashlib
import json
from pathlib import Path, PurePosixPath
import sys

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "app/src/main/assets"
EXPECTED_CATEGORIES = {
    "animals": "Animals",
    "movies": "Movies",
    "famous_people": "Famous People",
    "animes": "Animes",
    "countries": "Countries",
    "household_objects": "Household Objects",
}


def webp_size(data: bytes) -> tuple[int, int]:
    """Read WebP dimensions using its standard RIFF chunk headers, offline."""
    offset = 12
    while offset + 8 <= len(data):
        kind = data[offset:offset + 4]
        length = int.from_bytes(data[offset + 4:offset + 8], "little")
        payload = data[offset + 8:offset + 8 + length]
        if kind == b"VP8X" and len(payload) >= 10:
            return 1 + int.from_bytes(payload[4:7], "little"), 1 + int.from_bytes(payload[7:10], "little")
        if kind == b"VP8 " and len(payload) >= 10 and payload[3:6] == b"\x9d\x01\x2a":
            return int.from_bytes(payload[6:8], "little") & 0x3fff, int.from_bytes(payload[8:10], "little") & 0x3fff
        if kind == b"VP8L" and len(payload) >= 5 and payload[0] == 0x2f:
            packed = int.from_bytes(payload[1:5], "little")
            return 1 + (packed & 0x3fff), 1 + ((packed >> 14) & 0x3fff)
        offset += 8 + length + length % 2
    raise ValueError("WebP contains no readable image dimensions")


def validate() -> list[str]:
    errors: list[str] = []
    catalog = json.loads((ASSETS / "categories/bundled_categories.json").read_text(encoding="utf-8"))
    manifest_file = ROOT / "docs/photo_sources.json"
    manifest = json.loads(manifest_file.read_text(encoding="utf-8"))
    bundled_manifest = ASSETS / "photo_sources.json"
    if not bundled_manifest.is_file() or bundled_manifest.read_bytes() != manifest_file.read_bytes():
        errors.append("Bundled photo_sources.json must exactly match the canonical source manifest")
    photos = {photo["id"]: photo for photo in manifest["images"]}
    if len(photos) != len(manifest["images"]):
        errors.append("Source manifest contains duplicate image IDs")
    categories = catalog.get("categories", [])
    if catalog.get("version") != 1 or not categories:
        errors.append("A version-1 catalog with at least one category is required")
    category_ids: list[str] = []
    word_ids: list[str] = []
    used_paths: set[str] = set()

    def check_image(path: str, owner: str):
        relative = PurePosixPath(path)
        if not path.startswith("images/") or ".." in relative.parts or ":" in path or "\\" in path:
            errors.append(f"{owner}: image must be a safe local images/ path: {path}")
            return False
        target = ASSETS / relative
        if not target.is_file() or target.stat().st_size == 0:
            errors.append(f"{owner}: bundled image is missing or empty: {path}")
            return False
        used_paths.add(path)
        return True

    for category in categories:
        category_id = category["id"]
        category_ids.append(category_id)
        if category.get("title") != EXPECTED_CATEGORIES.get(category_id):
            errors.append(f"{category_id}: category name must match the six-deck product catalog")
        for key in ("title", "description", "accent"):
            if not category.get(key):
                errors.append(f"{category_id}: missing {key}")
        check_image(category.get("imagePath", ""), category_id)
        words = category.get("words", [])
        if len(words) != 50:
            errors.append(f"{category_id}: expected exactly 50 playable prompts")
        labels = [word.get("text", "").casefold() for word in words]
        if len(set(labels)) != len(labels):
            errors.append(f"{category_id}: duplicate clue labels")
        for word in words:
            word_id = word["id"]
            word_ids.append(word_id)
            is_text_clue = category_id == "famous_people"
            if is_text_clue:
                if word.get("imagePath") != "":
                    errors.append(f"{word_id}: Famous People must be name-only, with an empty imagePath")
            else:
                check_image(word.get("imagePath", ""), word_id)
            for key in ("text", "sourceTitle", "sourceUrl"):
                if not word.get(key):
                    errors.append(f"{word_id}: missing {key}")
            if not word.get("sourceUrl", "").startswith("https://"):
                errors.append(f"{word_id}: sourceUrl must identify its HTTPS source page")
            credit = word.get("credit", "")
            if credit != " ".join(credit.split()) or len(credit) > 180:
                errors.append(f"{word_id}: credit must be a concise single line without page navigation")
            if not is_text_clue and not credit:
                errors.append(f"{word_id}: missing photo credit")
            if is_text_clue and credit:
                errors.append(f"{word_id}: name-only clues cannot carry photo attribution")
            if not is_text_clue:
                photo = photos.get(word_id)
                if not photo:
                    errors.append(f"{word_id}: missing source manifest entry")
                    continue
                for key in ("imagePath", "sourceTitle", "sourceUrl", "credit"):
                    if photo.get(key) != word.get(key):
                        errors.append(f"{word_id}: {key} differs from its source manifest")

    # The neutral Famous People hero is credited as a cover, never as a portrait clue.
    expected_photo_ids = {word["id"] for category in categories if category["id"] != "famous_people"
                          for word in category.get("words", [])} | {"famous_people_cover"}
    catalog_paths = used_paths.copy()
    famous = next((category for category in categories if category["id"] == "famous_people"), {})
    if famous.get("imagePath") != photos.get("famous_people_cover", {}).get("imagePath"):
        errors.append("Famous People hero must use its separately sourced neutral cover")
    for photo_id, photo in photos.items():
        path = photo.get("imagePath", "")
        if not check_image(path, photo_id):
            continue
        target = ASSETS / path
        if not target.is_file():
            continue
        data = target.read_bytes()
        if target.suffix != ".webp" or data[:4] != b"RIFF" or data[8:12] != b"WEBP":
            errors.append(f"{photo_id}: bundled picture must be WebP")
        if webp_size(data) != (photo["width"], photo["height"]):
            errors.append(f"{photo_id}: actual dimensions differ from source manifest")
        if hashlib.sha256(data).hexdigest() != photo.get("sha256"):
            errors.append(f"{photo_id}: photo differs from its recorded checksum")
        if len(data) != photo.get("bytes"):
            errors.append(f"{photo_id}: photo size differs from source manifest")
        if min(photo["width"], photo["height"]) < 100 or max(photo["width"], photo["height"]) > 720:
            errors.append(f"{photo_id}: photo is outside the bundled resolution bounds (100–720px)")
        if not photo.get("credit") or not photo.get("sourceUrl", "").startswith("https://"):
            errors.append(f"{photo_id}: photo requires an actual source and attribution")
        if path not in catalog_paths:
            errors.append(f"{photo_id}: unused photo source record")

    for kind, ids in (("category", category_ids), ("word", word_ids)):
        errors.extend(f"Duplicate {kind} id: {item}" for item, count in Counter(ids).items() if count > 1)
    if set(category_ids) != set(EXPECTED_CATEGORIES):
        errors.append("Catalog must contain exactly the six approved categories")
    orphaned = {str(path.relative_to(ASSETS)).replace("\\", "/") for path in (ASSETS / "images").glob("*")} - used_paths
    if orphaned:
        errors.append("Unused image files: " + ", ".join(sorted(orphaned)))
    if set(photos) != expected_photo_ids:
        errors.append("Source manifest must match the 250 pictured clues and one neutral cover")
    if not errors:
        print(f"PASS: {len(categories)} sections, {len(word_ids)} prompts, {len(used_paths)} local photos")
        print(f"Bundled photos: {sum((ASSETS / path).stat().st_size for path in used_paths) / 1048576:.2f} MiB")
    return errors


if __name__ == "__main__":
    try:
        failures = validate()
    except (KeyError, OSError, ValueError) as error:
        failures = [str(error)]
    for failure in failures:
        print(f"FAIL: {failure}", file=sys.stderr)
    raise SystemExit(bool(failures))
