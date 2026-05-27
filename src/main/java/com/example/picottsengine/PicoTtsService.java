package com.example.picottsengine;

import android.media.AudioFormat;
import android.media.AudioTrack;
import android.speech.tts.SynthesisCallback;
import android.speech.tts.SynthesisRequest;
import android.speech.tts.TextToSpeechService;
import android.util.Log;

import java.util.Locale;

public class PicoTtsService extends TextToSpeechService {

    private static final String TAG = "PicoTtsService";
    private PicoEngine picoEngine;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "TTS Service created");
        picoEngine = new PicoEngine(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (picoEngine != null) {
            picoEngine.shutdown();
        }
    }

    /**
     * Check if a specific language is available
     */
    @Override
    protected int onIsLanguageAvailable(String lang, String country, String variant) {
        Log.d(TAG, "Checking language: " + lang + "-" + country);
        
        // Supported Pico languages: en-US, en-GB, de-DE, es-ES, fr-FR, it-IT
        if ("en".equals(lang)) {
            if ("US".equals(country) || "GB".equals(country)) {
                return LANG_AVAILABLE;
            }
        } else if ("de".equals(lang) && "DE".equals(country)) {
            return LANG_AVAILABLE;
        } else if ("es".equals(lang) && "ES".equals(country)) {
            return LANG_AVAILABLE;
        } else if ("fr".equals(lang) && "FR".equals(country)) {
            return LANG_AVAILABLE;
        } else if ("it".equals(lang) && "IT".equals(country)) {
            return LANG_AVAILABLE;
        }
        
        return LANG_NOT_SUPPORTED;
    }

    /**
     * Get supported languages
     */
    @Override
    protected String[] onGetLanguage() {
        return new String[]{
            "en_US",
            "en_GB",
            "de_DE",
            "es_ES",
            "fr_FR",
            "it_IT"
        };
    }

    /**
     * Load language data
     */
    @Override
    protected int onLoadLanguage(String lang, String country, String variant) {
        Log.d(TAG, "Loading language: " + lang + "-" + country);
        
        if (onIsLanguageAvailable(lang, country, variant) == LANG_AVAILABLE) {
            // Load Pico language data
            boolean loaded = picoEngine.loadLanguage(lang, country);
            if (loaded) {
                return LANG_AVAILABLE;
            }
        }
        return LANG_NOT_SUPPORTED;
    }

    /**
     * Main synthesis method - converts text to speech
     */
    @Override
    protected void onSynthesizeText(SynthesisRequest request, SynthesisCallback callback) {
        Log.d(TAG, "Synthesizing text: " + request.getText());
        
        String text = request.getText();
        float pitch = request.getPitch();
        float speechRate = request.getSpeechRate();
        
        try {
            // Generate audio from text
            byte[] audioData = picoEngine.synthesize(text, pitch, speechRate);
            
            if (audioData != null && audioData.length > 0) {
                // Report success and provide audio data
                callback.start(16000, AudioFormat.ENCODING_PCM_16BIT, 1);
                callback.audioAvailable(audioData);
                callback.done();
                Log.d(TAG, "Synthesis complete. Audio size: " + audioData.length);
            } else {
                callback.error("Synthesis failed");
                Log.e(TAG, "Synthesis produced no audio");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error synthesizing text", e);
            callback.error("Synthesis exception: " + e.getMessage());
        }
    }

    /**
     * Stop ongoing synthesis
     */
    @Override
    public void onStop() {
        Log.d(TAG, "Stopping synthesis");
        if (picoEngine != null) {
            picoEngine.stop();
        }
    }
}