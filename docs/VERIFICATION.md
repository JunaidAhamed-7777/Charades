# Verification record — 28 October 2026

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
