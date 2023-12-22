package com.slaviboy.drumpadmachine.api.entities

data class FileApi(
    val looped: Boolean,
    val filename: String,
    val choke: Int,
    val color: String,
    val stopOnRelease: String
)