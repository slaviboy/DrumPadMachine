package com.slaviboy.drumpadmachine.api.entities

data class SoundLibraries(
    val categories: List<Category>,
    val presets: List<Preset>
)