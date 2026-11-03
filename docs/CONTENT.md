# Bundled content

All sections and media ship in the base APK. Developers prepare images before a build; players never connect to a catalog service or install another content pack.

The current set has six decks with 50 clues each: Animals, Movies, Famous People, Animes, Countries, and Household Objects. Anime Characters is retired. Movies contains Hollywood films only. Famous People combines recognizable historical and current public figures and reveals names alone. Countries use accurate national flags; film/anime clues use recognizable published posters or artwork. Provenance is in [photo_sources.json](photo_sources.json).

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

Current category IDs are `animals`, `movies`, `famous_people`, `animes`, `countries`, and `household_objects`. The validator enforces that taxonomy and 50 clues per deck. Every cover has one local hero image. Famous People clue records keep the existing schema with `imagePath: ""`; their source title/URL identify the person, with no photo attribution. Empty paths are rejected for every other clue and for every cover. Future additions require a matching product/catalog/validator change and ship in another complete APK.

## Content workflow

1. Pick a specific, recognizable subject appropriate to its section.
2. Find an accurate source image. Verify the named person, work, character, animal, country/flag, or object against the source page. Do not substitute generated identities, cosplay for the fictional character, or unrelated stock imagery.
3. Prepare the image under `app/src/main/assets/images/` as WebP, quality 84, with a maximum 720px long edge. Preserve proportions without enlarging low-resolution sources, and check the fixed 4:3 Crop framing in covers and landscape reveals. Keep the subject recognizable.
4. Update the catalog and source title/URL/credit. Keep `docs/photo_sources.json` aligned with the original download URL and optimized-file hash.
5. Run `python scripts/validate_catalog.py`, visually review the image, rebuild, and verify the cover/reveal in airplane mode.

`scripts/import_photos.py` is developer-only acquisition tooling requiring `requests` and Pillow. Inspect its source list/options before running. Use `--prune` only after a complete successful import to remove retired images. It may use internet; app startup, gameplay, and normal Gradle builds must not invoke it.

Run `python scripts/import_photos.py --reencode-only` to normalize existing images entirely offline and refresh provenance dimensions, byte counts, and SHA-256 hashes. Re-run `python scripts/validate_catalog.py` afterward. Acquisition lists are developer input; runtime availability comes exclusively from the bundled JSON catalog.

The validator uses Python's standard library and works offline. Also review actual images: valid JSON and hashes cannot establish that a photograph identifies the right subject.

## Acceptance

All decks are immediately playable with 50 unique entries each. Every referenced image exists, decodes, is WebP within the resolution limit, and accurately depicts its clue (or a truthfully labeled deck-cover subject). Reject duplicate IDs, missing files, remote image paths, stale provenance, and invented attribution. Famous People must remain name-only. Keep labels/source text readable in landscape.

Schema changes need a matching parser and docs change. Content updates require a complete APK, with no runtime update check.
