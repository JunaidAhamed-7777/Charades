# Verification record — 3 November 2026 (landing and icon follow-up)

## Current APK

`app/build/outputs/apk/debug/app-debug.apk`, 21,255,160 bytes (20.27 MiB). Runtime checkpoint: `39137ab`.

SHA-256: `925a9500ad023ea9cbc25e31824ea4fff3f84359fa8fca059b103cb59c2f44d1`.

The landing page now uses Space Grotesk headings and Manrope body/labels, with a stronger “Choose a deck” hierarchy, a quiet “Made By Junaid” credit, and a compact privacy action. Font scale increases the grid minimum so large text gets wider cards. The launcher uses an upright card/C monogram with shared legacy, adaptive, round, and monochrome geometry. Game logic, deck models, and content are unchanged.

## Verification

- Full `testDebugUnitTest lintDebug assembleDebug` passed: 13 tests, no failures/errors. The final resource cleanup also passed lint/assembly. No lint errors; the missing monochrome-icon and unused launcher-color warnings are resolved.
- Catalog validation, policy synchronization, and APK verification passed: all six decks, 300 clues, 251 images, full sources/legal text, and both new bundled fonts; no network permissions.
- DESIGN lint and the premium strict static audit passed without findings/errors.
- Android 17/API 37 emulator: normal landing shows all six cards and exact creator credit. At 200% text size, the grid uses wider cards, labels remain legible, and the creator/privacy footer is reachable. Privacy opened offline. Font scale restored to 1.0.
- A landscape clue reveal with the new fonts kept the image, answer, count, and Next visible; Back returned normally.
- Android's App info screen rendered the new adaptive launcher icon correctly. Themed-icon metadata is present; themed-color rendering was not separately toggled on a launcher.
- New captures: `docs/screenshots/library.png`, `library-large-text.png`, `library-large-text-footer.png`, `game.png`, and `icon-system.png`. `docs/branding/launcher-icon-preview.png` is a geometric source preview; it is not an Android screenshot.

No physical phone was connected. Earlier lock/idle/TalkBack and older-device limitations still apply. The APK is debug-signed; no public release was published.

---

# Earlier verification — 3 November 2026

## Current artifact

Minimal near-black Compose UI with six decks of 50 clues, Hollywood-only Movies, name-only Famous People, and no Anime Characters. Shared theme/type/spacing/shape/component owners replace screen-local styling. Generated landing art and the unused playful font are removed. The domain models, game engine, ViewModels, and navigation code are unchanged.

Built and installed the complete debug APK on an Android 17/API 37 emulator. APK: `app/build/outputs/apk/debug/app-debug.apk`, 20,836,846 bytes (19.87 MiB). Runtime/content checkpoint: `3092bee`.

SHA-256: `7e9e76ce0d665c75d865165c940cc0ae3660bebc36fcf59d1dbb3da53f950e72`.

## Automated verification

- `validate_catalog.py`: PASS, six decks, 300 unique clues, 251 WebP images, 8.98 MiB. Local paths, actual WebP dimensions (maximum 720px long edge), source records, byte counts, and hashes match.
- `sync_policies.py --check`: PASS; legal text remains synchronized.
- `gradlew.bat testDebugUnitTest lintDebug assembleDebug`: PASS; 13 unit tests, no failures/errors; lint has no errors, with dependency/platform advisories remaining.
- `verify_apk.py`: PASS; actual APK contains the current catalog, all referenced media, complete source manifest, legal text, and bundled font. No Internet/network-state permissions.
- All pictured clues and the cover were visually reviewed against their source identities. Weak object/cover/anime images were corrected.
- Offline re-encoding was checked with network access disabled at the helper boundary; compliant files remain unchanged and unsafe paths are rejected. Validator rejection of a missing image was also checked.
- `designmd lint DESIGN.md`: zero errors/warnings. Premium strict static audit: zero findings. Source inspection confirms color literals only in `Theme.kt`, font sizes only in `Typography.kt`, dp spacing literals only in the six shared scale tokens, and a single fixed-4:3 Crop image renderer.

