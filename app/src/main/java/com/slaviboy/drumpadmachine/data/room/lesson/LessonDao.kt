package com.slaviboy.drumpadmachine.data.room.lesson

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Transaction
import androidx.room.Upsert

@Dao
interface LessonDao {

    @Transaction
    @Upsert
    suspend fun upsertLessons(lesson: List<LessonEntity>)

    @Delete
    suspend fun deleteLesson(lesson: LessonEntity)
}