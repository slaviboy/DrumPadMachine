package com.slaviboy.drumpadmachine.screens.drumpad.helpers

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.SystemClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Ticks a short click tone at a given tempo, using [ToneGenerator] rather than a bundled
 * sample so the feature needs no audio asset and stays decoupled from the pad-sample
 * [com.slaviboy.audio.DrumPadPlayer]/Oboe stream.
 */
class Metronome(private val scope: CoroutineScope) {

    private var toneGenerator: ToneGenerator? = null
    private var tickJob: Job? = null

    fun start(bpm: Int, volumePercent: Int) {
        stop()
        toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, volumePercent.coerceIn(0, 100))
        val intervalMs = 60_000L / bpm.coerceAtLeast(1)
        tickJob = scope.launch {
            var nextTick = SystemClock.elapsedRealtime()
            while (isActive) {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 50)
                nextTick += intervalMs
                val delayMs = nextTick - SystemClock.elapsedRealtime()
                if (delayMs > 0) delay(delayMs)
            }
        }
    }

    fun stop() {
        tickJob?.cancel()
        tickJob = null
        toneGenerator?.release()
        toneGenerator = null
    }
}
