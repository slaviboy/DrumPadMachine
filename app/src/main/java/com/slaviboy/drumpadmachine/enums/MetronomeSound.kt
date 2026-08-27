package com.slaviboy.drumpadmachine.enums

import androidx.annotation.RawRes
import androidx.annotation.StringRes
import com.slaviboy.drumpadmachine.R

enum class MetronomeSound(
    val key: String,
    @RawRes val rawResId: Int,
    @StringRes val labelResId: Int
) {
    Metronome1("metronome_1", R.raw.metronome_1, R.string.metronome_sound_1),
    Metronome2("metronome_2", R.raw.metronome_2, R.string.metronome_sound_2);

    companion object {
        val Default = Metronome1

        fun fromKey(key: String?): MetronomeSound = values().find { it.key == key } ?: Default
    }
}
