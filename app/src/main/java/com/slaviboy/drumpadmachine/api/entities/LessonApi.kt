package com.slaviboy.drumpadmachine.api.entities

data class LessonApi(
    val id: Int,
    val version: Int,
    val name: String,
    val orderBy: Int,
    val sequencerSize: Int,
    val rating: Int,
    val pads: LinkedHashMap<String, Array<PadApi>>
)