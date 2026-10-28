# Bundled content

All sections and media ship in the base APK. Developers prepare images before a build; players never connect to a catalog service or install another content pack.

The current set has seven sections with ten clues each: Animals, Movies, Famous People, Animes, Anime Characters, Countries, and Household Objects. The 70 prepared images occupy about 3.4 MiB. Countries use accurate national flags; film/anime clues use recognizable published posters or artwork. Provenance is in [photo_sources.json](photo_sources.json).

## Catalog

Canonical file: `app/src/main/assets/categories/bundled_categories.json`.

```json
{
  "version": 1,
  "categories": [
    {
      "id": "animals",
      "title": "Animals",
      "description": "Recognizable animals to guess or act out.",
      "imagePath": "images/animals_lion.webp",
      "accent": "#EFA06B",
      "words": [
        {
          "id": "animals_lion",
          "text": "Lion",
          "imagePath": "images/animals_lion.webp",
          "sourceTitle": "Lion",
          "sourceUrl": "https://en.wikipedia.org/wiki/Lion",
          "credit": "Original photo credit as recorded at the source"
        }
      ]
    }
  ]
}
```

This is a structural example. IDs are stable and unique. Image paths are relative to `assets/`, resolve inside it, and never contain HTTP URLs. Category covers use the same local format.

Current category IDs are `animals`, `movies`, `famous_people`, `animes`, `anime_characters`, `countries`, and `household_objects`. The current validator enforces that taxonomy and at least ten clues per section. Future additions require a matching product/catalog/validator change and ship in another complete APK.

## Content workflow

1. Pick a specific, recognizable subject appropriate to its section.
2. Find an accurate source image. Verify the named person, work, character, animal, country/flag, or object against the source page. Do not substitute generated identities, cosplay for the fictional character, or unrelated stock imagery.
3. Prepare the image under `app/src/main/assets/images/`, optimizing resolution and WebP size. Preserve proportions and check landscape center-crop framing.
4. Update the catalog and source title/URL/credit. Keep `docs/photo_sources.json` aligned with the original download URL and optimized-file hash.
5. Run `python scripts/validate_catalog.py`, visually review the image, rebuild, and verify the cover/reveal in airplane mode.

`scripts/import_photos.py` is developer-only acquisition tooling requiring `requests` and Pillow. Inspect its source list/options before running. Use `--prune` only after a complete successful import to remove retired images. It may use internet; app startup, gameplay, and normal Gradle builds must not invoke it.

The validator uses Python's standard library and works offline. Also review actual images: valid JSON and hashes cannot establish that a photograph identifies the right subject.

## Acceptance

All sections are immediately playable. Every image exists, decodes, and accurately depicts its clue. Reject duplicate IDs, missing files, remote image paths, stale provenance, and invented attribution. Keep labels/source text readable in landscape.

Schema changes need a matching parser and docs change. Content updates require a complete APK, with no runtime update check.
