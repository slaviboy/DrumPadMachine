package com.slaviboy.drumpadmachine.api.entities

data class Preset(
    val deleted: Boolean,
    val audioPreview1URL: String,
    val author: String,
    val icon: String,
    val id: String,
    val imagePreview1: String,
    val name: String,
    val orderBy: String,
    val price: Int,
    val tags: List<String>,
    val timestamp: Int
)