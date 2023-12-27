package com.slaviboy.drumpadmachine.data.room.lesson

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.slaviboy.drumpadmachine.data.entities.LessonState
import com.slaviboy.drumpadmachine.data.entities.Pad
import com.slaviboy.drumpadmachine.data.helpers.DatabaseHelper
import java.util.UUID

@Entity(tableName = "lesson")
data class LessonEntity(

    @PrimaryKey
    val id: UUID = UUID.randomUUID(),

    val presetId: UUID = DatabaseHelper.defaultUUID,
    val lessonId: Int,
    val side: String,
    val version: Int,
    val name: String,
    val orderBy: Int,
    val sequencerSize: Int,
    val rating: Int,
    val lastScore: Int,
    val bestScore: Int,
    val lessonState: LessonState
)