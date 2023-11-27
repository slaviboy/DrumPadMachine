#include <jni.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>

#include <android/log.h>

// parselib includes
#include <stream/MemInputStream.h>
#include <wav/WavStreamReader.h>

// iolib includes
#include <player/OneShotSampleSource.h>
#include <player/SimpleMultiPlayer.h>

static const char* TAG = "DrumPadPlayerJNI";

// JNI functions are "C" calling convention
#ifdef __cplusplus
extern "C" {
#endif

using namespace iolib;
using namespace parselib;

static SimpleMultiPlayer sDTPlayer;

/**
 * Native (JNI) implementation of DrumPadPlayer.setupAudioStreamNative()
 */
JNIEXPORT void JNICALL Java_com_slaviboy_audio_DrumPadPlayer_setupAudioStreamNative(
        JNIEnv* env, jobject, jint numChannels) {
    __android_log_print(ANDROID_LOG_INFO, TAG, "%s", "init()");
    sDTPlayer.setupAudioStream(numChannels);
}

JNIEXPORT void JNICALL
Java_com_slaviboy_audio_DrumPadPlayer_startAudioStreamNative(
        JNIEnv *env, jobject thiz) {
    sDTPlayer.startStream();
}

/**
 * Native (JNI) implementation of DrumPadPlayer.teardownAudioStreamNative()
 */
JNIEXPORT void JNICALL Java_com_slaviboy_audio_DrumPadPlayer_teardownAudioStreamNative(JNIEnv* , jobject) {
    __android_log_print(ANDROID_LOG_INFO, TAG, "%s", "deinit()");

    // we know in this case that the sample buffers are all 1-channel, 44.1K
    sDTPlayer.teardownAudioStream();
}

/**
 * Native (JNI) implementation of DrumPadPlayer.allocSampleDataNative()
 */
/**
 * Native (JNI) implementation of DrumPadPlayer.loadWavAssetNative()
 */
JNIEXPORT void JNICALL Java_com_slaviboy_audio_DrumPadPlayer_loadWavAssetNative(
        JNIEnv* env, jobject, jbyteArray bytearray, jint index, jfloat pan) {
    int len = env->GetArrayLength (bytearray);

    unsigned char* buf = new unsigned char[len];
    env->GetByteArrayRegion (bytearray, 0, len, reinterpret_cast<jbyte*>(buf));

    MemInputStream stream(buf, len);

    WavStreamReader reader(&stream);
    reader.parse();

    reader.getNumChannels();

    SampleBuffer* sampleBuffer = new SampleBuffer();
    sampleBuffer->loadSampleData(&reader);

    OneShotSampleSource* source = new OneShotSampleSource(sampleBuffer, pan);
    sDTPlayer.addSampleSource(source, sampleBuffer);

    delete[] buf;
}

/**
 * Native (JNI) implementation of DrumPadPlayer.unloadWavAssetsNative()
 */
JNIEXPORT void JNICALL Java_com_slaviboy_audio_DrumPadPlayer_unloadWavAssetsNative(JNIEnv* env, jobject) {
    sDTPlayer.unloadSampleData();
}

/**
 * Native (JNI) implementation of DrumPadPlayer.trigger()
 */
JNIEXPORT void JNICALL Java_com_slaviboy_audio_DrumPadPlayer_trigger(JNIEnv* env, jobject, jint index) {
    sDTPlayer.triggerDown(index);
}

/**
 * Native (JNI) implementation of DrumPadPlayer.trigger()
 */
JNIEXPORT void JNICALL Java_com_slaviboy_audio_DrumPadPlayer_stopTrigger(JNIEnv* env, jobject, jint index) {
    sDTPlayer.triggerUp(index);
}

/**
 * Native (JNI) implementation of DrumPadPlayer.getOutputReset()
 */
JNIEXPORT jboolean JNICALL Java_com_slaviboy_audio_DrumPadPlayer_getOutputReset(JNIEnv*, jobject) {
    return sDTPlayer.getOutputReset();
}

/**
 * Native (JNI) implementation of DrumPadPlayer.clearOutputReset()
 */
JNIEXPORT void JNICALL Java_com_slaviboy_audio_DrumPadPlayer_clearOutputReset(JNIEnv*, jobject) {
    sDTPlayer.clearOutputReset();
}

/**
 * Native (JNI) implementation of DrumPadPlayer.restartStream()
 */
JNIEXPORT void JNICALL Java_com_slaviboy_audio_DrumPadPlayer_restartStream(JNIEnv*, jobject) {
    sDTPlayer.resetAll();
    if (sDTPlayer.openStream() && sDTPlayer.startStream()){
        __android_log_print(ANDROID_LOG_INFO, TAG, "openStream successful");
    } else {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "openStream failed");
    }
}

JNIEXPORT void JNICALL Java_com_slaviboy_audio_DrumPadPlayer_setPan(
        JNIEnv *env, jobject thiz, jint index, jfloat pan) {
    sDTPlayer.setPan(index, pan);
}

JNIEXPORT jfloat JNICALL Java_com_slaviboy_audio_DrumPadPlayer_getPan(
        JNIEnv *env, jobject thiz, jint  index) {
    return sDTPlayer.getPan(index);
}

JNIEXPORT void JNICALL Java_com_slaviboy_audio_DrumPadPlayer_setGain(
        JNIEnv *env, jobject thiz, jint  index, jfloat gain) {
    sDTPlayer.setGain(index, gain);
}

JNIEXPORT jfloat JNICALL Java_com_slaviboy_audio_DrumPadPlayer_getGain(
        JNIEnv *env, jobject thiz, jint index) {
    return sDTPlayer.getGain(index);
}

#ifdef __cplusplus
}
#endif
