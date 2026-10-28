---
version: alpha
colors:
  primary: "#244BE8"
  coral: "#FF806E"
  background: "#10182D"
  surface: "#1B2742"
  foreground: "#F0F2FC"
  muted: "#B6C1D7"
typography:
  display:
    fontFamily: "Fredoka"
  body:
    fontFamily: "Nunito Sans"
rounded:
  card: "16px"
  control: "14px"
spacing:
  page: "24px"
  group: "16px"
components:
  primary-button:
    backgroundColor: "{colors.primary}"
    textColor: "#FFFFFF"
  guess-count:
    textColor: "{colors.coral}"
  body-text:
    textColor: "{colors.foreground}"
  supporting-text:
    textColor: "{colors.muted}"
---

# Charades visual identity

## Overview
A box of picture cards brought to a shared phone: vivid blue, warm coral, rich midnight surfaces, tactile original artwork, and playful lettering. The user chose blue/coral and playful lettering, then explicitly freed the aesthetic from the stage-zero draft. The old grey/teal sample colors and exact layout are historical references, not requirements.

## Colors
Runtime ownership (Model B): `ui/theme/Color.kt` owns palette constants; `Theme.kt` maps semantic Material roles; shared components and screens consume those roles. This file mirrors the accepted values and rationale. Primary actions use cobalt with white. Coral carries playful accents and the manual count against navy. Body text is near white; secondary text is a legible pale blue-grey. The app currently uses one deliberate dark scheme; avoid introducing an automatic light theme as an incidental library default.

## Typography
`Type.kt` owns the complete Material type scale. Bundled Fredoka expresses the brand in display/headline roles; Nunito Sans keeps body text, controls, and credits readable. All sizes are sp. Longer answers wrap; utility copy remains quieter. Fonts and licenses ship with the project; no runtime font service.

## Layout
Phone-first library: 24dp margins, 14dp photo-grid gutters, adaptive columns with a 155dp minimum card width. Reading screens cap content width at 640–720dp. Game switches to landscape and gives the image the largest region; the answer anchors it below while a narrow rail carries the manual counter and Next. System bars and cutouts receive safe insets.

## Elevation & Depth
Quiet flat native surfaces support the photographs. A bottom image scrim makes library titles readable. Material depth lives in the authored game-night illustration; avoid duplicating it through arbitrary UI shadows or ornamental glass.

## Shapes
16dp photo cards, 14dp primary controls, 20dp hero artwork. The logo consists of two overlapping rounded picture cards and a white play triangle. Keep all targets at least 48dp. Material controls carry familiar Android interactions.

## Components
Canonical visual primitives: `BrandMark`, `AppIcon`, `ScreenHeader`, `PrimaryAction`, and `StatusContent` in `ui/components/BrandComponents.kt`. The icon system is authored, consistent rounded strokes; no emoji icons. Android launcher vectors and the reusable SVG mark use the same geometry. See `docs/branding/README.md` for artwork provenance.

## Do's and Don'ts
Show accurate sourced photos, clear game controls, and concise human instructions. Keep the deck library immediately usable offline. Do not introduce a paid/downloaded-state treatment, image placeholders as finished content, a timed score system, or marketing decoration around the secret picture. Treat gameplay background from the first draft as product context and verify durable changes against the current user instructions.
