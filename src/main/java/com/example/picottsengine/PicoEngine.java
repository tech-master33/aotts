package com.example.picottsengine;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class PicoEngine {

    private static final String TAG = "PicoEngine";
    private Context context;
    private Map<String, String> loadedLanguages;
    private volatile boolean isStopped = false;
    private boolean isInitialized = false;

    private static final Map<String, String> VOICE_DATA = new HashMap<String, String>() {{
        put("en_US", "lang/en-US_lhp.bin");
        put("en_GB", "lang/en-GB_lhp.bin");
        put("de_DE", "lang/de-DE_lhp.bin");
        put("es_ES", "lang/es-ES_lhp.bin");
        put("fr_FR", "lang/fr-FR_lhp.bin");
        put("it_IT", "lang/it-IT_lhp.bin");
    }};

    public PicoEngine(Context context) {
        this.context = context;
        this.loadedLanguages = new HashMap<>();
        initializePico();
    }

    /**
     * Initialize Pico native system
     */
    private void initializePico() {
        try {
            if (PicoNative.initPico()) {
                isInitialized = true;
                Log.d(TAG, "Pico system initialized successfully");
                extractLanguageAssets();
            } else {
                Log.e(TAG, "Failed to initialize Pico system");
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception initializing Pico", e);
        }
    }

    /**
     * Extract language assets from APK
     */
    private void extractLanguageAssets() {
        try {
            AssetManager assetManager = context.getAssets();
            File cacheDir = context.getCacheDir();
            
            for (String voiceFile : VOICE_DATA.values()) {
                String fileName = voiceFile.substring(voiceFile.lastIndexOf('/') + 1);
                File outputFile = new File(cacheDir, fileName);
                
                if (!outputFile.exists()) {
                    try (InputStream input = assetManager.open(voiceFile);
                         FileOutputStream output = new FileOutputStream(outputFile)) {
                        
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = input.read(buffer)) > 0) {
                            output.write(buffer, 0, length);
                        }
                        Log.d(TAG, "Extracted: " + fileName);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error extracting language assets", e);
        }
    }

    /**
     * Load language
     */
    public boolean loadLanguage(String lang, String country) {
        if (!isInitialized) {
            Log.e(TAG, "Pico not initialized");
            return false;
        }

        String langKey = lang + "_" + country;
        if (loadedLanguages.containsKey(langKey)) {
            return true;
        }

        try {
            String voiceFile = VOICE_DATA.get(langKey);
            if (voiceFile == null) {
                Log.e(TAG, "Voice file not found for: " + langKey);
                return false;
            }

            File cacheDir = context.getCacheDir();
            String fileName = voiceFile.substring(voiceFile.lastIndexOf('/') + 1);
            File langPath = new File(cacheDir, fileName);

            if (PicoNative.loadLanguage(langPath.getAbsolutePath())) {
                loadedLanguages.put(langKey, langPath.getAbsolutePath());
                Log.d(TAG, "Language loaded: " + langKey);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading language", e);
        }

        return false;
    }

    /**
     * Synthesize text using native Pico library
     */
    public byte[] synthesize(String text, float pitch, float speechRate) {
        if (!isInitialized || text == null || text.isEmpty()) {
            return new byte[0];
        }

        try {
            if (!PicoNative.createEngine("en-US")) {
                Log.e(TAG, "Failed to create engine");
                return new byte[0];
            }

            byte[] audio = PicoNative.synthesize(text);
            Log.d(TAG, "Synthesis complete. Audio size: " + (audio != null ? audio.length : 0));
            
            return audio != null ? audio : new byte[0];
        } catch (Exception e) {
            Log.e(TAG, "Synthesis error", e);
            return new byte[0];
        }
    }

    /**
     * Stop synthesis
     */
    public void stop() {
        isStopped = true;
    }

    /**
     * Cleanup
     */
    public void shutdown() {
        try {
            PicoNative.cleanup();
            loadedLanguages.clear();
            isInitialized = false;
            Log.d(TAG, "Engine shutdown");
        } catch (Exception e) {
            Log.e(TAG, "Error during shutdown", e);
        }
    }
}