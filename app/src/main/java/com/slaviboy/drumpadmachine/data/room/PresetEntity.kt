package com.slaviboy.drumpadmachine.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PresetEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val name: String,
    val author: String,
    val price: Int,
    val orderBy: String,
    val timestamp: Int,
    val deleted: Boolean,
    val tags: List<String>
)