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

#ifndef _PLAYER_REVERB_
#define _PLAYER_REVERB_

#include <cstdint>
#include <vector>

namespace iolib {

/**
 * A single feedback comb filter with a one-pole damping filter in the feedback
 * path. One of the two building blocks of the classic Freeverb algorithm.
 */
class CombFilter {
public:
    void init(int32_t delayLengthInSamples, float feedback, float damp);
    float process(float input);

private:
    std::vector<float> mBuffer;
    int32_t mBufferIndex = 0;
    float mFeedback = 0.0f;
    float mDamp1 = 0.0f;
    float mDamp2 = 0.0f;
    float mFilterStore = 0.0f;
};

/**
 * A single allpass filter. The second building block of the classic Freeverb
 * algorithm - a bank of these run in series after the comb filters.
 */
class AllpassFilter {
public:
    void init(int32_t delayLengthInSamples, float feedback);
    float process(float input);

private:
    std::vector<float> mBuffer;
    int32_t mBufferIndex = 0;
    float mFeedback = 0.0f;
};

/**
 * A lightweight Freeverb-style algorithmic reverb: for each channel, 8 comb
 * filters run in parallel and are summed, then fed through 4 allpass filters
 * in series. Room size / damping are fixed; only the overall wet signal is
 * produced - the caller is responsible for blending it with the dry signal.
 */
class Reverb {
public:
    static constexpr int32_t kNumCombs = 8;
    static constexpr int32_t kNumAllpasses = 4;

    /** (Re)sizes the internal delay lines for the given sample rate. */
    void init(int32_t sampleRate);

    /**
     * Processes numFrames of interleaved audio in dryIn (channelCount channels)
     * and writes the wet (reverb-only) signal into wetOut, which must be at
     * least as large as dryIn.
     */
    void process(const float* dryIn, float* wetOut, int32_t numFrames, int32_t channelCount);

private:
    CombFilter mCombsLeft[kNumCombs];
    CombFilter mCombsRight[kNumCombs];
    AllpassFilter mAllpassesLeft[kNumAllpasses];
    AllpassFilter mAllpassesRight[kNumAllpasses];
};

} // namespace iolib

#endif //_PLAYER_REVERB_
