package com.slaviboy.drumpadmachine.data.room.preset

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.slaviboy.drumpadmachine.data.helpers.DatabaseHelper
import java.util.UUID

@Entity
data class PresetEntity(

    @PrimaryKey
    val id: UUID = UUID.randomUUID(),

    val configId: UUID = DatabaseHelper.defaultUUID,

    val presetId: Long,
    val name: String,
    val author: String?,
    val price: Int?,
    val orderBy: String?,
    val timestamp: Int?,
    val deleted: Boolean?,
    val hasInfo: Boolean,
    val tempo: Int,
    val tags: List<String>?,
    // val files: List<File>?,
    // val lessons: List<Lesson>?
)