/*
 * Copyright 2024 The Android Open Source Project
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

#include "Reverb.h"

#include <algorithm>

namespace iolib {

// Classic Freeverb tunings, in samples at a 44100Hz reference rate.
static const int32_t kCombTuningLeft[Reverb::kNumCombs] =
        {1116, 1188, 1277, 1356, 1422, 1491, 1557, 1617};
static const int32_t kAllpassTuningLeft[Reverb::kNumAllpasses] = {556, 441, 341, 225};
static constexpr int32_t kStereoSpread = 23;

// Fixed room character - only the wet/dry mix is exposed as a user control.
static constexpr float kCombFeedback = 0.84f;
static constexpr float kDamp = 0.2f;
static constexpr float kAllpassFeedback = 0.5f;

// Scales the mono sum fed into the 8 parallel combs so their summed energy
// doesn't overload the network (same fixed gain used by the original Freeverb).
static constexpr float kInputGain = 0.015f;

void CombFilter::init(int32_t delayLengthInSamples, float feedback, float damp) {
    mBuffer.assign(static_cast<size_t>(std::max(1, delayLengthInSamples)), 0.0f);
    mBufferIndex = 0;
    mFeedback = feedback;
    mDamp1 = damp;
    mDamp2 = 1.0f - damp;
    mFilterStore = 0.0f;
}

float CombFilter::process(float input) {
    float output = mBuffer[mBufferIndex];
    mFilterStore = (output * mDamp2) + (mFilterStore * mDamp1);
    mBuffer[mBufferIndex] = input + (mFilterStore * mFeedback);
    if (++mBufferIndex >= static_cast<int32_t>(mBuffer.size())) {
        mBufferIndex = 0;
    }
    return output;
}

void AllpassFilter::init(int32_t delayLengthInSamples, float feedback) {
    mBuffer.assign(static_cast<size_t>(std::max(1, delayLengthInSamples)), 0.0f);
    mBufferIndex = 0;
    mFeedback = feedback;
}

float AllpassFilter::process(float input) {
    float bufferedValue = mBuffer[mBufferIndex];
    float output = -input + bufferedValue;
    mBuffer[mBufferIndex] = input + (bufferedValue * mFeedback);
    if (++mBufferIndex >= static_cast<int32_t>(mBuffer.size())) {
        mBufferIndex = 0;
    }
    return output;
}

void Reverb::init(int32_t sampleRate) {
    float rateScale = static_cast<float>(sampleRate) / 44100.0f;
    for (int32_t i = 0; i < kNumCombs; i++) {
        int32_t leftLength = static_cast<int32_t>(kCombTuningLeft[i] * rateScale);
        mCombsLeft[i].init(leftLength, kCombFeedback, kDamp);
        mCombsRight[i].init(leftLength + kStereoSpread, kCombFeedback, kDamp);
    }
    for (int32_t i = 0; i < kNumAllpasses; i++) {
        int32_t leftLength = static_cast<int32_t>(kAllpassTuningLeft[i] * rateScale);
        mAllpassesLeft[i].init(leftLength, kAllpassFeedback);
        mAllpassesRight[i].init(leftLength + kStereoSpread, kAllpassFeedback);
    }
}

void Reverb::process(const float* dryIn, float* wetOut, int32_t numFrames, int32_t channelCount) {
    for (int32_t frame = 0; frame < numFrames; frame++) {
        const float* inFrame = dryIn + (frame * channelCount);
        float* outFrame = wetOut + (frame * channelCount);

        float inputL = inFrame[0];
        float inputR = channelCount > 1 ? inFrame[1] : inputL;
        float monoInput = (inputL + inputR) * kInputGain;

        float outL = 0.0f;
        float outR = 0.0f;
        for (int32_t i = 0; i < kNumCombs; i++) {
            outL += mCombsLeft[i].process(monoInput);
            outR += mCombsRight[i].process(monoInput);
        }
        for (int32_t i = 0; i < kNumAllpasses; i++) {
            outL = mAllpassesLeft[i].process(outL);
            outR = mAllpassesRight[i].process(outR);
        }

        outFrame[0] = outL;
        if (channelCount > 1) {
            outFrame[1] = outR;
        }
        for (int32_t ch = 2; ch < channelCount; ch++) {
            outFrame[ch] = 0.0f;
        }
    }
}

} // namespace iolib
