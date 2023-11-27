#include <jni.h>
#include <string>
#include <oboe/Oboe.h>
#include <flowgraph/resampler/MultiChannelResampler.h>
#include <player/OneShotSampleSource.h>
#include "player/SimpleMultiPlayer.h"

//static iolib::SimpleMultiPlayer sDTPlayer;
static oboe::resampler::MultiChannelResampler *a = oboe::resampler::MultiChannelResampler::make(
        1,
        2,
        3,
        oboe::resampler::MultiChannelResampler::Quality::Medium
);

extern "C" JNIEXPORT jstring JNICALL
Java_com_slaviboy_audio_NativeLib_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}