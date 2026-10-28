# Specification reconciliation

Reconciled 28 October 2026. This records how the original game brief, later user corrections, and temporary scaffold plans resolve into [PRODUCT.md](../PRODUCT.md).

## Original core retained

Human-led 20 Questions/Charades, the seven-section v1 set, landscape clues, a three-second countdown before each reveal, manual nonnegative guess count, and normal Next/X/Back navigation remain the core.

A section-detail encounter creates the shuffled order used by its session. Countdown timing continues across lock/backgrounding. An active clue keeps the screen awake until three minutes without interaction, after which normal Android timeout can apply.

The app is personal, nonprofit, always dark, and retains the existing GPL license.

## Later user decisions

**Installation:** all sections/images ship in the first APK. This replaces remote checks, database/download gates, and subsequent content installation.

**Aesthetic direction:** prioritize a sleek overall design with the approved rich dark navy, blue/coral palette, playful type, original artwork, and logo. Earlier teal, exact color values, sizes, and stage-zero placement hints are superseded when they conflict with the coherent design.

**Documentation:** maintain durable agent/product context, ship readable privacy/terms/license, and keep “How to install” near the README top.

**Media:** acquire accurate clue images and retain source identity. Generated branding is appropriate; fabricated factual clue identities are not.

## Draft assumptions removed

Timed rounds, correct/skip scoring, score results, sounds/haptic preferences, a settings repository, and eight differently named decks were temporary implementation assumptions. Their classes/tests do not make them current requirements.

Room/Retrofit/OkHttp stubs likewise do not justify a database or network. The complete offline APK decision takes precedence.

The seven sections are the current curated release, not a permanent ban on additional packaged content.

## Names and unresolved inputs

Public name: **Charades**. Working title: **Free Charades**. Marketing line: **“The offline game of 20 Questions & Charades.”** Retain requested section wording, including **Animes**.

No donation destination/beneficiary is supplied. Hide the optional browser action until configured. Do not assume a release URL, signing identity, store publication, or successful device test.

## Future work

Explicit new user decisions can change the product. Update the relevant product/design/interaction docs, implementation, and affected policies together. Do not silently restore obsolete draft behavior because code once existed.
