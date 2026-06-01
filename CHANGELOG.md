# Changelog — aotts

All notable changes to aotts are documented here.
Format follows [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).
Nightly builds are automatically tagged; see the [releases page](https://github.com/tech-master33/aotts/releases) for APK downloads.

---

## [Unreleased]

Changes merged to `main` but not yet cut into a versioned release.

---

## [0.1.0] — 2025-06-01 (initial nightly)

### Added
- **SVOX Pico TTS engine** via JNI — lightweight speech synthesis compatible with Android's TextToSpeech API
- **PicoTtsService** — registers as a system-level TTS engine; compatible with TalkBack, andrdscren, and any app using `android.speech.tts`
- **SettingsActivity** — lets users select Pico as their default TTS engine and configure language/voice
- **Six languages** — en-US, en-GB, de-DE, es-ES, fr-FR, it-IT
- **NDK CMake build** — `src/main/cpp/CMakeLists.txt` + `pico_jni.c` bridge; targets armeabi-v7a, arm64-v8a, x86, x86_64
- GitHub Actions workflow (`android.yml`) — builds APK on every push to `main` and publishes a release
- Integrated into BAOSP nightly bundle (`baosp-nightly.yml` job 2) alongside andrdscren and aoler

### Fixed / hardened (build infrastructure)
- Gradle wrapper added (`gradlew`, `gradlew.bat`, `gradle-wrapper.jar`, `gradle-wrapper.properties`) — CI can now build without a pre-installed Gradle
- `build.gradle` updated:
  - namespace + applicationId changed to `org.baosp.aotts`
  - compileSdk / targetSdk bumped 32 → 34 (Android 14)
  - minSdk bumped 21 → 26 (aligned with BAOSP baseline)
  - `compileOptions` bumped `VERSION_1_8` → `VERSION_17`
  - Release build: `minifyEnabled true`, debug-keystore signing for installable nightlies
  - `shrinkResources false` kept for native `.so` files (cannot be shrunk by R8)
  - AppCompat and ConstraintLayout bumped to current stable versions
- `settings.gradle` fixed — removed erroneous `include ':app'` (source lives at repo root, not in a submodule)
- `proguard-rules.pro` updated — covers `org.baosp.aotts` package, keeps JNI signatures and TTS service intact

### Technical details
- minSdk 26 (Android 8.0), targetSdk 34 (Android 14)
- NDK 23.1.7779620, CMake 3.18.1, JDK 17, Groovy DSL
- Package: `org.baosp.aotts`

---

## How the nightly build works

Each night at midnight UTC the `baosp-nightly.yml` workflow in the [baosp](https://github.com/tech-master33/baosp) repo:

1. Checks out the latest `main` of this repo
2. Downloads SVOX Pico C source and language data
3. Runs `./gradlew clean assembleRelease` (R8-optimised, debug-signed)
4. Renames the APK to `aotts-<git-sha>.apk`
5. Uploads it as part of the combined BAOSP nightly release at  
   **[github.com/tech-master33/baosp/releases/tag/nightly](https://github.com/tech-master33/baosp/releases/tag/nightly)**

The standalone `android.yml` in this repo builds on every push to `main`.

[Unreleased]: https://github.com/tech-master33/aotts/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/tech-master33/aotts/releases/tag/v0.1.0
