# Working on Charades

Read this file and `PRODUCT.md` before changing the app. Charades is a complete offline Android game delivered in one APK. Earlier timed-round, preferences, and content-downloader drafts are superseded.

## Context and priority

The latest user request takes precedence. `PRODUCT.md` is the reconciled product context; `docs/DECISIONS.md` and `docs/SPEC_RECONCILIATION.md` explain approved changes. `DESIGN.md` and `UX-CONTRACT.md` own shared design and interaction behavior.

The latest design direction prioritizes the app's overall aesthetic over rigid stage-zero pixel/layout hints. Use the approved rich dark navy, blue/coral identity and playful lettering; do not restore an old teal accent or exact draft background merely because it appeared in an early specification.

Public name: **Charades**; working name: **Free Charades**. Marketing line: **“The offline game of 20 Questions & Charades.”**

## Product contract

- Ship every section, clue, image, source record, and legal document in the base APK. A fresh install works in airplane mode without a check/download/setup gate.
- The current curated set is Animals, Movies, Famous People, Animes, Anime Characters, Countries, and Household Objects. It may grow through future complete APK releases.
- No Room, runtime remote APIs, download/update gates, accounts, analytics, ads, timed rounds, scoring, sounds, saved preferences, or light theme.
- Gameplay is landscape. Show a three-second countdown before every reveal, including after Next.
- + increments the manual guess count; − clamps at zero. Next and exit reset it. Do not turn the count into points or impose an automatic 20-guess limit.
- Shuffle on each section-detail encounter; preserve that order throughout its session.
- X/system Back pop normally without confirmation.
- Countdowns continue through screen lock/backgrounding. Keep an active clue awake; after three minutes without interaction, release the flag and allow Android's normal sleep behavior.
- No `INTERNET` or `ACCESS_NETWORK_STATE` permissions. Source URLs are provenance; gameplay images are local.
- Optional donations use an external browser link. No destination/beneficiary has been supplied. Hide the action until configured; never invent one.

## Implementation

Native code is under `app/src/main/java/com/yourdomain/freecharades`. Content is in `app/src/main/assets`; Android resources are in `app/src/main/res`. Read the architecture/content guides before changing their formats.

Keep `com.yourdomain.freecharades` as application ID unless the user requests migration. Preserve GNU GPL v3. Keep SDK paths, `local.properties`, signing keys, and secrets out of version control.

Use Kotlin/Compose and shared theme/components. Keep session rules independent of Android UI. Maintain one countdown owner and a monotonic deadline; recomposition must not create another countdown. Cancel obsolete jobs on Next/exit. Preserve intended timing across lifecycle changes.

Load/validate the catalog off the main thread. Check local paths and stable IDs, show recoverable content errors, and keep source identities accurate. Never silently replace a factual clue with an unrelated picture or generated likeness. Avoid a second hardcoded list of available sections.

Use local Android saved state for transient session/navigation restoration, not a preference/history service. Update the privacy policy for any new data handling.

Prioritize coherent visual design and readable images. Support small landscape screens, large text, system insets, contrast, meaningful accessibility labels, and 48 dp touch targets. Apply intentional center-crop imagery without clipping the controls.

## Verification

Run relevant checks from the repository root:

```powershell
python scripts\validate_catalog.py
python scripts\sync_policies.py --check
.\gradlew.bat testDebugUnitTest lintDebug assembleDebug
```

Use focused checks during iteration. Session/deadline changes need meaningful unit coverage. Simple prose/color changes do not need tests that mirror implementation.

Validate files, JSON, and image identities; visually review content. Inspect the APK for the complete catalog/media/legal text. When a device is available, install with `adb install -r` and follow `docs/DEVELOPMENT.md`: first-launch airplane mode, every section, reveal/count, Next/Back, lock timing, idle wake release, and layout accessibility.

Report actual results. A build is not a device test. Do not fabricate a release URL, attribution, donation destination, or passed check.

## Documentation and handoff

Keep “How to install” near the README top. Update product/design/architecture/content docs when relevant behavior changes. Legal Markdown in `docs/` is canonical: run `python scripts/sync_policies.py`, then `--check`, to synchronize app text.

Handoff should state what changed, what passed, the APK path when built, and remaining blockers. Do not publish or send external messages without user authorization. For authorized commits/pushes, stage explicit paths and coordinate Git mutations so one worker does not commit another worker's unfinished files.
