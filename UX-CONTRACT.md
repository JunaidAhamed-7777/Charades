# Charades interaction contract

Product truth is maintained in [PRODUCT.md](PRODUCT.md); visual intent in [DESIGN.md](DESIGN.md). The stage-zero draft is background, not an immutable UI specification.

- The entire catalog loads from APK assets on IO. No network requests, database gate, account, entitlement, or subsequent downloads. Loading, error, and retry states are explicit.
- Browsing and legal screens stay portrait; gameplay requests landscape and restores portrait on exit.
- Library → category → three-second countdown → picture reveal. The same deck supports human-led Charades and 20 Questions; there is no unnecessary mode selector because the app's controls are identical.
- A new category encounter starts a fresh shuffled deck. Pictures proceed in that order. Saved navigation/game state preserves that order, index, manual counter, and countdown deadline through supported Android recreation.
- Reveal controls: leave game, increment/decrement the manual guess count, and Next. Counter clamps at zero and resets for the next picture. No auto-timer, auto-skip, automatic scoring, or sound controls. Next always starts a fresh countdown.
- Countdown is based on elapsed time and completes through screen lock. Exhausting the deck offers a return to category for a fresh shuffle. The deck does not silently repeat.
- In-game close and system Back pop to category without an extra confirmation. Leaving category returns to library; Back at library follows Android's normal activity exit.
- The active reveal keeps the screen awake for up to three minutes after the last touch. After inactivity the app clears its awake flag and Android controls timeout. Leaving gameplay always clears it.
- Info provides rules, app version, photo credits, privacy, terms, and GPL license. These are locally bundled and scrollable. Donation stays unconfigured until a real destination is supplied; no fabricated payment link.
- Shared owners: Navigation Compose/AppNavHost for back stack; Material buttons for actions; shared ScreenHeader for back affordance; StatusContent for loading/error; fixed local legal-asset map for legal documents. Dates, tables, CRUD, toast systems, and remote async mutations do not apply.
- Touch targets are 48dp or larger. Icon buttons carry TalkBack labels; decorative images are silent. Font scaling and safe insets must remain usable. The game image/answer are kept local to the clue-giver's display.
