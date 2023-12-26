package com.slaviboy.drumpadmachine.data.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Preset(
    val id: Long,
    val name: String,
    val author: String?,
    val price: Int?,
    val orderBy: String?,
    val timestamp: Int?,
    val deleted: Boolean?,
    val hasInfo: Boolean,
    val tempo: Int,
    val tags: List<String>?,
    val files: List<File>?,
    val lessons: List<Lesson>?
) : Parcelable