# Specification reconciliation

Reconciled 3 November 2026. This records how the original game brief, later user corrections, and temporary scaffold plans resolve into [PRODUCT.md](../PRODUCT.md).

## Original core retained

Human-led 20 Questions/Charades, landscape clues, a three-second countdown before each reveal, manual nonnegative guess count, and normal Next/X/Back navigation remain the core.

A section-detail encounter creates the shuffled order used by its session. Countdown timing continues across lock/backgrounding. An active clue keeps the screen awake until three minutes without interaction, after which normal Android timeout can apply.

The app is personal, nonprofit, always dark, and retains the existing GPL license.

## Later user decisions

**Installation:** all sections/images ship in the first APK. This replaces remote checks, database/download gates, and subsequent content installation.

**Aesthetic direction (3 November):** near-black #0E0E10, white titles, muted grey text, flat surfaces, one restrained accent, two font weights, one radius, and shared spacing/type tokens. This replaces navy/coral, playful lettering, generated landing artwork, gradients, shadows, and collages. Images use one fixed 4:3 Crop wrapper.

**Catalog (3 November):** six decks of 50 entries each. Anime Characters is removed; Animes remains. Movies is Hollywood-only. Famous People mixes historical and current recognizable names and has no clue photographs. The deck data model and game logic remain intact.

**Documentation:** maintain durable agent/product context, ship readable privacy/terms/license, and keep “How to install” near the README top.

**Media:** acquire accurate clue images and retain source identity. Re-encode local images as WebP with at most 720px on the long edge, without enlarging smaller sources. Generated landing branding is removed; fabricated factual clue identities remain prohibited.

## Draft assumptions removed

Timed rounds, correct/skip scoring, score results, sounds/haptic preferences, a settings repository, and eight differently named decks were temporary implementation assumptions. Their classes/tests do not make them current requirements.

Room/Retrofit/OkHttp stubs likewise do not justify a database or network. The complete offline APK decision takes precedence.

The six sections are the current curated release, not a permanent ban on additional packaged content.

## Names and unresolved inputs

Public name: **Charades**. Working title: **Free Charades**. Marketing line: **“The offline game of 20 Questions & Charades.”** Retain requested section wording, including **Animes**.

No donation destination/beneficiary is supplied. Hide the optional browser action until configured. Do not assume a release URL, signing identity, store publication, or successful device test.

## Future work

Explicit new user decisions can change the product. Update the relevant product/design/interaction docs, implementation, and affected policies together. Do not silently restore obsolete draft behavior because code once existed.
