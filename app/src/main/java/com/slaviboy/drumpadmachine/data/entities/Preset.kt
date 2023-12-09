package com.slaviboy.drumpadmachine.data.entities

data class Preset(
    val id: Int,
    val name: String ,
    val author: String?,
    val price: Int?,
    val orderBy: String?,
    val timestamp: Int?,
    val deleted: Boolean?,
    val tags: List<String>?
)