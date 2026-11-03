# Charades product context

Reconciled implementation target — 3 November 2026.

This combines the original game brief with the latest installation and visual-direction decisions. [Decisions](docs/DECISIONS.md) and [reconciliation](docs/SPEC_RECONCILIATION.md) record what supersedes earlier drafts.

## Purpose and identity

**Charades** is a free, personal, nonprofit Android party game. **Free Charades** is its working name. Marketing line: **“The offline game of 20 Questions & Charades.”**

The landing page includes the creator credit **“Made By Junaid”**. Typography pairs Space Grotesk headings with Manrope body text within the minimal dark visual system.

People lead their own game. The app supplies clues, a manual guess counter, and simple navigation. It does not judge answers or run competitive timed rounds.

The visual priority is dark, minimal, and premium: near-black #0E0E10, flat surfaces, pure white titles, muted grey secondary text, and one sparingly used accent. Use two font weights, one corner radius, shared spacing and typography, and consistent content alignment. No gradients, shadows, bright fills, image collages, or generated landing artwork. This supersedes the earlier navy/coral and playful-lettering direction.

## Complete installation

One initial APK download and installation includes every section, image, word, source record, and legal document. Everything works immediately on first launch in airplane mode.

Use bundled JSON and local media. No Room database, remote catalog check, network image loading, updater, or later asset pack. Do not request `INTERNET` or `ACCESS_NETWORK_STATE`. ADB installs the complete APK without a separate photo push.

Developer-only scripts can acquire/prepare photos before building; normal app startup and builds must not depend on fetching a new catalog.

## Current sections

The curated collection has six sections, each with 50 clues:

1. Animals
2. Movies
3. Famous People
4. Animes
5. Countries
6. Household Objects

Anime Characters is removed. Movies contains Hollywood films only. Famous People mixes historical figures and recognizable current public figures; these clues show the name alone, without a photograph. Other decks retain accurate local clue images. Difficulty ranges from familiar to challenging without specialist trivia.

Future complete APK releases can expand the collection. Images must accurately represent their clues, with local provenance. Use proper source photos/artwork for factual subjects rather than generated identities or unrelated stock images.

## Play and navigation

For **20 Questions**, a player who cannot see the clue asks yes/no questions and the group tracks guesses manually. For **Charades**, players act out the clue. The group applies its own rules; the app does not need a separate mode engine or score system.

Flow: library → section detail → gameplay. Each section-detail encounter creates a fresh shuffle. Gameplay walks that order for the session and does not reshuffle after every clue.

Gameplay uses landscape orientation. X and system Back perform a normal pop without a confirmation dialog, clearing the clue's guess count. There is no score/result route.

## Reveal and counter

Show a three-second countdown before the first reveal and after Next. Next resets the count before revealing the next clue.

A reveal shows a word/name and source text, with an image for illustrated decks. Famous People reveals names alone. Images use one fixed 4:3 Crop wrapper with rounded corners and a placeholder surface; deck covers use one hero image and a uniform dark scrim. Keep subjects recognizable and controls clear on small landscape screens.

+ increments the manual count. − clamps at zero. There is no automatic 20-guess cap, correctness judging, or conversion of guesses to points.

Use a monotonic deadline for countdowns so lock/background time counts. Returning after the deadline reveals the clue rather than resuming a paused countdown.

## Screen wake

Keep an active revealed clue awake. After three minutes without interaction, release the keep-awake flag and allow Android's regular screen timeout. Interaction refreshes the idle deadline. Exit restores normal wake behavior.

Three minutes is an idle wake policy, not a game limit. A background wake lock is not needed merely to animate a countdown.

## Data and external links

No accounts, analytics, ads, in-app payments, light mode, sounds, saved preferences, or permanent game history. Android may restore locally retained transient navigation/session state; no gameplay is sent to the developer.

Privacy, terms, license, and source information are readable offline. Repository policies and shipped text stay synchronized.

A donation action is optional and opens a configured website through the browser. The URL/beneficiary is unresolved; hide the action until supplied and never invent a destination. This does not block gameplay or installation.

## Delivery

Keep “How to install” near the top of README and maintain practical agent/developer context. Preserve GNU GPL v3. Build a complete APK and install with ADB when an authorized device is available.

Verify offline media, landscape reveals/countdowns, manual counter/reset, session order/navigation, lock timing, and idle wake behavior. Report actual build/device outcomes, skipped checks, and the real artifact path. Do not assume a published release or download URL.
