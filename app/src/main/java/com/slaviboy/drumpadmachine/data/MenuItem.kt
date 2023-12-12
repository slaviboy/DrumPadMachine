package com.slaviboy.drumpadmachine.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class MenuItem(
    val isSelected: Boolean = false,
    @DrawableRes val iconResId: Int,
    @StringRes val titleResId: Int
)