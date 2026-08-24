package com.slaviboy.drumpadmachine.screens.lessonplayer.helpers

import com.slaviboy.drumpadmachine.data.entities.Lesson
import com.slaviboy.drumpadmachine.data.entities.Pad
import com.slaviboy.drumpadmachine.screens.lessonplayer.models.TapAccuracy
import kotlin.math.abs

/** All pads that share the same scheduled step (chords / layered hits). */
data class ScheduledEvent(
    val timeMs: Long,
    val pads: List<Pad>
) {
    val nonAmbientPadIds: Set<Int> = pads.filter { !it.ambient }.map { it.id }.toSet()
}

data class LessonSchedule(
    val stepDurationMs: Float,
    val totalDurationMs: Long,
    val events: List<ScheduledEvent>,
    val tapEvents: List<ScheduledEvent>
)

/**
 * Builds a time-ordered playback schedule from a lesson's pads.
 *
 * `Pad.start` is a step index on a 16th-note grid (sequencerSize values of 16n+1 across real
 * lesson data support this); tempo (BPM) converts steps to real time.
 */
object LessonScheduler {

    fun build(lesson: Lesson, tempo: Int): LessonSchedule {
        val stepDurationMs = 60_000f / tempo.coerceAtLeast(1) / 4f
        val totalDurationMs = ((lesson.sequencerSize - 1).coerceAtLeast(1) * stepDurationMs).toLong()
        val events = lesson.pads
            .groupBy { it.start }
            .toSortedMap()
            .map { (start, pads) -> ScheduledEvent(timeMs = (start * stepDurationMs).toLong(), pads = pads) }
        val tapEvents = events.filter { it.nonAmbientPadIds.isNotEmpty() }
        return LessonSchedule(stepDurationMs, totalDurationMs, events, tapEvents)
    }

    fun accuracyFor(deltaMs: Long, stepDurationMs: Float): TapAccuracy {
        val delta = abs(deltaMs)
        return when {
            delta <= stepDurationMs / 2 -> TapAccuracy.Perfect
            delta <= stepDurationMs -> TapAccuracy.Good
            delta <= stepDurationMs * 2 -> TapAccuracy.Late
            else -> TapAccuracy.Missed
        }
    }
}
