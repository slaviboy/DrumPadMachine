package com.slaviboy.drumpadmachine.core.entities

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class BaseItem(
    @DrawableRes val iconResId: Int,
    @StringRes val titleResId: Int,
    @StringRes val subtitleResId: Int
)