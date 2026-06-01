# aotts — ProGuard / R8 rules

# ── TTS service and JNI bridge must survive shrinking ─────────────────────
# The Android TTS framework binds the service by class name.
# JNI methods must keep their exact signatures or native calls will crash.
-keep class org.baosp.aotts.** { *; }
-keep class com.example.picottsengine.** { *; }

# ── Keep all native (JNI) method signatures intact ────────────────────────
-keepclasseswithmembernames class * {
    native <methods>;
}

# ── TTS service registration ───────────────────────────────────────────────
-keep public class * extends android.speech.tts.TextToSpeech$Engine
-keep public class * extends android.app.Service

# ── Kotlin metadata ────────────────────────────────────────────────────────
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keepattributes Signature
-keepattributes Exceptions

# ── Suppress known-safe warnings ──────────────────────────────────────────
-dontwarn kotlin.**
