package com.slaviboy.drumpadmachine.screens.lessonplayer.models

import android.os.Parcelable
import com.slaviboy.drumpadmachine.data.entities.LessonState
import com.slaviboy.drumpadmachine.enums.PadColor
import kotlinx.parcelize.Parcelize

enum class LessonPhase {
    Listen, Play, Result
}

enum class TapAccuracy(val scorePercent: Int) {
    Perfect(100),
    Good(75),
    Late(40),
    Missed(0)
}

data class LessonPlayerUiState(
    val phase: LessonPhase = LessonPhase.Listen,
    val page: Int = 0,
    val usedPadIndices: Set<Int> = emptySet(),
    val padColors: Map<Int, PadColor> = emptyMap(),
    val glowingPads: Set<Int> = emptySet(),
    val expectedPadIndices: Set<Int> = emptySet(),
    val tappedInCurrentEvent: Set<Int> = emptySet(),
    val listenStartedAtElapsedRealtime: Long? = null,
    val listenTotalDurationMs: Long = 0L,
    val playActivatedAtElapsedRealtime: Long? = null,
    val playTotalDurationMs: Long = 0L,
    val playActivationFraction: Float = 0f,
    val finalScorePercent: Int? = null,
    val bestScorePercent: Int = 0,
    val isPass: Boolean = false,
    val savedResultPayload: LessonResultPayload? = null
)

/** Result handed back to the lessons list when the player screen is dismissed. */
@Parcelize
data class LessonResultPayload(
    val lessonId: Int,
    val side: String,
    val lastScore: Int,
    val bestScore: Int,
    val newState: LessonState,
    val unlockedNextLessonId: Int?
) : Parcelable
