package com.slaviboy.drumpadmachine.screens.home.usecases

import android.content.Context
import com.google.gson.Gson
import com.slaviboy.drumpadmachine.api.entities.ConfigApi
import com.slaviboy.drumpadmachine.api.repositories.ApiRepository
import com.slaviboy.drumpadmachine.api.results.Result
import com.slaviboy.drumpadmachine.data.entities.Category
import com.slaviboy.drumpadmachine.data.entities.Config
import com.slaviboy.drumpadmachine.data.entities.File
import com.slaviboy.drumpadmachine.data.entities.Filter
import com.slaviboy.drumpadmachine.data.entities.Lesson
import com.slaviboy.drumpadmachine.data.entities.LessonState
import com.slaviboy.drumpadmachine.data.entities.Pad
import com.slaviboy.drumpadmachine.data.entities.Preset
import com.slaviboy.drumpadmachine.data.room.category.CategoryDao
import com.slaviboy.drumpadmachine.data.room.category.CategoryEntity
import com.slaviboy.drumpadmachine.data.room.config.ConfigDao
import com.slaviboy.drumpadmachine.data.room.config.ConfigEntity
import com.slaviboy.drumpadmachine.data.room.file.FileDao
import com.slaviboy.drumpadmachine.data.room.file.FileEntity
import com.slaviboy.drumpadmachine.data.room.filter.FilterDao
import com.slaviboy.drumpadmachine.data.room.filter.FilterEntity
import com.slaviboy.drumpadmachine.data.room.lesson.LessonDao
import com.slaviboy.drumpadmachine.data.room.lesson.LessonEntity
import com.slaviboy.drumpadmachine.data.room.pad.PadDao
import com.slaviboy.drumpadmachine.data.room.pad.PadEntity
import com.slaviboy.drumpadmachine.data.room.preset.PresetDao
import com.slaviboy.drumpadmachine.data.room.preset.PresetEntity
import com.slaviboy.drumpadmachine.data.room.relations.ConfigWithRelations
import com.slaviboy.drumpadmachine.dispatchers.Dispatchers
import com.slaviboy.drumpadmachine.screens.home.helpers.ZipHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.internal.toLongOrDefault
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton
import java.io.File as IOFile

interface GetPresetsConfigUseCase {
    fun execute(version: Int): Flow<Result<Config>>
}

