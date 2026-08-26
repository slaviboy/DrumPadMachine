/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <android/log.h>

// parselib includes
#include <stream/MemInputStream.h>
#include <wav/WavStreamReader.h>

// local includes
#include "OneShotSampleSource.h"
#include "SimpleMultiPlayer.h"

static const char* TAG = "SimpleMultiPlayer";

using namespace oboe;
using namespace parselib;

namespace iolib {

constexpr int32_t kBufferSizeInBursts = 2; // Use 2 bursts as the buffer size (double buffer)

SimpleMultiPlayer::SimpleMultiPlayer()
  : mChannelCount(0), mOutputReset(false), mSampleRate(0), mNumSampleBuffers(0)
{}

DataCallbackResult SimpleMultiPlayer::MyDataCallback::onAudioReady(AudioStream *oboeStream,
                                                                   void *audioData,
                                                                   int32_t numFrames) {

    StreamState streamState = oboeStream->getState();
    if (streamState != StreamState::Open && streamState != StreamState::Started) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "  streamState:%d", streamState);
    }
    if (streamState == StreamState::Disconnected) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "  streamState::Disconnected");
    }

    memset(audioData, 0, static_cast<size_t>(numFrames) * static_cast<size_t>
            (mParent->mChannelCount) * sizeof(float));

    // OneShotSampleSource* sources = mSampleSources.get();
    for(int32_t index = 0; index < mParent->mNumSampleBuffers; index++) {
        if (mParent->mSampleSources[index]->isPlaying()) {
            mParent->mSampleSources[index]->mixAudio((float*)audioData, mParent->mChannelCount,
                                                     numFrames);
        }
    }

    float* outBuffer = (float*)audioData;
    size_t numSamples = static_cast<size_t>(numFrames) * static_cast<size_t>(mParent->mChannelCount);

    if (mParent->mReverbMix > 0.0f) {
        if (mParent->mReverbScratch.size() < numSamples) {
            mParent->mReverbScratch.resize(numSamples);
        }
        mParent->mReverb.process(outBuffer, mParent->mReverbScratch.data(), numFrames,
                                  mParent->mChannelCount);
        float wet = mParent->mReverbMix;
        float dry = 1.0f - wet;
        for (size_t i = 0; i < numSamples; i++) {
            outBuffer[i] = (outBuffer[i] * dry) + (mParent->mReverbScratch[i] * wet);
        }
    }

    float masterGain = mParent->mMasterGain;
    float pan = mParent->mMasterPan;
    float leftGain = pan <= 0.0f ? 1.0f : 1.0f - pan;
    float rightGain = pan >= 0.0f ? 1.0f : 1.0f + pan;
    for (size_t i = 0; i < numSamples; i++) {
        bool isLeftChannel = (i % mParent->mChannelCount) == 0;
        float sample = outBuffer[i] * masterGain * (isLeftChannel ? leftGain : rightGain);
        // cheap safety limiter - master gain can go above unity (boost)
        if (sample > 1.0f) {
            sample = 1.0f;
        } else if (sample < -1.0f) {
            sample = -1.0f;
        }
        outBuffer[i] = sample;
    }

    return DataCallbackResult::Continue;
}

void SimpleMultiPlayer::MyErrorCallback::onErrorAfterClose(AudioStream *oboeStream, Result error) {
    __android_log_print(ANDROID_LOG_INFO, TAG, "==== onErrorAfterClose() error:%d", error);

    mParent->resetAll();
    if (mParent->openStream() && mParent->startStream()) {
        mParent->mOutputReset = true;
    }
}

