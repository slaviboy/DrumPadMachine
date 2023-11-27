#include <jni.h>
#include <string>

#include <flowgraph/resampler/MultiChannelResampler.h>
#include <player/OneShotSampleSource.h>

extern "C" JNIEXPORT jstring JNICALL
Java_com_slaviboy_audio_NativeLib_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}