#include <jni.h>
#include <string.h>
#include <android/log.h>
#include <stdlib.h>
#include "picoapi.h"

#define LOG_TAG "PicoTTS"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

static pico_System picoSystem = NULL;
static pico_Engine picoEngine = NULL;
static const int BUFFER_SIZE = 128;

/**
 * Initialize Pico system
 */
JNIEXPORT jboolean JNICALL
Java_com_example_picottsengine_PicoNative_initPico(JNIEnv *env, jclass clazz) {
    pico_Status status = PICO_OK;
    
    LOGI("Initializing Pico TTS system...");
    
    // Create system
    status = pico_new(&picoSystem);
    if (PICO_OK != status) {
        LOGE("Failed to create Pico system: %d", status);
        return JNI_FALSE;
    }
    
    LOGI("Pico system created successfully");
    return JNI_TRUE;
}

/**
 * Load language/voice data
 */
JNIEXPORT jboolean JNICALL
Java_com_example_picottsengine_PicoNative_loadLanguage(JNIEnv *env, jclass clazz,
                                                        jstring lang_path) {
    pico_Status status = PICO_OK;
    pico_Resource picoResource = NULL;
    const char *langPath = (*env)->GetStringUTFChars(env, lang_path, 0);
    
    LOGI("Loading language from: %s", langPath);
    
    if (!picoSystem) {
        LOGE("Pico system not initialized");
        (*env)->ReleaseStringUTFChars(env, lang_path, langPath);
        return JNI_FALSE;
    }
    
    // Load language resource
    status = pico_loadResource(picoSystem, langPath, &picoResource);
    if (PICO_OK != status) {
        LOGE("Failed to load language resource: %d", status);
        (*env)->ReleaseStringUTFChars(env, lang_path, langPath);
        return JNI_FALSE;
    }
    
    (*env)->ReleaseStringUTFChars(env, lang_path, langPath);
    LOGI("Language loaded successfully");
    return JNI_TRUE;
}

/**
 * Create TTS engine
 */
JNIEXPORT jboolean JNICALL
Java_com_example_picottsengine_PicoNative_createEngine(JNIEnv *env, jclass clazz,
                                                        jstring resource_name) {
    pico_Status status = PICO_OK;
    const char *resourceName = (*env)->GetStringUTFChars(env, resource_name, 0);
    
    LOGI("Creating engine with resource: %s", resourceName);
    
    if (!picoSystem) {
        LOGE("Pico system not initialized");
        (*env)->ReleaseStringUTFChars(env, resource_name, resourceName);
        return JNI_FALSE;
    }
    
    // Create engine
    status = pico_newEngine(picoSystem, resourceName, &picoEngine);
    if (PICO_OK != status) {
        LOGE("Failed to create engine: %d", status);
        (*env)->ReleaseStringUTFChars(env, resource_name, resourceName);
        return JNI_FALSE;
    }
    
    (*env)->ReleaseStringUTFChars(env, resource_name, resourceName);
    LOGI("Engine created successfully");
    return JNI_TRUE;
}

/**
 * Synthesize text to audio
 */
JNIEXPORT jbyteArray JNICALL
Java_com_example_picottsengine_PicoNative_synthesize(JNIEnv *env, jclass clazz,
                                                      jstring text) {
    pico_Status status = PICO_OK;
    const char *inputText = (*env)->GetStringUTFChars(env, text, 0);
    
    LOGI("Synthesizing text: %s", inputText);
    
    if (!picoEngine) {
        LOGE("Pico engine not initialized");
        (*env)->ReleaseStringUTFChars(env, text, inputText);
        return NULL;
    }
    
    // Process text
    status = pico_putTextUtf8(picoEngine, (const pico_Char *)inputText, strlen(inputText), &inputText);
    if (PICO_OK != status) {
        LOGE("Failed to put text: %d", status);
        (*env)->ReleaseStringUTFChars(env, text, inputText);
        return NULL;
    }
    
    (*env)->ReleaseStringUTFChars(env, text, inputText);
    
    // Get synthesized audio
    pico_Char outBuf[BUFFER_SIZE];
    pico_Int16 bytesWritten = 0;
    pico_Char *outputBuffer = (pico_Char *)malloc(BUFFER_SIZE * 100);
    if (!outputBuffer) {
        LOGE("Memory allocation failed");
        return NULL;
    }
    
    int totalSize = 0;
    
    while (PICO_OK == status) {
        status = pico_getData(picoEngine, (void *)outBuf, BUFFER_SIZE,
                            &bytesWritten, NULL);
        
        if (bytesWritten > 0 && totalSize + bytesWritten <= BUFFER_SIZE * 100) {
            memcpy(outputBuffer + totalSize, outBuf, bytesWritten);
            totalSize += bytesWritten;
        }
    }
    
    LOGI("Synthesis complete. Generated %d bytes", totalSize);
    
    // Convert to byte array
    jbyteArray result = (*env)->NewByteArray(env, totalSize);
    if (result != NULL) {
        (*env)->SetByteArrayRegion(env, result, 0, totalSize, (jbyte *)outputBuffer);
    }
    
    free(outputBuffer);
    return result;
}

/**
 * Cleanup Pico engine
 */
JNIEXPORT void JNICALL
Java_com_example_picottsengine_PicoNative_cleanup(JNIEnv *env, jclass clazz) {
    LOGI("Cleaning up Pico TTS");
    
    if (picoEngine) {
        pico_disposeEngine(picoSystem, &picoEngine);
        picoEngine = NULL;
    }
    
    if (picoSystem) {
        pico_delete(&picoSystem);
        picoSystem = NULL;
    }
}