bool SimpleMultiPlayer::openStream() {
    __android_log_print(ANDROID_LOG_INFO, TAG, "openStream()");

    // Use shared_ptr to prevent use of a deleted callback.
    mDataCallback = std::make_shared<MyDataCallback>(this);
    mErrorCallback = std::make_shared<MyErrorCallback>(this);

    // Create an audio stream
    AudioStreamBuilder builder;
    builder.setChannelCount(mChannelCount);
    // we will resample source data to device rate, so take default sample rate
    builder.setDataCallback(mDataCallback);
    builder.setErrorCallback(mErrorCallback);
    builder.setPerformanceMode(PerformanceMode::LowLatency);
    builder.setSharingMode(SharingMode::Exclusive);
    builder.setSampleRateConversionQuality(SampleRateConversionQuality::Medium);

    // Request AAudio explicitly for the lowest latency path. AAudio only exists on
    // API 26+; on older devices (minSdk is 21) this open will fail and we retry
    // below with Unspecified, which lets Oboe fall back to OpenSL ES.
    builder.setAudioApi(AudioApi::AAudio);

    Result result = builder.openStream(mAudioStream);
    if (result != Result::OK) {
        __android_log_print(
                ANDROID_LOG_WARN,
                TAG,
                "openStream with AAudio failed. Error: %s. Retrying with OpenSL ES fallback.",
                convertToText(result));
        builder.setAudioApi(AudioApi::Unspecified);
        result = builder.openStream(mAudioStream);
    }
    if (result != Result::OK){
        __android_log_print(
                ANDROID_LOG_ERROR,
                TAG,
                "openStream failed. Error: %s", convertToText(result));
        return false;
    }

    // Reduce stream latency by setting the buffer size to a multiple of the burst size
    // Note: this will fail with ErrorUnimplemented if we are using a callback with OpenSL ES
    // (i.e. on API < 26 where AAudio is unavailable) - expected there, not a bug.
    // See oboe::AudioStreamBuffered::setBufferSizeInFrames
    result = mAudioStream->setBufferSizeInFrames(
            mAudioStream->getFramesPerBurst() * kBufferSizeInBursts);
    if (result != Result::OK) {
        __android_log_print(
                ANDROID_LOG_WARN,
                TAG,
                "setBufferSizeInFrames failed. Error: %s", convertToText(result));
    }

    mSampleRate = mAudioStream->getSampleRate();
    mReverb.init(mSampleRate);

    return true;
}

bool SimpleMultiPlayer::startStream() {
    int tryCount = 0;
    while (tryCount < 3) {
        bool wasOpenSuccessful = true;
        // Assume that apenStream() was called successfully before startStream() call.
        if (tryCount > 0) {
            usleep(20 * 1000); // Sleep between tries to give the system time to settle.
            wasOpenSuccessful = openStream(); // Try to open the stream again after the first try.
        }
        if (wasOpenSuccessful) {
            Result result = mAudioStream->requestStart();
            if (result != Result::OK){
                __android_log_print(
                        ANDROID_LOG_ERROR,
                        TAG,
                        "requestStart failed. Error: %s", convertToText(result));
                mAudioStream->close();
                mAudioStream.reset();
            } else {
                return true;
            }
        }
        tryCount++;
    }

    return false;
}

void SimpleMultiPlayer::setupAudioStream(int32_t channelCount) {
    __android_log_print(ANDROID_LOG_INFO, TAG, "setupAudioStream()");
    mChannelCount = channelCount;

    openStream();
}

void SimpleMultiPlayer::teardownAudioStream() {
    __android_log_print(ANDROID_LOG_INFO, TAG, "teardownAudioStream()");
    // tear down the player
    if (mAudioStream) {
        mAudioStream->stop();
        mAudioStream->close();
        mAudioStream.reset();
    }
}

void SimpleMultiPlayer::addSampleSource(SampleSource* source, SampleBuffer* buffer) {
    buffer->resampleData(mSampleRate);

    mSampleBuffers.push_back(buffer);
    mSampleSources.push_back(source);
    mNumSampleBuffers++;
}

void SimpleMultiPlayer::unloadSampleData() {
    __android_log_print(ANDROID_LOG_INFO, TAG, "unloadSampleData()");
    resetAll();

    for (int32_t bufferIndex = 0; bufferIndex < mNumSampleBuffers; bufferIndex++) {
        delete mSampleBuffers[bufferIndex];
        delete mSampleSources[bufferIndex];
    }

    mSampleBuffers.clear();
    mSampleSources.clear();

    mNumSampleBuffers = 0;
}

void SimpleMultiPlayer::triggerDown(int32_t index) {
    if (index < mNumSampleBuffers) {
        mSampleSources[index]->setPlayMode();
    }
}

void SimpleMultiPlayer::triggerUp(int32_t index) {
    if (index < mNumSampleBuffers) {
        mSampleSources[index]->setStopMode();
    }
}

void SimpleMultiPlayer::resetAll() {
    for (int32_t bufferIndex = 0; bufferIndex < mNumSampleBuffers; bufferIndex++) {
        mSampleSources[bufferIndex]->setStopMode();
    }
}

void SimpleMultiPlayer::setPan(int index, float pan) {
    mSampleSources[index]->setPan(pan);
}

float SimpleMultiPlayer::getPan(int index) {
    return mSampleSources[index]->getPan();
}

void SimpleMultiPlayer::setGain(int index, float gain) {
    mSampleSources[index]->setGain(gain);
}

float SimpleMultiPlayer::getGain(int index) {
    return mSampleSources[index]->getGain();
}

void SimpleMultiPlayer::setMasterGain(float gain) {
    mMasterGain = gain;
}

void SimpleMultiPlayer::setReverbMix(float mix) {
    mReverbMix = mix;
}

void SimpleMultiPlayer::setMasterPan(float pan) {
    mMasterPan = pan;
}

}
