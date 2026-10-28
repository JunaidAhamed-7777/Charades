"""Validate the complete offline catalog without contacting any web service."""
from __future__ import annotations

from collections import Counter
import hashlib
import json
from pathlib import Path, PurePosixPath
import sys

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "app/src/main/assets"


def validate() -> list[str]:
    errors: list[str] = []
    catalog = json.loads((ASSETS / "categories/bundled_categories.json").read_text(encoding="utf-8"))
    manifest = json.loads((ROOT / "docs/photo_sources.json").read_text(encoding="utf-8"))
    photos = {photo["id"]: photo for photo in manifest["images"]}
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
            return
        target = ASSETS / relative
        if not target.is_file() or target.stat().st_size == 0:
            errors.append(f"{owner}: bundled image is missing or empty: {path}")
        used_paths.add(path)

    for category in categories:
        category_id = category["id"]
        category_ids.append(category_id)
        for key in ("title", "description", "accent"):
            if not category.get(key):
                errors.append(f"{category_id}: missing {key}")
        check_image(category.get("imagePath", ""), category_id)
        words = category.get("words", [])
        if len(words) < 8:
            errors.append(f"{category_id}: expected at least 8 playable prompts")
        for word in words:
            word_id = word["id"]
            word_ids.append(word_id)
            check_image(word.get("imagePath", ""), word_id)
            for key in ("text", "sourceTitle", "sourceUrl", "credit"):
                if not word.get(key):
                    errors.append(f"{word_id}: missing {key}")
            if not word.get("sourceUrl", "").startswith("https://"):
                errors.append(f"{word_id}: sourceUrl must identify its HTTPS source page")
            photo = photos.get(word_id)
            if not photo:
                errors.append(f"{word_id}: missing source manifest entry")
                continue
            target = ASSETS / word["imagePath"]
            if target.is_file():
                if hashlib.sha256(target.read_bytes()).hexdigest() != photo["sha256"]:
                    errors.append(f"{word_id}: photo differs from its recorded checksum")
                if target.stat().st_size != photo["bytes"]:
                    errors.append(f"{word_id}: photo size differs from source manifest")
            if min(photo["width"], photo["height"]) < 100 or max(photo["width"], photo["height"]) > 960:
                errors.append(f"{word_id}: photo is outside the bundled resolution bounds")

    for kind, ids in (("category", category_ids), ("word", word_ids)):
        errors.extend(f"Duplicate {kind} id: {item}" for item, count in Counter(ids).items() if count > 1)
    orphaned = {str(path.relative_to(ASSETS)).replace("\\", "/") for path in (ASSETS / "images").glob("*")} - used_paths
    if orphaned:
        errors.append("Unused image files: " + ", ".join(sorted(orphaned)))
    if set(photos) != set(word_ids):
        errors.append("Source manifest and catalog prompt IDs do not match exactly")
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
