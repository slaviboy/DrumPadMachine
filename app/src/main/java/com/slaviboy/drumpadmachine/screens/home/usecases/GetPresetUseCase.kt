package com.slaviboy.drumpadmachine.screens.home.usecases

import com.slaviboy.drumpadmachine.api.results.Result
import com.slaviboy.drumpadmachine.data.entities.File
import com.slaviboy.drumpadmachine.data.entities.Lesson
import com.slaviboy.drumpadmachine.data.entities.Pad
import com.slaviboy.drumpadmachine.data.entities.Preset
import com.slaviboy.drumpadmachine.data.room.preset.PresetDao
import com.slaviboy.drumpadmachine.data.room.relations.PresetWithRelations
import com.slaviboy.drumpadmachine.dispatchers.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

interface GetPresetUseCase {
    fun execute(presetId: Long): Flow<Result<Preset>>
}

@Singleton
class GetPresetUseCaseImpl @Inject constructor(
    private val presetDao: PresetDao,
    private val dispatchers: Dispatchers
) : GetPresetUseCase {

    override fun execute(presetId: Long): Flow<Result<Preset>> = flow {
        emit(Result.Loading)

        presetDao.getPreset(presetId)?.let {
            val preset = mapToPreset(it)
            emit(Result.Success(preset))
            return@flow
        } ?: run {
            emit(Result.Fail("Cannot retrieve preset!"))
        }

    }.flowOn(dispatchers.io)

    private fun mapToPreset(preset: PresetWithRelations): Preset {
        return preset.let {
            Preset(
                id = it.owner.presetId,
                name = it.owner.name,
                author = it.owner.author,
                price = it.owner.price,
                orderBy = it.owner.orderBy,
                timestamp = it.owner.timestamp,
                deleted = it.owner.deleted,
                hasInfo = it.owner.hasInfo,
                tempo = it.owner.tempo,
                tags = it.owner.tags,
                files = it.files.map {
                    File(
                        looped = it.looped,
                        filename = it.filename,
                        choke = it.choke,
                        color = it.color,
                        stopOnRelease = it.stopOnRelease
                    )
                },
                lessons = it.lessons.map {
                    Lesson(
                        id = it.owner.lessonId,
                        side = it.owner.side,
                        version = it.owner.version,
                        name = it.owner.name,
                        orderBy = it.owner.orderBy,
                        sequencerSize = it.owner.sequencerSize,
                        rating = it.owner.rating,
                        lastScore = it.owner.lastScore,
                        bestScore = it.owner.bestScore,
                        lessonState = it.owner.lessonState,
                        pads = it.pads.map {
                            Pad(
                                id = it.padId,
                                start = it.start,
                                ambient = it.ambient,
                                duration = it.duration
                            )
                        }
                    )
                }
            )
        }
    }
}