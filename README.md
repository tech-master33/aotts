# SVOX Pico TTS Engine for Android 8.0+

[![Android CI — aotts](https://github.com/tech-master33/aotts/actions/workflows/android.yml/badge.svg)](https://github.com/tech-master33/aotts/actions/workflows/android.yml)

A complete Android Text-to-Speech engine implementation using SVOX Pico TTS library, compatible with Android 8.0 and higher. Built as part of the [BAOSP project](https://github.com/tech-master33/baosp).

## Download

**Latest APK → [github.com/tech-master33/baosp/releases/tag/nightly](https://github.com/tech-master33/baosp/releases/tag/nightly)**

A fresh build is posted there automatically every night alongside the screen reader and launcher.
You can also find standalone builds on the [releases page](https://github.com/tech-master33/aotts/releases) of this repo.

## Features

✅ **SVOX Pico TTS Engine** — Lightweight speech synthesis
✅ **Android 8.0+ Support** — Works on modern Android versions
✅ **Multiple Languages** — English (US/GB), German, Spanish, French, Italian
✅ **JNI Integration** — Native C bindings for optimal performance
✅ **TalkBack Compatible** — Full accessibility support
✅ **System-Level TTS** — Registers as default TTS engine
✅ **Works with andrdscren** — Paired with the BAOSP screen reader for full accessible stack

## Installing on your device

1. Download the APK from the nightly link above
2. Transfer it to your Android device
3. Install it — allow "unknown sources" if prompted
4. Go to **Settings → Language & input → Text-to-speech output**
5. Select **AOTTS Pico** as your engine

For the best experience, also install [andrdscren](https://github.com/tech-master33/andrdscren) (the BAOSP screen reader) and [aoler](https://github.com/tech-master33/aoler) (the BAOSP launcher) from the same nightly bundle.

## Building locally

The build requires SVOX Pico C source and language data — the CI downloads these
automatically. To build locally:

```bash
git clone https://github.com/tech-master33/aotts.git
cd aotts

# Download SVOX Pico native source
git clone --depth=1 https://android.googlesource.com/platform/external/svox /tmp/svox
cp -r /tmp/svox/pico/* src/main/cpp/pico/

# Download language data
mkdir -p src/main/assets/lang
curl -L "https://android.googlesource.com/platform/external/svox/+archive/refs/heads/master/pico/lang.tar.gz" \
  | tar -xz -C src/main/assets/lang/

# Build
chmod +x gradlew
./gradlew assembleDebug -Pandroid.ndkVersion=23.1.7779620
```

## Supported Languages

| Language | Locale |
|----------|--------|
| English (US) | en-US |
| English (GB) | en-GB |
| German | de-DE |
| Spanish | es-ES |
| French | fr-FR |
| Italian | it-IT |

## CI/CD

Every push to `main` automatically builds a new APK and posts it as a GitHub Release.
The badge above shows whether the latest build passed or failed.

## BAOSP Ecosystem

aotts is one of four repos that make up BAOSP — an accessible Android platform for blind and visually impaired users:

| Repo | What it does |
|------|-------------|
| [baosp](https://github.com/tech-master33/baosp) | Main project — nightly bundle, AOSP patches, release coordination |
| [andrdscren](https://github.com/tech-master33/andrdscren) | Screen reader — accessibility service |
| **[aotts](https://github.com/tech-master33/aotts)** | **SVOX Pico TTS engine (this repo)** |
| [aoler](https://github.com/tech-master33/aoler) | Accessible home screen launcher |

All four APKs are bundled together and published every night at  
**[github.com/tech-master33/baosp/releases/tag/nightly](https://github.com/tech-master33/baosp/releases/tag/nightly)**

## License

SVOX Pico TTS is licensed under the Apache License 2.0.
