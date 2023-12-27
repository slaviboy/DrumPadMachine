package com.slaviboy.drumpadmachine.data.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Lesson(
    val id: Int,
    val side: String,
    val version: Int,
    val name: String,
    val orderBy: Int,
    val sequencerSize: Int,
    val rating: Int,
    val lastScore: Int,
    val bestScore: Int,
    val lessonState: LessonState,
    val pads: List<Pad>
) : Parcelable