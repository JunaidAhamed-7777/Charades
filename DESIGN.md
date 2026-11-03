---
version: alpha
colors:
  primary: "#8FA7E8"
  background: "#0E0E10"
  surface: "#19191D"
  raised: "#232328"
  foreground: "#FFFFFF"
  muted: "#9F9FA5"
typography:
  display:
    fontFamily: "Space Grotesk"
  title:
    fontFamily: "Space Grotesk"
  body:
    fontFamily: "Manrope"
  label:
    fontFamily: "Manrope"
rounded:
  card: "12px"
spacing:
  xs: "4px"
  sm: "8px"
  md: "12px"
  lg: "16px"
  xl: "24px"
  xxl: "32px"
  page: "24px"
  group: "16px"
components:
  primary-button:
    backgroundColor: "{colors.raised}"
    textColor: "{colors.foreground}"
  body-text:
    textColor: "{colors.foreground}"
  supporting-text:
    textColor: "{colors.muted}"
---

# Charades visual identity

## Overview
An understated library of picture cards for a group gathered around one phone. The 3 November brief replaces the playful navy/coral identity and generated landing illustration with a quiet near-black interface. The signature is a consistent set of single-image deck covers, with restrained small-caps labels. This is a native game tool: recognition and a clear next action matter more than decoration.

## Colors
Runtime ownership (Model B): `ui/theme/Theme.kt` defines the entire color scheme; components consume Material semantic roles. This document mirrors that source. Background is #0E0E10, card surfaces are one step above it, titles are pure white, and supporting text is approximately 60% white. One muted blue accent is reserved for actions and focus. Primary buttons use a dark surface rather than a bright fill. Keep the app dark, with no gradients, drop shadows, or extra deck-specific accent colors.

## Typography
`ui/theme/Typography.kt` owns four roles: display, title, body, and label, mapped into Material typography. Bundled Space Grotesk at weight 600 gives display/title roles a sharper identity; Manrope at weights 400/600 keeps body/label roles readable and quiet. The complete scale remains 40/48sp, 24/32sp, 16/24sp, and 12/16sp respectively, with only two weights. No screen-local font sizes or weight overrides. Screen headings use the same title style and header position. Large clue names wrap and remain scrollable when required. Deck labels use uppercase text with the shared label style; accessibility preserves the ordinary deck name.

The landing page leads with the display-style “Choose a deck” and groups the marketing line directly below it. A quiet, centered “Made By Junaid” credit and compact Privacy policy action close the scrolling library. Avoid decorative hero copy or oversized footer controls competing with the playable decks.

## Layout
`ui/theme/Spacing.kt` owns the 4/8/12/16/24/32dp spacing scale. Shared content columns use 24dp horizontal padding and a consistent maximum reading width; all screen headers share the same alignment. Decks form identical 4:3 cards with one accurate hero image. The grid's minimum width grows with system font scale so larger text can switch to fewer, wider columns. Images use the same fixed 4:3 Crop wrapper, placeholder surface, and corner radius. Reframing must preserve subject recognition. Famous People gameplay is name-only. Landscape gameplay retains the counter and Next controls; compact screens can scroll content. Apply system safe insets and support large text.

## Elevation & Depth
Flat surfaces only. A uniform dark scrim improves cover-title contrast without a gradient. Use separation through spacing and surface tone rather than shadows. No generated landing art, collage, glow, or ornamental texture.

## Shapes
`ui/theme/Shape.kt` owns one 12dp radius for cards, images, and controls. Buttons and icon actions retain at least 48dp touch targets. Icon geometry is separate from component corner radii.

## Components
Shared primitives live in `ui/components/BrandComponents.kt`: the aligned screen/content shell, `ScreenHeader`, `AppCard`, `AppImage`, `PrimaryAction`, `SecondaryAction`, `AppIcon`, and `StatusContent`. Every image passes through `AppImage`; every card uses `AppCard`. Loading, errors, retries, and exhausted decks use these owners. Navigation and timing remain in their existing owners, outside the visual system.

Token path: `Theme.kt` / `Typography.kt` / `Shape.kt` / `Spacing.kt` → Material theme and shared primitives → screens. Android XML startup/launcher colors mirror the dark background; no screen consumes raw palette literals. See [UX-CONTRACT.md](UX-CONTRACT.md) for interaction rules.

## Do's and Don'ts
Use truthful local media and short instructions. Keep all six decks immediately playable offline and preserve the existing game rules. Do not restore playful branding, collages, gradients, shadows, bright button fills, a second spacing scale, or duplicated controls. Keep 720px maximum-long-edge WebP assets and accurate provenance. Crop deliberately; do not use unrelated substitutes or fabricated likenesses.

