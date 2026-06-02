# Contributing to aotts

Thank you for contributing to aotts, the SVOX Pico TTS engine built for BAOSP.
This guide is written to work well with screen readers and keyboard-only navigation.
Every step is numbered and linear — no visual layout is assumed.

---

## Ways to contribute

You do not need to write code to contribute. Here are all the ways you can help:

1. Report a bug — describe something that does not sound right or causes a crash
2. Request a feature — describe a voice, language, or setting you need
3. Add mispronounced words — report words Pico says wrong so they can be fixed
4. Improve documentation — fix unclear steps or add missing information
5. Test builds — install nightly APKs and report voice quality issues
6. Translate — help add new language voices or translate in-app strings
7. Write code — fix bugs or add features to the TTS service

---

## Claiming an issue

If you want to work on an existing issue — especially one tagged `needs help` or `good first issue` — follow these steps first.

1. Open the issue you want to work on
2. Read the full description, including the "Where to start" section if there is one
3. Leave a comment saying you would like to work on it — for example: "I'd like to take this on"
4. Wait for a maintainer to reply — we will assign the issue to you and answer any questions
5. Once assigned, follow the steps in this guide to fork, branch, make your change, and open a pull request

This step matters because it avoids two people doing the same work at the same time.
If you have been assigned an issue and decide you cannot continue, leave a comment to let us know.
We will unassign it so someone else can pick it up.

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

Running `git remote -v` should now show both `origin` (your fork) and `upstream` (the main repo).

---

## Step 3 — Create a branch for your change

Never commit directly to `main`. Create a new branch first.

```bash
git checkout -b your-branch-name
```

Name the branch something descriptive. Examples:

- `fix/crash-on-italian-voice`
- `feature/add-portuguese-voice`
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

You should see `.bin` files — one per language.
If the tar download fails, use the sparse clone fallback:

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
- `src/main/cpp/pico/` — SVOX Pico C source (downloaded in Step 4, do not commit)
- `src/main/assets/lang/` — voice data `.bin` files (downloaded in Step 4, do not commit)
- `CMakeLists.txt` — native build configuration

### Accessibility rules for this project

Every change must follow these rules:

1. All UI elements in settings screens must have a content description
2. Touch targets must be at least 48dp wide and 48dp tall
3. All strings shown to the user must be in `strings.xml` so they can be translated
4. Test every settings screen change with a screen reader turned on before submitting

---

## Step 6 — Build and test locally

```bash
chmod +x gradlew
./gradlew assembleDebug
```

The APK will be at:

```
build/outputs/apk/debug/aotts-debug.apk
```

To install it on a connected Android device:

```bash
adb install build/outputs/apk/debug/aotts-debug.apk
```

After installing, go to Settings → Accessibility → Text-to-speech output and select aotts as the engine.

### Manual testing checklist

Go through each item before submitting your pull request:

- The app builds without errors or warnings (including the NDK build)
- aotts appears in the TTS engine list in Android Settings
- All six languages (English US, English GB, German, Spanish, French, Italian) produce speech
- Speech rate and pitch controls in Settings take effect immediately
- The engine does not crash or produce silence on long strings
- The engine does not crash when the system language does not match any available voice

---

## Step 7 — Commit your changes

Write a commit message that clearly explains what changed and why.

```bash
git add .
git commit -m "fix: Italian voice fails silently when lang file path is wrong

The asset path for the Italian .bin file had a typo introduced in
the last refactor. Fixed the path and added a check that logs an
error if the file is missing at startup."
```

Commit message format:

```
type: short summary in plain English

Longer explanation if needed. Explain the problem, not just the fix.
```

Types: `fix`, `feature`, `docs`, `refactor`, `build`

---

## Step 8 — Push and open a pull request

```bash
git push origin your-branch-name
```

Then:

1. Open github.com/YOUR-USERNAME/aotts
2. GitHub shows a bar saying your branch was recently pushed
3. Activate Compare and pull request
4. Fill in the title: one sentence describing the change
5. Fill in the description: what problem does this solve, how did you test it
6. Activate Create pull request

---

## Reporting a bug or requesting a feature

You do not need to know how to code to do this. It is one of the most valuable contributions.

1. Open github.com/tech-master33/aotts/issues
2. Activate New issue
3. Choose Bug report or Feature request
4. Fill in the title with one short sentence describing the problem or request
5. In the body, include:
   - What language and voice you were using
   - What you were trying to do
   - What happened instead of what you expected
   - Your Android version and device model
   - Whether the problem happens with all languages or only one

---

## Code review process

After you open a pull request:

1. A maintainer will read your changes and may ask questions in the comments
2. Reply to comments — activate the Resolve conversation button once you have addressed the point
3. If changes are requested, push new commits to the same branch — the pull request updates automatically
4. Once approved, a maintainer will merge your pull request

Most pull requests receive a first response within a few days.
If you have not heard back after a week, add a comment to the pull request to ask for an update.

---

## Community and questions

- Discussions: github.com/tech-master33/aotts/discussions
- Issues: github.com/tech-master33/aotts/issues
- Screen reader: github.com/tech-master33/andrdscren
- Launcher: github.com/tech-master33/aoler
- BAOSP main project: github.com/tech-master33/baosp

Open a discussion if you have a question. Describe what you are trying to do and where you are stuck.
You do not need to have a solution — questions about how things work are welcome.