## Emulator verification

- Fresh app storage, airplane mode enabled, Wi-Fi/data disabled: first launch and all six decks open and reveal clues offline.
- Famous People reveals a name with no photograph; Anime Characters is absent and all deck counts show 50.
- Counter increments/decrements, clamps at zero, and resets on Next. Backgrounding after Next for longer than three seconds returns to the revealed clue with count zero.
- X and system Back pop normally; portrait browsing returns after landscape gameplay.
- Default rendering and compact 320dp landscape height at 540dpi/font scale 1.3: image, title, manual controls, and Next remain accessible. Density/font scale restored afterward.
- Local privacy and source pages open. The neutral microphone cover shows its actual creator, license, and source URL offline. No crash entries appeared in the inspected crash buffer.
- Actual app captures are in `docs/screenshots/`; the README uses the new library/game images. The app was left at the library.

## Limits

No physical phone was connected during this session. Hardware lock/unlock, full three-minute idle wake release, TalkBack navigation, older Android versions, tablets, and every possible long-clue/font-scale combination were not tested end to end. The unchanged engine tests cover deadline/session rules; this emulator pass is not a physical-device test. No release was published and no signing identity or donation destination was invented.

The optional Impeccable design sidecar predates the redesign; runtime tokens and maintained design/product docs are current.

---

# Historical verification — 28 October 2026

## Delivered artifact

Installed the complete debug APK on the user's Samsung Galaxy A26 (SM-A266B), Android 16, using ADB. The APK is `app/build/outputs/apk/debug/app-debug.apk`, 16,158,119 bytes (15.41 MiB).

SHA-256: `431e069f65a83c9fef7e23949376ebebaf4e4ed40af081d3ca771ff6f8a18c4e`.

The artifact includes the final credit cleanup from `a8e83e3`. Subsequent importer safeguards do not change runtime image bytes or game behavior.

## Automated checks

- Gradle APK assembly passed. The latest runtime code passed `testDebugUnitTest` (13 tests, zero failures/errors) and `lintDebug` (zero errors; advisory warnings remain).
- `validate_catalog.py`: seven sections, 70 prompts/images, 3.40 MiB of bundled images; IDs, local paths, source records and checksums pass.
- All 70 WebP files were decoded and their subject identities visually reviewed. No runtime image URL is remote.
- `sync_policies.py --check`: repository policies match bundled text.
- `verify_apk.py`: actual APK contains the current catalog, every image, privacy/terms/GPL/font-license text, and local fonts; no Internet/network-state permission.

## Physical-device checks passed

- Cold launch with airplane mode enabled; the deck library is immediately available. Original airplane setting restored afterward.
- All seven sections open and reveal an image, answer, attribution and controls.
- Three-second initial and Next countdowns; Next resets the manual counter and advances the clue.
- Counter increments/decrements and clamps at zero; disabled decrement is visible at zero.
- Next → background for more than three seconds → return reveals the next picture with count zero.
- X returns to section detail; system Back returns to library. Browsing returns to portrait after landscape play.
- Home → process kill → reopen preserves the same clue and manual count (tested at two guesses).
- Default display and font scale 1.3; a temporary 540dpi override also exercises the compact rail at 320dp landscape height with 1.3 text. Image, answer and controls remain visible. Original density 450dpi and font scale 1.0 restored.
- Local info, privacy, terms, font licenses and image credits open and scroll. Final rebuilt credits show clean Giraffe/Kangaroo author labels without scraped page boilerplate.
- Current app process has no AndroidRuntime fatal exceptions in the inspected crash log.

The final app was left at its library. Screenshots in `docs/screenshots/` are real device renders, not mockups.

## Scope limits

Physical lock/unlock timing, the full three-minute idle wake release, and a TalkBack walkthrough were not measured end to end on hardware. Deterministic engine tests cover reveal deadlines across elapsed time/restoration; background timing was verified on the phone. Older Android versions and tablets were not separately device-tested. This record concerns a debug-signed personal build, not a published release or configured long-term signing identity.
