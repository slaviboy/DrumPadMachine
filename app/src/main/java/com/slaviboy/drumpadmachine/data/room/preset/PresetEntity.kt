package com.slaviboy.drumpadmachine.data.room.preset

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PresetEntity(

    @PrimaryKey(autoGenerate = false)
    val id: Long,

    val configId: Long,
    val name: String,
    val author: String?,
    val price: Int?,
    val orderBy: String?,
    val timestamp: Int?,
    val deleted: Boolean?,
    val hasInfo: Boolean,
    val tempo: Int,
    //val tags: List<String>?,
    // val files: List<File>?,
    // val lessons: List<Lesson>?
)