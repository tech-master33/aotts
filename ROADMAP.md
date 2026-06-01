# aotts Roadmap

This document describes what we plan to build next, why each item matters,
and what state each one is in. It is updated as things change.

If you want to work on something listed here, open an issue or discussion first
so we can coordinate and avoid duplicated effort.

If something important to you is missing, open a feature request at
github.com/tech-master33/aotts/issues — the issue templates will ask you
the right questions.

---

## How to read this document

Each item has a status:

- **Planned** — we intend to build this but have not started
- **In progress** — actively being worked on
- **Needs help** — no one is currently assigned, good place to contribute
- **Done** — shipped and in the nightly build

Items are roughly ordered by priority within each section.

---

## Language and voice support

### Additional language voices — Needs help

**What it is:** Add more language voices beyond the current six
(English US/GB, German, Spanish, French, Italian).

**Why it matters:** SVOX Pico has voices for additional languages in some AOSP
distributions. Adding them makes aotts usable for more people without requiring a
second TTS engine.

**How to contribute:** Check whether the SVOX AOSP source includes the `.bin`
language data file for the language you want to add. If it does, the change is small —
add the locale mapping in the TTS service class and the `.bin` file path to the asset
loader. You do not need to modify any C code.

**Open languages to investigate:**

| Language | SVOX Pico voice available |
|----------|--------------------------|
| Portuguese (Brazil) | Investigating |
| Portuguese (Portugal) | Investigating |
| Catalan | Investigating |
| Czech | Investigating |
| Polish | Investigating |

If you speak one of these languages and want to test the voice quality,
open an issue and we will send you a test APK.

---

### Per-app speech rate and pitch — Planned

**What it is:** Let users set a different speech rate or pitch for specific apps.

**Why it matters:** A user might want fast speech in their email app to skim messages
quickly, but slower speech in a banking app where missing a digit matters.
Right now rate and pitch are single global settings that apply everywhere.

**Proposed approach:** Store a map of package name → (rate, pitch) in SharedPreferences.
When the foreground app changes, update the TTS parameters. Add a UI in the aotts
settings screen to manage per-app overrides.

---

### Multilingual voice switching — Planned

**What it is:** When the text sent to TTS is in a different language than the current
voice, automatically switch to the matching Pico voice for that language.

**Why it matters:** If a user's phone is set to English and they receive a message in
French, the English voice reads the French words with English pronunciation — which is
often incomprehensible. Auto-switching makes multilingual content understandable.

**Dependencies:** Requires the andrdscren screen reader (or another client) to pass
a language hint with the TTS request, or requires language detection in the aotts service
layer itself using Android's `TextClassifier`.

---

### Voice preview in settings — Needs help

**What it is:** A button in the aotts settings screen that speaks a sample sentence in
each available language, so the user can compare voices before choosing one.

**Why it matters:** Right now the only way to hear what a voice sounds like is to
select it, navigate out of settings, and listen to whatever app you were using.
Switching between six voices to compare them takes many steps. An in-settings preview
reduces this to one button per language.

**Proposed approach:** Add a "Hear sample" button next to each language in the settings
list. Each button speaks a fixed sentence in that language using the Pico voice.
The sentence should be the same across all languages so the comparison is fair.

**Where to start:** `SettingsActivity` — add a button row for each locale in the language
list adapter.

---

## Speech quality

### Pronunciation dictionary — Needs help

**What it is:** A list of words that SVOX Pico mispronounces, with corrected phonetic
spellings, applied before text is sent to the engine.

**Why it matters:** SVOX Pico was built in the mid-2000s. It mispronounces many modern
words — app names, websites, abbreviations, and brand names. A pronunciation dictionary
in the Kotlin layer can fix these without modifying any C code.

**How to contribute:** Open an issue with a word that sounds wrong and what it should
sound like phonetically. You do not need to know how to code to contribute to this —
the dictionary is a data file, and collecting the words that need fixing is the hardest
part.

