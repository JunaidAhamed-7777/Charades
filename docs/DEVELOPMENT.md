# Development and verification

## Setup and build

The project targets Android SDK 35, supports Android 8.0/API 26 and newer, and compiles Java/Kotlin bytecode for Java 17. Use the checked-in Gradle wrapper and a compatible JDK. Android Studio's bundled JDK can be used; configure its Gradle JDK and terminal `JAVA_HOME` consistently.

Install Android SDK Platform 35 and Platform-Tools in SDK Manager. Put the machine-specific SDK path in untracked `local.properties`; Android Studio can generate it. A Windows example:

```properties
sdk.dir=C\:\\Users\\YOUR_USER\\AppData\\Local\\Android\\Sdk
```

Initial SDK/Gradle setup needs internet. The compiled app does not.

From the repository root in PowerShell:

```powershell
python scripts\validate_catalog.py
python scripts\sync_policies.py --check
.\gradlew.bat testDebugUnitTest lintDebug assembleDebug
```

The APK is `app/build/outputs/apk/debug/app-debug.apk`. Unit-test/lint reports are under `app/build/reports/`. Session rules are covered by `app/src/test/java/com/yourdomain/freecharades/domain/game/DeckEngineTest.kt`.

Normal builds must not fetch new media. Inspect/unzip the APK to confirm its catalog, every referenced image, and legal text are included.

## Install and launch

Enable USB debugging on an authorized phone, or start an emulator:

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" devices
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" install -r .\app\build\outputs\apk\debug\app-debug.apk
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" shell am start -n com.yourdomain.freecharades/.MainActivity
```

Use your actual SDK path and `-s DEVICE_SERIAL` when needed. Install the complete APK once; do not push a media folder or download a database afterward.

An APK signed by another key cannot upgrade an installed build. Preserve the signing identity for updates. Uninstalling a development build removes its app-managed local state.

## Device acceptance checklist

Use a fresh emulator profile or clear Android app storage for first-launch verification. Clearing storage intentionally removes local state.

1. Enable airplane mode before first launch. Open Animals, Movies, Famous People, Animes, Anime Characters, Countries, and Household Objects. Every clue image must be ready without another download or content-check prompt.
2. Start gameplay. Confirm landscape, three-second countdown, correct center-cropped image, word/name, and source text.
3. Press + repeatedly and − past zero. The manual count must stay nonnegative; it must not become a score or enforce an automatic 20-guess limit.
4. Press Next. Confirm count reset and a new three-second countdown. Check that the session walks the shuffled section-detail order.
5. Exit through X, then system Back. Both pop normally without a dialog. Re-enter section detail for a fresh shuffle and start with count zero.
6. Lock/background during countdown, wait past the deadline, and return. The clue should be ready; countdown time must not pause.
7. Leave a revealed clue idle for three minutes. Confirm keep-awake releases and Android's ordinary timeout can apply. Confirm interaction refreshes idle timing and exit restores normal orientation/wake behavior.
8. Read Privacy, Terms, License, and source information offline. There must be no sounds/preferences screen, timed-round setting, score screen, ad, sign-in, or light-theme option.
9. Check small landscape screens, large text, system insets, contrast, and TalkBack labels. Keep controls visible and reachable.
10. Inspect `adb logcat` for fatal exceptions and the merged manifest for absence of `INTERNET`, `ACCESS_NETWORK_STATE`, and unnecessary runtime permissions.

Record device/API, outcomes, and skipped checks. A build or unit-test result is not a device test.

## Distribution

`assembleDebug` produces an installable local debug APK. Keep release signing keys outside the repository. Configure signing privately before generating a distribution APK.

Distribute one signed APK with all sections/media. Do not introduce asset packs or remote first-run setup. Retain a stable signing identity, increment `versionCode`, and retain its matching source version. Add a README download link only after the artifact exists at that URL.
