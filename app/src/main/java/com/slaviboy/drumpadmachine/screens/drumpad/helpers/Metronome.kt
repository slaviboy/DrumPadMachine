package com.slaviboy.drumpadmachine.screens.drumpad.helpers

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.SystemClock
import androidx.annotation.RawRes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Ticks a click sample at a given tempo. Uses [SoundPool] (built for short, frequently repeated
 * effects) rather than [android.media.MediaPlayer], and stays decoupled from the pad-sample
 * [com.slaviboy.audio.DrumPadPlayer]/Oboe stream.
 */
class Metronome(
    private val scope: CoroutineScope,
    private val context: Context
) {
    private val soundPool = SoundPool.Builder()
        .setMaxStreams(2)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private var loadedResId: Int? = null
    private var soundId = 0
    private var tickJob: Job? = null

    fun start(bpm: Int, volumePercent: Int, @RawRes soundResId: Int) {
        tickJob?.cancel()
        ensureSoundLoaded(soundResId)
        val volume = volumePercent.coerceIn(0, 100) / 100f
        val intervalMs = 60_000L / bpm.coerceAtLeast(1)
        tickJob = scope.launch {
            var nextTick = SystemClock.elapsedRealtime()
            while (isActive) {
                soundPool.play(soundId, volume, volume, 1, 0, 1f)
                nextTick += intervalMs
                val delayMs = nextTick - SystemClock.elapsedRealtime()
                if (delayMs > 0) delay(delayMs)
            }
        }
    }

    fun stop() {
        tickJob?.cancel()
        tickJob = null
    }

    fun release() {
        stop()
        soundPool.release()
    }

    private fun ensureSoundLoaded(@RawRes soundResId: Int) {
        if (loadedResId == soundResId) return
        soundId = soundPool.load(context, soundResId, 1)
        loadedResId = soundResId
    }
}
