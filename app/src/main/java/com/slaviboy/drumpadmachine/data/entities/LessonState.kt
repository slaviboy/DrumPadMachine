package com.slaviboy.drumpadmachine.data.entities

import androidx.annotation.StringRes
import com.slaviboy.drumpadmachine.R

enum class LessonState(@StringRes val textResId: Int) {
    Replay(R.string.replay),
    Play(R.string.play),
    Unlock(R.string.unlock);
}