**Example entries to collect:**
- "GitHub" — often mispronounced as "gith-ub"
- "Wi-Fi" — sometimes read as "wee-fee"
- "URL" — sometimes read as one word instead of three letters

---

### Sentence boundary detection — Planned

**What it is:** Improve how aotts splits long text into sentences before sending to
the Pico C engine.

**Why it matters:** SVOX Pico clips audio if a single text chunk is too long.
The current splitting logic breaks on some edge cases — abbreviations with periods
(e.g. "Dr. Smith"), decimal numbers, and URLs. Improving the splitter prevents
unnatural pauses and cut-off words.

**Proposed approach:** Replace the simple period-based splitter with a rule-based
sentence boundary detector that handles abbreviations, numbers, and punctuation
sequences correctly.

---

### SSML support — Planned

**What it is:** Accept a subset of Speech Synthesis Markup Language (SSML) tags in input
text, allowing callers to control emphasis, pauses, and pronunciation inline.

**Why it matters:** Some Android apps send SSML-formatted text to TTS engines to add
natural-sounding pauses and stress. Currently aotts strips all markup and reads the raw
text, losing the formatting the app intended.

**Proposed approach:** Parse `<break>`, `<emphasis>`, `<say-as>`, and `<phoneme>` tags
in the Kotlin layer before passing text to Pico. Map each tag to a Pico-level equivalent
where possible.

---

## Performance and reliability

### Background audio focus handling — Needs help

**What it is:** Handle audio focus correctly when another app (music player, video,
phone call) takes audio focus while the TTS engine is speaking.

**Why it matters:** If a phone call arrives while the screen reader is speaking,
aotts and the phone call audio currently conflict. The TTS engine should pause,
let the call audio play, and resume after the call ends.

**Proposed approach:** Register an `AudioFocusRequest` in `PicoTtsService`.
On `AUDIOFOCUS_LOSS_TRANSIENT`, pause the current utterance.
On `AUDIOFOCUS_GAIN`, resume it.

**Where to start:** `PicoTtsService` — add `AudioManager.requestAudioFocus()` and the
focus change callback.

---

### NDK build reproducibility — Needs help

**What it is:** Make the CMake NDK build produce identical output every time it runs,
regardless of which CI runner or developer machine builds it.

**Why it matters:** Non-reproducible builds make it hard to verify that the APK
distributed in the nightly release matches the source code. Reproducible builds are
a basic security property for software that processes all user text.

**Proposed approach:** Pin the NDK version (already done at 23.1.7779620), set fixed
timestamps in the CMake build, and ensure no environment-specific paths are baked
into the binary.

---

## Long-term research

### Offline neural TTS voice — Long term

**What it is:** Replace SVOX Pico with a small neural TTS model that sounds more
natural while still running entirely on-device without internet.

**Why it matters:** SVOX Pico sounds robotic. Better-sounding speech reduces fatigue
for users who listen to it all day. The model must run offline because blind users
cannot be locked out of their phone when internet is unavailable.

**Candidates to evaluate:**

| Engine | License | On-device | Notes |
|--------|---------|-----------|-------|
| Piper TTS | MIT | Yes | ARM-optimised, many voices |
| Kokoro | Apache 2.0 | Yes | Small model size, good English quality |
| eSpeak NG | GPL 3.0 | Yes | More robotic than Piper but very lightweight |

**Status:** Research phase — no implementation started.
If you have experience with on-device neural TTS on Android, open a discussion.
The license must be compatible with Apache 2.0 for inclusion in BAOSP.

---

## How priorities are set

Items move up the list when:

1. More users report being blocked by the missing feature
2. A contributor volunteers to lead the work
3. A dependency (another item on this list) is completed

Items are not added to this roadmap just because they are technically interesting.
Every item here has a stated impact on blind or disabled users. If you propose a feature,
the most important thing you can say is who it helps and how.
