# Charades artwork

The launcher mark is one upright clue card with a geometric **C**: a thin muted-blue outline and white monogram on near-black. The 48×60 card uses a 12-unit corner radius and a 3-unit outline on a 108×108 canvas. It replaces the rotated card fan and play triangle with a quieter, more recognizable initial.

`res/drawable/ic_launcher_foreground.xml` owns the Android geometry; `charades-mark.svg` mirrors it for reuse. Legacy and adaptive launcher resources share that foreground, so normal/round versions do not maintain separate paths. The adaptive mark sits within the central safe zone, and its same alpha shape supplies Android's themed monochrome layer. See the [Android adaptive icon guide](https://developer.android.com/develop/ui/compose/system/icon_design_adaptive) for layer and safe-zone rules. The operating system supplies the launcher mask; no shadows, gradients, or generated raster artwork are used.

The generated game-night landing illustration was removed in the 3 November redesign. The home screen presents only the game title, brief instructions, and deck covers.

Bundled typography pairs [Space Grotesk](https://github.com/google/fonts/tree/main/ofl/spacegrotesk) for headings with [Manrope](https://github.com/google/fonts/tree/main/ofl/manrope) for body text and labels. Space Grotesk uses weight 600; Manrope uses weights 400 and 600. Both variable TTFs are authentic Google Fonts distributions, stored locally with their SIL Open Font Licenses in `spacegrotesk-OFL.txt`, `manrope-OFL.txt`, and the bundled `font_licenses.txt`. Gameplay media provenance is separate in `docs/photo_sources.json`.
