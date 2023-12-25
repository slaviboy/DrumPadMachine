package com.slaviboy.drumpadmachine.data.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Pad(
    val start: Int,
    val embient: Boolean,
    val duration: Int
) : Parcelable