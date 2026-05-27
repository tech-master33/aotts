# SVOX Pico TTS Engine for Android 8.0+

A complete Android Text-to-Speech engine implementation using SVOX Pico TTS library, compatible with Android 8.0 and higher.

## Features

✅ **SVOX Pico TTS Engine** - Lightweight speech synthesis
✅ **Android 8.0+ Support** - Works on modern Android versions
✅ **Multiple Languages** - English (US/GB), German, Spanish, French, Italian
✅ **JNI Integration** - Native C bindings for optimal performance
✅ **TalkBack Compatible** - Full accessibility support
✅ **System-Level TTS** - Registers as default TTS engine

## Installation

### Prerequisites

- Android Studio 4.0+
- Android NDK 23.1+
- Android SDK 32+
- CMake 3.18+

### Build Instructions

1. Clone the repository:
```bash
git clone https://github.com/tech-master33/aotts.git
cd aotts
```

2. Download AOSP Pico source:
```bash
cd src/main/cpp
git clone https://github.com/aosp-mirror/platform_external_svox.git pico
cd ../..
```

3. Add language data files to `src/main/assets/lang/`:
   - en-US_lhp.bin
   - en-GB_lhp.bin
   - de-DE_lhp.bin
   - es-ES_lhp.bin
   - fr-FR_lhp.bin
   - it-IT_lhp.bin

4. Build the APK:
```bash
./gradlew clean build
```

5. Install:
```bash
./gradlew installDebug
```

## Configuration

To set as default TTS engine:

1. Go to **Settings → Accessibility → Text-to-Speech output**
2. Select **Pico TTS Engine** from available engines
3. Adjust speech rate and pitch as needed

## Project Structure

```
aotts/
├── src/main/
│   ├── cpp/
│   │   ├── CMakeLists.txt          # CMake build configuration
│   │   ├── pico_jni.c              # JNI bindings
│   │   └── pico/                   # AOSP SVOX Pico source (clone here)
│   ├── java/
│   │   └── com/example/picottsengine/
│   │       ├── PicoNative.java     # JNI interface
│   │       ├── PicoEngine.java     # Engine wrapper
│   │       ├── PicoTtsService.java # TTS service
│   │       └── SettingsActivity.java
│   ├── assets/
│   │   └── lang/                   # Language data files (add here)
│   ├── res/
│   │   ├── xml/
│   │   │   └── tts_engine_settings.xml
│   │   └── values/
│   │       └── strings.xml
│   └── AndroidManifest.xml
├── build.gradle
└── README.md
```

## Obtaining Language Data

Pico TTS language data files can be obtained from:

1. **AOSP Builds**: Extract from older Android ROMs (pre-Android 6.0)
2. **Pico TTS Package**: Download from older device backups
3. **Build from Source**: Compile from AOSP with proper language packs

Language files should be placed in: `src/main/assets/lang/`

## Development

### Adding New Languages

1. Add language file to `src/main/assets/lang/`
2. Update `VOICE_DATA` map in `PicoEngine.java`
3. Add language entry to `tts_engine_settings.xml`
4. Rebuild and reinstall

### Debugging

View logs with:
```bash
adb logcat | grep PicoTTS
```

## Supported Languages

| Language | Locale | Status |
|----------|--------|--------|
| English (US) | en-US | ✅ Supported |
| English (GB) | en-GB | ✅ Supported |
| German | de-DE | ✅ Supported |
| Spanish | es-ES | ✅ Supported |
| French | fr-FR | ✅ Supported |
| Italian | it-IT | ✅ Supported |

## Compatibility

- **Minimum SDK**: API 21 (Android 5.0)
- **Target SDK**: API 32 (Android 12)
- **Tested on**: Android 8.0, 9.0, 10, 11, 12

## License

SVOX Pico TTS is licensed under the Apache License 2.0
This project wrapper is open source.

## References

- [AOSP SVOX Pico](https://android.googlesource.com/platform/external/svox/+/refs/heads/master/pico/)
- [Android TTS Service](https://developer.android.com/reference/android/speech/tts/TextToSpeechService)
- [Android NDK](https://developer.android.com/ndk)

## Support

For issues and questions, please open an issue on GitHub.