@Singleton
class GetPresetsConfigUseCaseImpl @Inject constructor(
    private val repository: ApiRepository,
    private val configDao: ConfigDao,
    private val categoryDao: CategoryDao,
    private val presetDao: PresetDao,
    private val filterDao: FilterDao,
    private val fileDao: FileDao,
    private val lessonDao: LessonDao,
    private val padDao: PadDao,
    private val gson: Gson,
    private val context: Context,
    private val dispatchers: Dispatchers
) : GetPresetsConfigUseCase {

    override fun execute(version: Int): Flow<Result<Config>> = flow {
        emit(Result.Loading)

        // emit cached data
        configDao.getConfig()?.let {
            val config = getConfig(it)
            emit(Result.Success(config))
        }

        // make API request, and cache locally
        try {
            val response = repository.getSoundConfigZip(version)
            if (!response.isSuccessful) {
                emit(Result.Fail("Failed to download ZIP file"))
                return@flow
            }
            val path = IOFile(context.cacheDir, "config/presets/v$version/")
            val tempFile = IOFile(path, "temp_config.zip")
            tempFile.getParentFile()?.mkdirs()
            tempFile.createNewFile()
            response.body()?.byteStream()?.use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->

                    // extract Zip and save json file locally
                    inputStream.copyTo(outputStream)
                    ZipHelper.extractZip(tempFile, path)
                    tempFile.delete()

                    // extract ConfigApi from json file
                    val bufferedReader = IOFile(path, "config.json").bufferedReader()
                    val configText = bufferedReader.use { it.readText() }
                    val configApi = gson.fromJson(configText, ConfigApi::class.java)

                    // emit updated API data
                    val config = toCategoryEntity(configApi)
                    emit(Result.Success(config))
                }
            } ?: run {
                emit(Result.Fail("Empty response body"))
            }

        } catch (e: Exception) {
            emit(Result.Fail("Network error!"))
        }
    }.flowOn(dispatchers.io)

    private fun getConfig(configWithRelations: ConfigWithRelations): Config {
        val presets = configWithRelations.presets.map {
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
        val categories = configWithRelations.categories.map {
            Category(
                title = it.owner.title,
                filter = Filter(tags = it.filter.tags)
            )
        }
        return Config(
            categories = categories,
            presets = presets
        )
    }

    private suspend fun toCategoryEntity(configApi: ConfigApi): Config {
        val configEntity = ConfigEntity()
        configDao.upsertConfig(configEntity)

        val categories = configApi.categoriesApi.map { categoryApi ->
            val categoryEntity = CategoryEntity(
                configId = configEntity.id,
                title = categoryApi.title
            )
            categoryDao.upsertCategory(categoryEntity)

            val filterEntity = FilterEntity(
                categoryId = categoryEntity.id,
                tags = categoryApi.filterApi.tags
            )
            filterDao.upsertFilter(filterEntity)

            Category(
                title = categoryApi.title,
                filter = Filter(categoryApi.filterApi.tags)
            )
        }

        val presets = configApi.presetsApi.map {
            val (_, presetApi) = it

            val presetEntity = PresetEntity(
                configId = configEntity.id,
                presetId = presetApi.id.toLongOrDefault(0L),
                name = presetApi.name,
                author = presetApi.author,
                price = presetApi.price,
                orderBy = presetApi.orderBy,
                timestamp = presetApi.timestamp,
                deleted = presetApi.deleted,
                hasInfo = presetApi.hasInfo,
                tempo = presetApi.tempo,
                tags = presetApi.tags
            )
            presetDao.upsertPreset(presetEntity)

            val files = presetApi.files.map {
                val (_, fileApi) = it
                val fileEntity = FileEntity(
                    presetId = presetEntity.id,
                    looped = fileApi.looped,
                    filename = fileApi.filename,
                    choke = fileApi.choke,
                    color = fileApi.color,
                    stopOnRelease = fileApi.stopOnRelease
                )
                fileDao.upsertFile(fileEntity)

                File(
                    looped = fileApi.looped,
                    filename = fileApi.filename,
                    choke = fileApi.choke,
                    color = fileApi.color,
                    stopOnRelease = fileApi.stopOnRelease
                )
            }

            val lessons = presetApi.beatSchool?.map {
                val (side, lessonApiList) = it

                lessonApiList.map {
                    val pads = it.pads.map {
                        val (padId, padApiArray) = it
                        padApiArray.map {
                            Pad(
                                id = padId.toIntOrNull() ?: -1,
                                start = it.start,
                                ambient = it.embient,
                                duration = it.duration
                            )
                        }.filter {
                            it.id != -1
                        }
                    }.flatten()


                    val lessonEntity = LessonEntity(
                        presetId = presetEntity.id,
                        lessonId = it.id,
                        side = side,
                        version = it.version,
                        name = it.name,
                        orderBy = it.orderBy,
                        sequencerSize = it.sequencerSize,
                        rating = it.rating,
                        lastScore = 0,
                        bestScore = 0,
                        lessonState = LessonState.Unlock
                    )
                    lessonDao.upsertLesson(lessonEntity)

                    val padEntityList = pads.map {
                        PadEntity(
                            lessonId = lessonEntity.id,
                            padId = it.id,
                            start = it.start,
                            ambient = it.ambient,
                            duration = it.duration
                        )
                    }
                    padDao.upsertPads(padEntityList)

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
                        pads = pads
                    )
                }
            }?.flatten()

            Preset(
                id = presetApi.id.toLongOrDefault(0L),
                name = presetApi.name,
                author = presetApi.author,
                price = presetApi.price,
                orderBy = presetApi.orderBy,
                timestamp = presetApi.timestamp,
                deleted = presetApi.deleted,
                hasInfo = presetApi.hasInfo,
                tempo = presetApi.tempo,
                tags = presetApi.tags,
                files = files,
                lessons = lessons
            )
        }

        return Config(
            categories = categories,
            presets = presets
        )
    }
}