package com.slaviboy.drumpadmachine.screens.home.usecases

import android.content.Context
import com.google.gson.Gson
import com.slaviboy.drumpadmachine.api.entities.ConfigApi
import com.slaviboy.drumpadmachine.api.entities.LessonApi
import com.slaviboy.drumpadmachine.api.repositories.ApiRepository
import com.slaviboy.drumpadmachine.api.results.Result
import com.slaviboy.drumpadmachine.data.entities.Category
import com.slaviboy.drumpadmachine.data.entities.Config
import com.slaviboy.drumpadmachine.data.entities.Filter
import com.slaviboy.drumpadmachine.data.entities.Lesson
import com.slaviboy.drumpadmachine.data.entities.LessonState
import com.slaviboy.drumpadmachine.data.entities.Pad
import com.slaviboy.drumpadmachine.data.entities.Preset
import com.slaviboy.drumpadmachine.data.room.ConfigDao
import com.slaviboy.drumpadmachine.data.room.ConfigEntity
import com.slaviboy.drumpadmachine.dispatchers.Dispatchers
import com.slaviboy.drumpadmachine.screens.home.helpers.ZipHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton
import com.slaviboy.drumpadmachine.data.entities.File as FileDb

interface GetPresetsConfigUseCase {
    fun execute(version: Int): Flow<Result<Config>>
}

@Singleton
class GetPresetsConfigUseCaseImpl @Inject constructor(
    private val repository: ApiRepository,
    private val dao: ConfigDao,
    private val gson: Gson,
    private val context: Context,
    private val dispatchers: Dispatchers
) : GetPresetsConfigUseCase {

    override fun execute(version: Int): Flow<Result<Config>> = flow {
        emit(Result.Loading)

        // emit cached data
        dao.getConfig()?.let {
            val config = Config(it.categories, it.presets)
            emit(Result.Success(config))
        }

        // make API request, and cache locally
        try {
            val response = repository.getSoundConfigZip(version)
            if (!response.isSuccessful) {
                emit(Result.Fail("Failed to download ZIP file"))
                return@flow
            }
            val path = File(context.cacheDir, "config/presets/v$version/")
            val tempFile = File(path, "temp_config.zip")
            tempFile.getParentFile()?.mkdirs()
            tempFile.createNewFile()
            response.body()?.byteStream()?.use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->

                    // extract Zip and save json file locally
                    inputStream.copyTo(outputStream)
                    ZipHelper.extractZip(tempFile, path)
                    tempFile.delete()

                    // extract ConfigApi from json file
                    val bufferedReader = File(path, "config.json").bufferedReader()
                    val configText = bufferedReader.use { it.readText() }
                    val configApi = gson.fromJson(configText, ConfigApi::class.java)
                    val configEntity = configApi.let {
                        ConfigEntity(
                            id = 0,
                            categories = it.categoriesApi.map {
                                Category(
                                    title = it.title,
                                    filter = Filter(it.filterApi.tags)
                                )
                            },
                            presets = it.presetsApi.map {
                                val (_, presetApi) = it
                                Preset(
                                    id = presetApi.id.toIntOrNull() ?: 0,
                                    name = presetApi.name,
                                    author = presetApi.author,
                                    price = presetApi.price,
                                    orderBy = presetApi.orderBy,
                                    timestamp = presetApi.timestamp,
                                    deleted = presetApi.deleted,
                                    hasInfo = presetApi.hasInfo,
                                    tempo = presetApi.tempo,
                                    tags = presetApi.tags,
                                    files = presetApi.files.map {
                                        val (_, fileApi) = it
                                        FileDb(
                                            looped = fileApi.looped,
                                            filename = fileApi.filename,
                                            choke = fileApi.choke,
                                            color = fileApi.color,
                                            stopOnRelease = fileApi.stopOnRelease
                                        )
                                    },
                                    lessons = presetApi.beatSchool?.let {
                                        getLessons(it, "v0", "a", "b")
                                    }
                                )
                            }
                        )
                    }
                    dao.upsertConfig(configEntity)

                    // emit updated API data
                    val config = Config(configEntity.categories, configEntity.presets)
                    emit(Result.Success(config))
                }
            } ?: run {
                emit(Result.Fail("Empty response body"))
            }

        } catch (e: Exception) {
            emit(Result.Fail("Network error!"))
        }
    }.flowOn(dispatchers.io)

    private fun getLessons(lessonsMap: LinkedHashMap<String, List<LessonApi>>, vararg sides: String): List<Lesson> {
        return sides.map { side ->
            lessonsMap[side]?.map {
                Lesson(
                    id = it.id,
                    side = side,
                    version = it.version,
                    name = it.name,
                    orderBy = it.orderBy,
                    sequencerSize = it.sequencerSize,
                    rating = it.rating,
                    lastScore = 0,
                    bestScore = 0,
                    lessonState = LessonState.Unlock,
                    pads = it.pads.mapValues {
                        val (_, padApiArray) = it
                        padApiArray.map {
                            Pad(
                                start = it.start,
                                embient = it.embient,
                                duration = it.duration
                            )
                        }.toTypedArray()
                    } as HashMap
                )
            } ?: listOf()
        }.flatten()
    }
}