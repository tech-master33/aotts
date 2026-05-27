cmake_minimum_required(VERSION 3.18.1)
project(picotts)

set(PICO_SRC_DIR "${CMAKE_CURRENT_SOURCE_DIR}/pico")

# Pico TTS Library
add_library(ttspico SHARED
    ${PICO_SRC_DIR}/lib/picoapi.c
    ${PICO_SRC_DIR}/lib/picodata.c
    ${PICO_SRC_DIR}/lib/picodsp.c
    ${PICO_SRC_DIR}/lib/picofftsg.c
    ${PICO_SRC_DIR}/lib/picokdt.c
    ${PICO_SRC_DIR}/lib/picokpdf.c
    ${PICO_SRC_DIR}/lib/picokpr.c
    ${PICO_SRC_DIR}/lib/picoos.c
    ${PICO_SRC_DIR}/lib/picopal.c
    ${PICO_SRC_DIR}/lib/picostrdup.c
    ${PICO_SRC_DIR}/lib/picosig.c
    ${PICO_SRC_DIR}/lib/picosigpr.c
    ${PICO_SRC_DIR}/lib/picotok.c
    ${PICO_SRC_DIR}/lib/picoparse.c
    ${PICO_SRC_DIR}/lib/picodb.c
    ${PICO_SRC_DIR}/lib/picoextapi.c
    ${PICO_SRC_DIR}/lib/picounits.c
    ${PICO_SRC_DIR}/lib/pico_internal.c
    pico_jni.c
)

target_include_directories(ttspico PRIVATE
    ${PICO_SRC_DIR}/lib
    ${PICO_SRC_DIR}/lib/inc
    ${ANDROID_NDK}/toolchains/llvm/prebuilt/${ANDROID_PLATFORM}/sysroot/usr/include
)

target_link_libraries(ttspico
    log
)

set_target_properties(ttspico PROPERTIES
    C_STANDARD 99
    SOVERSION 1
)

# JNI Bridge Library
add_library(pico_jni SHARED pico_jni.c)
target_include_directories(pico_jni PRIVATE
    ${PICO_SRC_DIR}/lib
    ${PICO_SRC_DIR}/lib/inc
)
target_link_libraries(pico_jni ttspico log)