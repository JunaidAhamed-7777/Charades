# Product decisions

Updated 3 November 2026. [PRODUCT.md](../PRODUCT.md) is the reconciled implementation target.

## One complete APK

Bundle every section, clue, image, source record, and legal document in the initial APK. ADB installs that complete package.

This supersedes remote catalog checks, per-section installation, download buttons, runtime image requests, Room content seeding, and background content updates. The latest instruction requires everything immediately with no later installation.

## Human-led gameplay

Use one untimed clue viewer for 20 Questions and Charades, with a manual guess count. Show three seconds before every reveal. Next resets the count; − clamps at zero. Do not enforce an automatic 20-guess cap.

The earlier 30/60/90/120-second rounds, correct/skip scoring, result screens, sounds/haptic settings, saved preferences, pause/replay controls, and history were temporary draft additions. They are superseded.

## Current taxonomy

Use Animals, Movies, Famous People, Animes, Countries, and Household Objects, with 50 clues per deck. Remove Anime Characters. Movies is Hollywood-only; Famous People combines historical and current recognizable names with no gameplay photos. Keep the existing catalog schema and game engine.

The temporary eight-deck catalog with food, sports, landmarks, everyday, and nature titles is superseded.

## Navigation, reveal, and wake

Landscape gameplay presents the word/name, source text, and a fixed 4:3 cropped image where applicable. Famous People is name-only. X/system Back pop normally without confirmation. Shuffle on a section-detail encounter; preserve that session's order.

Countdown deadlines continue through lock/backgrounding. Keep an active clue awake, releasing the flag after three minutes without interaction. Three minutes is an idle wake policy, not a round timer.

## Visual direction

The 3 November redesign supersedes navy/coral, playful lettering, and generated landing art. Use #0E0E10, flat slightly raised surfaces, white titles, muted grey text, one restrained accent, two font weights, and one radius. Shared tokens own the 4/8/12/16/24/32dp spacing scale and four typography roles. Single-image 4:3 covers replace collages; all images use the shared Crop wrapper and local WebP assets with a maximum 720px long edge.

The app remains dark; no light-mode selector is needed.

## Data and optional donation

No accounts, analytics, ads, runtime network permissions, permanent gameplay history, or saved preferences. Android's local saved state may restore transient sessions/navigation.

A donation action is optional and uses the browser. No URL/beneficiary is supplied, so keep it hidden until configured. This unresolved input does not block the game.

## Documentation and delivery

Maintain agent/product context, accurate privacy/terms/license, image provenance, and installation instructions near the README top. Retain GNU GPL v3. Report real build/device outcomes and artifact paths; do not fabricate release URLs.

For authorized small commits/pushes, stage explicit files and coordinate shared Git mutations.
