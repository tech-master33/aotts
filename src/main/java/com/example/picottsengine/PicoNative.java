package com.example.picottsengine;

public class PicoNative {
    static {
        System.loadLibrary("pico_jni");
        System.loadLibrary("ttspico");
    }

    /**
     * Initialize Pico system
     */
    public static native boolean initPico();

    /**
     * Load language resource
     */
    public static native boolean loadLanguage(String langPath);

    /**
     * Create TTS engine
     */
    public static native boolean createEngine(String resourceName);

    /**
     * Synthesize text to audio
     */
    public static native byte[] synthesize(String text);

    /**
     * Cleanup
     */
    public static native void cleanup();
}