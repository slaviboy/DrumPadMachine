package com.slaviboy.drumpadmachine.data.room.lesson

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import java.util.UUID

@Dao
interface LessonDao {

    @Transaction
    @Upsert
    suspend fun upsertLessons(lesson: List<LessonEntity>)

    @Query("SELECT * FROM lesson WHERE presetId = :presetId AND lessonId = :lessonId AND side = :side LIMIT 1")
    suspend fun getLesson(presetId: UUID, lessonId: Int, side: String): LessonEntity?

    @Query("SELECT * FROM lesson WHERE presetId = :presetId AND side = :side AND orderBy > :orderBy ORDER BY orderBy ASC LIMIT 1")
    suspend fun getNextLesson(presetId: UUID, side: String, orderBy: Int): LessonEntity?

    @Query("DELETE FROM lesson")
    fun deleteAll()
}