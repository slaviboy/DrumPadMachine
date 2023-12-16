package com.slaviboy.drumpadmachine.enums

import androidx.annotation.DrawableRes
import com.slaviboy.drumpadmachine.R

enum class PadColor(@DrawableRes val value: Int) {
    Red(R.drawable.ic_rect_red),
    Green(R.drawable.ic_rect_green),
    Blue(R.drawable.ic_rect_blue),
    Pink(R.drawable.ic_rect_pink),
    Orange(R.drawable.ic_rect_orange),
    Purple(R.drawable.ic_rect_purple),
    Aqua(R.drawable.ic_rect_aqua),
    None(R.drawable.ic_rect_none);
}