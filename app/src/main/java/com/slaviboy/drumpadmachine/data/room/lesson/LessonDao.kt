package com.slaviboy.drumpadmachine.data.room.lesson

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Upsert

@Dao
interface LessonDao {

    @Upsert
    suspend fun upsertLessons(lesson: List<LessonEntity>)

    @Delete
    suspend fun deleteLesson(lesson: LessonEntity)
}