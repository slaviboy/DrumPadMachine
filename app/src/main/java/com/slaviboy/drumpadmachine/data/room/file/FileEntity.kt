package com.slaviboy.drumpadmachine.data.room.file

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.slaviboy.drumpadmachine.data.helpers.DatabaseHelper
import java.util.UUID

@Entity
data class FileEntity(

    @PrimaryKey
    val id: UUID = UUID.randomUUID(),

    val presetId: UUID = DatabaseHelper.defaultUUID,
    val looped: Boolean?,
    val filename: String,
    val choke: Int?,
    val color: String,
    val stopOnRelease: String?
)