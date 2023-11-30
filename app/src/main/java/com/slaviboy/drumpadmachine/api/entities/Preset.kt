package com.slaviboy.drumpadmachine.api.entities

data class Preset(
    val deleted: Boolean,
    val author: String,
    val id: String,
    val name: String,
    val orderBy: String,
    val price: Int,
    val tags: List<String>,
    val timestamp: Int
)