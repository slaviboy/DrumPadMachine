package com.slaviboy.drumpadmachine.screens.lessonplayer.viewmodels

import android.content.Context
import android.os.SystemClock
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slaviboy.audio.DrumPadPlayer
import com.slaviboy.drumpadmachine.api.results.Result
import com.slaviboy.drumpadmachine.data.entities.Lesson
import com.slaviboy.drumpadmachine.data.entities.Preset
import com.slaviboy.drumpadmachine.enums.PadColor
import com.slaviboy.drumpadmachine.screens.drumpad.helpers.DrumPadHelper
import com.slaviboy.drumpadmachine.screens.lessonplayer.helpers.LessonSchedule
import com.slaviboy.drumpadmachine.screens.lessonplayer.helpers.LessonScheduler
import com.slaviboy.drumpadmachine.screens.lessonplayer.helpers.ScheduledEvent
import com.slaviboy.drumpadmachine.screens.lessonplayer.models.LessonPhase
import com.slaviboy.drumpadmachine.screens.lessonplayer.models.LessonPlayerUiState
import com.slaviboy.drumpadmachine.screens.lessonplayer.models.TapAccuracy
import com.slaviboy.drumpadmachine.screens.lessonplayer.usecases.SaveLessonResultUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LessonPlayerViewModel @Inject constructor(
    private val saveLessonResultUseCase: SaveLessonResultUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    companion object {
        const val PASS_THRESHOLD_PERCENT = 60
        private const val GLOW_FLASH_DURATION_MS = 150L
    }

    private var drumPadPlayer: DrumPadPlayer? = null
    private var job: Job? = null

    private lateinit var preset: Preset
    private lateinit var lesson: Lesson
    private lateinit var schedule: LessonSchedule
    private var expectedEventIndex = 0
    private val tapResults = mutableListOf<TapAccuracy>()

    private val _uiState: MutableState<LessonPlayerUiState> = mutableStateOf(LessonPlayerUiState())
    val uiState: State<LessonPlayerUiState> = _uiState

    fun start(preset: Preset, lesson: Lesson) {
        job?.cancel()
        terminatePlayer()
        this.preset = preset
        this.lesson = lesson
        schedule = LessonScheduler.build(lesson, preset.tempo)
        expectedEventIndex = 0
        tapResults.clear()

        val usedIndices = lesson.pads.map { it.id }.toSet()
        val page = usedIndices.minOrNull()?.let { it / DrumPadHelper.numberItemsPerPage() } ?: 0
        val colors = usedIndices.associateWith { index -> mapPadColor(preset.files?.getOrNull(index)?.color) }

        _uiState.value = LessonPlayerUiState(
            phase = LessonPhase.Listen,
            page = page,
            usedPadIndices = usedIndices,
            padColors = colors,
            bestScorePercent = lesson.bestScore
        )

        job = viewModelScope.launch {
            awaitFrame()
            drumPadPlayer = DrumPadPlayer().apply {
                setupAudioStream()
                loadWavFile(
                    dirPath = "${context.cacheDir}/audio/${preset.id}",
                    filenames = preset.files?.map { it.filename } ?: listOf()
                )
                startAudioStream()
            }
            runListenPhase()
        }
    }

    fun onPadTapped(index: Int) {
        val state = _uiState.value
        if (state.phase != LessonPhase.Play) return
        if (index !in state.expectedPadIndices || index in state.tappedInCurrentEvent) return

        playSoundAtIndex(index)
        flashGlow(setOf(index))

        val event = schedule.tapEvents.getOrNull(expectedEventIndex) ?: return
        val now = SystemClock.elapsedRealtime()
        val activatedAt = state.playActivatedAtElapsedRealtime

        if (activatedAt == null) {
            // First correct tap activates the fixed clock, backdated so this pad's own
            // scheduled time lines up with "now" (matches the Listen-phase timing exactly).
            // Always scored Perfect - it's what *defines* the clock, there's nothing to
            // compare it against yet.
            val resolvedActivatedAt = now - event.timeMs
            tapResults.add(TapAccuracy.Perfect)
            val tappedNow = setOf(index)
            if (tappedNow.containsAll(event.nonAmbientPadIds)) {
                advanceToEvent(expectedEventIndex + 1, resolvedActivatedAt)
            } else {
                _uiState.value = _uiState.value.copy(
                    tappedInCurrentEvent = tappedNow,
                    playActivatedAtElapsedRealtime = resolvedActivatedAt
                )
            }
            // Metronomic scheduler: from here on the expected pad advances on the clock, not
            // on tap success, so one missed/late pad can't permanently stall scoring for the
            // rest of the lesson (see runPlayScheduler for why that mattered).
            runPlayScheduler(resolvedActivatedAt)
            return
        }

        val accuracy = LessonScheduler.accuracyFor(now - (activatedAt + event.timeMs), schedule.stepDurationMs)
        tapResults.add(accuracy)
        val tappedNow = state.tappedInCurrentEvent + index
        if (tappedNow.containsAll(event.nonAmbientPadIds)) {
            advanceToEvent(expectedEventIndex + 1, activatedAt)
        } else {
            _uiState.value = _uiState.value.copy(tappedInCurrentEvent = tappedNow)
        }
    }

    private fun advanceToEvent(newIndex: Int, activatedAt: Long) {
        expectedEventIndex = newIndex
        val next = schedule.tapEvents.getOrNull(newIndex)
        _uiState.value = _uiState.value.copy(
            expectedPadIndices = next?.nonAmbientPadIds ?: emptySet(),
            tappedInCurrentEvent = emptySet(),
            playActivatedAtElapsedRealtime = activatedAt
        )
    }

    fun onDone() {
        job?.cancel()
        terminatePlayer()
    }

    override fun onCleared() {
        job?.cancel()
        terminatePlayer()
    }

    private fun runListenPhase() {
        job = viewModelScope.launch {
            val startedAt = SystemClock.elapsedRealtime()
            _uiState.value = _uiState.value.copy(
                listenStartedAtElapsedRealtime = startedAt,
                listenTotalDurationMs = schedule.totalDurationMs
            )
            for (event in schedule.events) {
                val wait = (startedAt + event.timeMs) - SystemClock.elapsedRealtime()
                if (wait > 0) delay(wait)
                triggerEvent(event)
            }
            val remaining = schedule.totalDurationMs - (SystemClock.elapsedRealtime() - startedAt)
            if (remaining > 0) delay(remaining)
            startPlayPhase()
        }
    }

    private fun startPlayPhase() {
        expectedEventIndex = 0
        val firstExpected = schedule.tapEvents.firstOrNull()
        _uiState.value = _uiState.value.copy(
            phase = LessonPhase.Play,
            glowingPads = emptySet(),
            expectedPadIndices = firstExpected?.nonAmbientPadIds ?: emptySet(),
            tappedInCurrentEvent = emptySet(),
            playActivatedAtElapsedRealtime = null,
            playTotalDurationMs = schedule.totalDurationMs,
            playActivationFraction = firstExpected?.let {
                if (schedule.totalDurationMs <= 0) 0f else it.timeMs / schedule.totalDurationMs.toFloat()
            } ?: 0f
        )
        if (firstExpected == null) {
            // Nothing to tap in this lesson - finish immediately.
            job = viewModelScope.launch { finishLesson() }
        }
    }

    /**
     * Walks every remaining tap event on the fixed clock started at [activatedAt]. Whichever
     * event isn't fully tapped by [LessonSchedule.stepDurationMs] * 2 past its scheduled time
     * (matching [LessonScheduler.accuracyFor]'s own "Late" cutoff) has its untapped pads scored
     * Missed and the expected pad advances anyway - this is what lets the user recover after
     * dropping a beat instead of every later correct tap being silently ignored because the
     * ViewModel was still waiting on a pad they'd already missed.
     */
    private fun runPlayScheduler(activatedAt: Long) {
        job = viewModelScope.launch {
            val timeoutMs = (schedule.stepDurationMs * 2).toLong()
            while (expectedEventIndex < schedule.tapEvents.size) {
                val event = schedule.tapEvents[expectedEventIndex]
                val wait = (activatedAt + event.timeMs + timeoutMs) - SystemClock.elapsedRealtime()
                if (wait > 0) delay(wait)
                // Only act if we're still waiting on this exact event - a tap may already have
                // advanced (or partially advanced) it while we were delaying.
                val current = _uiState.value
                if (expectedEventIndex < schedule.tapEvents.size &&
                    schedule.tapEvents[expectedEventIndex] === event
                ) {
                    val stillPending = event.nonAmbientPadIds - current.tappedInCurrentEvent
                    repeat(stillPending.size) { tapResults.add(TapAccuracy.Missed) }
                    advanceToEvent(expectedEventIndex + 1, activatedAt)
                }
            }
            val remaining = schedule.totalDurationMs - (SystemClock.elapsedRealtime() - activatedAt)
            if (remaining > 0) delay(remaining)
            finishLesson()
        }
    }

    private suspend fun finishLesson() {
        // Called as the last step of the currently-running `job` coroutine (either the
        // activation timer or the empty-lesson fast path) - must NOT cancel `job` here, that
        // would self-cancel this very coroutine and abort the save below.
        val totalExpectedTaps = schedule.tapEvents.sumOf { it.nonAmbientPadIds.size }
        val missedCount = (totalExpectedTaps - tapResults.size).coerceAtLeast(0)
        val allAccuracies = tapResults + List(missedCount) { TapAccuracy.Missed }
        val finalScore = if (allAccuracies.isEmpty()) {
            100
        } else {
            allAccuracies.sumOf { it.scorePercent } / allAccuracies.size
        }
        val bestScore = maxOf(lesson.bestScore, finalScore)

        _uiState.value = _uiState.value.copy(
            phase = LessonPhase.Result,
            glowingPads = emptySet(),
            expectedPadIndices = emptySet(),
            finalScorePercent = finalScore,
            bestScorePercent = bestScore,
            isPass = finalScore >= PASS_THRESHOLD_PERCENT
        )

        saveLessonResultUseCase.execute(
            presetId = preset.id,
            lesson = lesson,
            scorePercent = finalScore,
            passThreshold = PASS_THRESHOLD_PERCENT
        ).collect { result ->
            if (result is Result.Success) {
                _uiState.value = _uiState.value.copy(savedResultPayload = result.data)
            }
        }
    }

    private fun triggerEvent(event: ScheduledEvent) {
        val indices = event.pads.map { it.id }
        indices.forEach { playSoundAtIndex(it) }
        flashGlow(indices.toSet())
    }

    private fun flashGlow(indices: Set<Int>) {
        _uiState.value = _uiState.value.copy(glowingPads = _uiState.value.glowingPads + indices)
        viewModelScope.launch {
            delay(GLOW_FLASH_DURATION_MS)
            _uiState.value = _uiState.value.copy(glowingPads = _uiState.value.glowingPads - indices)
        }
    }

    private fun playSoundAtIndex(index: Int) {
        val files = preset.files ?: return
        val currentFile = files.getOrNull(index) ?: return
        files.forEachIndexed { i, file ->
            if (file.choke == currentFile.choke && file.choke != 0) {
                drumPadPlayer?.stopTrigger(i)
            }
        }
        drumPadPlayer?.trigger(index)
    }

    private fun mapPadColor(color: String?): PadColor {
        return when (color) {
            "red" -> PadColor.Red
            "blue" -> PadColor.Blue
            "green" -> PadColor.Green
            "purple" -> PadColor.Purple
            "yellow" -> PadColor.Orange
            else -> PadColor.None
        }
    }

    private fun terminatePlayer() {
        drumPadPlayer?.apply {
            teardownAudioStream()
            unloadWavAssets()
        }
        drumPadPlayer = null
    }
}
