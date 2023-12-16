package com.slaviboy.drumpadmachine.data.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class File(
    val looped: Boolean?,
    val filename: String,
    val choke: Int?,
    val color: String,
    val stopOnRelease: String?
) : Parcelable