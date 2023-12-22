package com.slaviboy.drumpadmachine.api.entities

data class PresetApi(
    val id: String,
    val name: String,
    val author: String,
    val price: Int,
    val orderBy: String,
    val timestamp: Int,
    val deleted: Boolean,
    val hasInfo: Boolean,
    val tempo: Int,
    val tags: List<String>,
    val files: LinkedHashMap<String, FileApi>
)