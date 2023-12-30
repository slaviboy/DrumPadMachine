package com.slaviboy.drumpadmachine.data.room.pad

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.slaviboy.drumpadmachine.data.helpers.DatabaseHelper
import java.util.UUID

@Entity(tableName = "pad")
data class PadEntity(

    @PrimaryKey
    val id: UUID = UUID.randomUUID(),

    val lessonId: UUID = DatabaseHelper.defaultUUID,
    val padId: Int,
    val start: Int,
    val ambient: Boolean,
    val duration: Int
)