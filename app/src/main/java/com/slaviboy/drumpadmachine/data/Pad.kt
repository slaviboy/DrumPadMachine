package com.slaviboy.drumpadmachine.data

import com.slaviboy.drumpadmachine.enums.PadColor

data class Pad(
    val color: PadColor,
    val isActive: Boolean
)