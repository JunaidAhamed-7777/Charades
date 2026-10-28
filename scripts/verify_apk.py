"""Verify that an installable APK really contains the entire offline library."""
from __future__ import annotations

import argparse
import json
from pathlib import Path
import zipfile


def main() -> None:
    root = Path(__file__).resolve().parents[1]
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("apk", nargs="?", type=Path,
                        default=root / "app/build/outputs/apk/debug/app-debug.apk")
    args = parser.parse_args()
    if not args.apk.is_file():
        parser.error(f"APK not found: {args.apk}")
    with zipfile.ZipFile(args.apk) as apk:
        names = set(apk.namelist())
        catalog_path = "assets/categories/bundled_categories.json"
        catalog_bytes = apk.read(catalog_path)
        source_assets = root / "app/src/main/assets"
        if catalog_bytes != (source_assets / "categories/bundled_categories.json").read_bytes():
            raise SystemExit("FAIL: APK catalog differs from the current source catalog. Rebuild it.")
        catalog = json.loads(catalog_bytes)
        paths = set()
        prompts = 0
        for category in catalog["categories"]:
            paths.add(category["imagePath"])
            for word in category["words"]:
                paths.add(word["imagePath"])
                prompts += 1
        for path in sorted(paths):
            if not path.startswith("images/") or ".." in Path(path).parts:
                raise SystemExit(f"FAIL: Image is not a safe bundled path: {path}")
            asset_path = f"assets/{path}"
            if asset_path not in names or apk.read(asset_path) != (source_assets / path).read_bytes():
                raise SystemExit(f"FAIL: Missing or stale photo in APK: {path}")
        for name in ("privacy_policy.txt", "terms_of_service.txt", "license.txt"):
            if apk.read(f"assets/{name}") != (source_assets / name).read_bytes():
                raise SystemExit(f"FAIL: Bundled document is stale: {name}")
        for font in ("fredoka.ttf", "nunito_sans.ttf"):
            if f"res/font/{font}" not in names:
                raise SystemExit(f"FAIL: Font missing from APK: {font}")
        manifest = apk.read("AndroidManifest.xml")
        for permission in ("android.permission.INTERNET", "android.permission.ACCESS_NETWORK_STATE"):
            if permission.encode() in manifest or permission.encode("utf-16-le") in manifest:
                raise SystemExit(f"FAIL: Unexpected network permission: {permission}")
    print(f"PASS: APK contains {len(catalog['categories'])} decks, {prompts} prompts, "
          f"{len(paths)} current images, local policies and fonts; no network permissions.")
    print(f"Complete APK: {args.apk.resolve()} ({args.apk.stat().st_size / 1024 / 1024:.2f} MiB)")


if __name__ == "__main__":
    main()
