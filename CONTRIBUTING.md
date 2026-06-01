# Contributing to aotts

Thank you for contributing to aotts, the SVOX Pico TTS engine built for BAOSP.
This guide is written to work well with screen readers and keyboard-only navigation.
Every step is numbered and linear — no visual layout is assumed.

---

## Before you start

You need:

1. A GitHub account — create one free at github.com/join
2. Git installed on your computer — download at git-scm.com
3. Android Studio or a text editor such as VS Code
4. Java 17 — download at adoptium.net
5. Android SDK with NDK version 23.1.7779620 (installed via SDK Manager)
6. CMake 3.18 or newer (installed via SDK Manager)

### Installing the NDK and CMake in Android Studio

1. Open Android Studio
2. Open the Tools menu, then select SDK Manager
3. Select the SDK Tools tab
4. Check NDK (Side by side) — version 23.1.7779620
5. Check CMake
6. Activate Apply, then OK

---

## About this project

aotts wraps the SVOX Pico TTS library (written in C) as an Android TTS engine.
The project has two main parts:

- `src/main/java/` — Kotlin/Java wrapper that registers as an Android TTS engine
- `src/main/cpp/` — C code for SVOX Pico (downloaded during build, not committed)
- `src/main/assets/lang/` — Language data files (also downloaded during build)

The SVOX Pico C source and language data are downloaded automatically by the CI.
For local builds you need to download them yourself — see Step 4 below.

---

## Step 1 — Fork the repository

Forking makes a personal copy of the code under your own GitHub account.

1. Open github.com/tech-master33/aotts
2. Activate the Fork button near the top of the page
3. On the next screen, activate Create fork
4. GitHub takes you to your copy at github.com/YOUR-USERNAME/aotts

---

## Step 2 — Clone your fork to your computer

Open a terminal and run these commands one at a time.
Replace YOUR-USERNAME with your actual GitHub username.

```bash
git clone https://github.com/YOUR-USERNAME/aotts.git
cd aotts
git remote add upstream https://github.com/tech-master33/aotts.git
```

---

## Step 3 — Create a branch for your change

```bash
git checkout -b your-branch-name
```

Name examples:

- `fix/crash-on-italian-voice`
- `feature/add-german-dialect`
- `docs/local-build-steps`

---

## Step 4 — Download SVOX Pico source and language data

The C source and language files are not stored in the repository.
Run these commands to download them before building.

```bash
# Download SVOX Pico native C source
git clone --depth=1 https://android.googlesource.com/platform/external/svox /tmp/svox
mkdir -p src/main/cpp/pico
cp -r /tmp/svox/pico/* src/main/cpp/pico/

# Download language voice data files
mkdir -p src/main/assets/lang
curl -L \
  "https://android.googlesource.com/platform/external/svox/+archive/refs/heads/master/pico/lang.tar.gz" \
  | tar -xz -C src/main/assets/lang/

# Confirm the language files downloaded correctly
ls src/main/assets/lang/
```

You should see files ending in `.bin` — one file per language.
If the tar download fails, try the sparse clone fallback:

```bash
git clone --depth=1 --filter=blob:none --sparse \
  https://android.googlesource.com/platform/external/svox /tmp/svox_lang
git -C /tmp/svox_lang sparse-checkout set pico/lang
git -C /tmp/svox_lang checkout
find /tmp/svox_lang/pico/lang -name "*.bin" -exec cp {} src/main/assets/lang/ \;
```

---

## Step 5 — Make your changes

Key source files:

- `src/main/java/` — TTS engine service, language detection, voice selection
- `src/main/cpp/` — C wrapper around SVOX Pico (after you download it in Step 4)
- `CMakeLists.txt` — build rules for the C code
- `build.gradle` — Android project configuration

### Language support

Supported languages are defined in the Java/Kotlin service class.
Each language needs a matching `.bin` file in `src/main/assets/lang/`.
Do not add a language unless its `.bin` file is available from the SVOX AOSP source.

---

## Step 6 — Build locally

```bash
chmod +x gradlew
./gradlew assembleDebug -Pandroid.ndkVersion=23.1.7779620
```

The APK will be at:

```
build/outputs/apk/debug/
```

Note: the APK lives in the root `build/` folder, not inside `app/build/`.
This is because the project does not use an `app/` module.

To install on a connected device:

```bash
adb install build/outputs/apk/debug/*.apk
```

### Manual testing checklist

Go through each item before submitting your pull request:

- The APK builds without errors
- The TTS engine appears in Settings under Language and input, Text-to-speech output
- Selecting the engine does not crash the Settings app
- The engine speaks a test sentence clearly when you activate Play
- All six languages produce speech (English US, English GB, German, Spanish, French, Italian)
- The screen reader can read the engine name and language options aloud

---

## Step 7 — Commit your changes

```bash
git add .
git commit -m "fix: Spanish voice mispronounces words ending in -cion

The phoneme mapping for -cion endings was missing a rule.
Added the rule in the Kotlin layer before passing text to Pico."
```

---

## Step 8 — Push and open a pull request

```bash
git push origin your-branch-name
```

Then:

1. Open github.com/YOUR-USERNAME/aotts
2. Activate Compare and pull request
3. Title: one sentence describing the change
4. Description: what problem does this solve, how did you test it
5. Activate Create pull request

---

## Reporting a bug

1. Open github.com/tech-master33/aotts/issues
2. Activate New issue
3. Include:
   - Which language or voice has the problem
   - What text triggered the problem
   - What you heard versus what you expected to hear
   - Your Android version and device model

---

## Getting help

Open a discussion at github.com/tech-master33/aotts/discussions
