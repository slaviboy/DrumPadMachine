package com.slaviboy.drumpadmachine.screens.lessonplayer.usecases

import com.slaviboy.drumpadmachine.api.results.Result
import com.slaviboy.drumpadmachine.data.entities.Lesson
import com.slaviboy.drumpadmachine.data.entities.LessonState
import com.slaviboy.drumpadmachine.data.room.lesson.LessonDao
import com.slaviboy.drumpadmachine.data.room.preset.PresetDao
import com.slaviboy.drumpadmachine.dispatchers.Dispatchers
import com.slaviboy.drumpadmachine.screens.lessonplayer.models.LessonResultPayload
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

interface SaveLessonResultUseCase {
    fun execute(presetId: Long, lesson: Lesson, scorePercent: Int, passThreshold: Int): Flow<Result<LessonResultPayload>>
}

@Singleton
class SaveLessonResultUseCaseImpl @Inject constructor(
    private val presetDao: PresetDao,
    private val lessonDao: LessonDao,
    private val dispatchers: Dispatchers
) : SaveLessonResultUseCase {

    override fun execute(
        presetId: Long,
        lesson: Lesson,
        scorePercent: Int,
        passThreshold: Int
    ): Flow<Result<LessonResultPayload>> = flow {
        emit(Result.Loading)
        try {
            val presetEntity = presetDao.getPreset(presetId)?.owner
            if (presetEntity == null) {
                emit(Result.Fail("Cannot resolve preset!"))
                return@flow
            }

            val current = lessonDao.getLesson(presetEntity.id, lesson.id, lesson.side)
            if (current == null) {
                emit(Result.Fail("Cannot resolve lesson!"))
                return@flow
            }

            val updatedCurrent = current.copy(
                lastScore = scorePercent,
                bestScore = maxOf(current.bestScore, scorePercent),
                lessonState = LessonState.Replay
            )

            var unlockedNextLessonId: Int? = null
            val toUpsert = mutableListOf(updatedCurrent)
            if (scorePercent >= passThreshold) {
                val next = lessonDao.getNextLesson(presetEntity.id, lesson.side, lesson.orderBy)
                if (next != null && next.lessonState == LessonState.Unlock) {
                    toUpsert.add(next.copy(lessonState = LessonState.Play))
                    unlockedNextLessonId = next.lessonId
                }
            }

            lessonDao.upsertLessons(toUpsert)

            emit(
                Result.Success(
                    LessonResultPayload(
                        lessonId = lesson.id,
                        side = lesson.side,
                        lastScore = updatedCurrent.lastScore,
                        bestScore = updatedCurrent.bestScore,
                        newState = updatedCurrent.lessonState,
                        unlockedNextLessonId = unlockedNextLessonId
                    )
                )
            )
        } catch (e: Exception) {
            emit(Result.Fail("Failed to save lesson result!"))
        }
    }.flowOn(dispatchers.io)
}
