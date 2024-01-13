package com.slaviboy.drumpadmachine.data.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Pad(
    val id: Int,
    val start: Int,
    val ambient: Boolean,
    val duration: Int
) : Parcelable