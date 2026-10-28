# Architecture

Charades is a native Kotlin/Compose Android app that reads its complete catalog and media from the APK. There is no remote catalog, Room database, synchronization, or download state.

## Local catalog

`data/repository/CategoryRepository.kt` loads `assets/categories/bundled_categories.json` on an IO dispatcher and maps it to `domain/model/Category.kt` and `Word.kt`. Images resolve to `file:///android_asset/images/<filename>`. Source website URLs are provenance, not image-loading endpoints.

The catalog owns the current seven sections. Do not maintain a second list of enabled/downloaded categories. See [content maintenance](CONTENT.md) for schema and preparation.

## Session and reveal

`domain/game/DeckEngine.kt` owns session order, clue position, and manual guess-count rules. A section-detail encounter creates a fresh shuffled session. Gameplay walks that order without reshuffling each reveal.

`ui/screens/game/GameViewModel.kt` owns the three-second reveal deadline and exposes state to Compose. A monotonic deadline counts lock/background time; recomposition must not spawn a new countdown. Next resets the count and starts a new reveal deadline.

+ increments; − clamps at zero. X/system Back pop normally. There are no correct/skip scores, timed rounds, sounds, preferences, or results history.

The gameplay ViewModel retains session state in memory and Android `SavedStateHandle`: category/session identity, ordered prompt IDs, index, manual count, and elapsed-time reveal deadline. Android can restore the current clue/order/count after process recreation. This is transient restoration state, not a permanent history or preference system.

## Orientation and screen wake

Gameplay requests landscape and restores the prior orientation on exit. An active revealed clue sets the window keep-screen-on flag. Three minutes without interaction releases that flag, allowing Android's regular timeout. The app does not force sleep at exactly three minutes.

Interaction refreshes the idle deadline. Exit clears the wake flag. Countdown continuity relies on its deadline rather than a background wake lock.

## Navigation and visual system

`MainActivity` hosts Compose. `ui/navigation/` owns the library/detail/game and local legal/source routes. Use standard back-stack behavior.

`ui/theme/` and `ui/components/` own shared design. [DESIGN](../DESIGN.md) and [UX-CONTRACT](../UX-CONTRACT.md) record the approved dark blue/coral identity and interaction rules. Present center-cropped clues intentionally while preserving readable type and reachable controls on small landscape screens.

## Privacy boundary

The manifest declares neither Internet nor network-state permission. There is no analytics/ad SDK, account API, remote content client, preferences repository, or permanent gameplay-history database. Any optional donation link uses an external browser and remains hidden until a real URL is configured.

Keep saved-state fields, backup settings, and the [privacy policy](PRIVACY_POLICY.md) aligned when changing data handling.

## Maintenance owners

- `app/src/main/assets/`: complete catalog, media, and legal text.
- `docs/photo_sources.json`: source URLs, available creators/licenses, and prepared-file hashes.
- `scripts/import_photos.py`: developer-only acquisition; not a runtime or normal-build dependency.
- `scripts/validate_catalog.py`: offline content validation.
- `scripts/sync_policies.py`: legal Markdown to bundled plain text.
- `gradle/libs.versions.toml`, `app/build.gradle.kts`, and Gradle wrapper: pinned build configuration.

Avoid unrelated dependency upgrades when changing content or UI.
