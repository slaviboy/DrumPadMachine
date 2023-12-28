package com.slaviboy.drumpadmachine.screens.home.usecases

import android.content.Context
import com.google.gson.Gson
import com.slaviboy.drumpadmachine.api.entities.ConfigApi
import com.slaviboy.drumpadmachine.api.repositories.ApiRepository
import com.slaviboy.drumpadmachine.api.results.Result
import com.slaviboy.drumpadmachine.data.entities.Category
import com.slaviboy.drumpadmachine.data.entities.Config
import com.slaviboy.drumpadmachine.data.entities.Filter
import com.slaviboy.drumpadmachine.data.entities.LessonState
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

interface GetConfigUseCase {
    fun execute(version: Int): Flow<Result<Config>>
}

@Singleton
class GetConfigUseCaseImpl @Inject constructor(
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
) : GetConfigUseCase {

    override fun execute(version: Int): Flow<Result<Config>> = flow {
        emit(Result.Loading)

        // emit cached data
        configDao.getConfig()?.let {
            val config = getConfig(it)
            emit(Result.Success(config))
            return@flow
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
                id = it.presetId,
                name = it.name,
                author = it.author,
                price = it.price,
                orderBy = it.orderBy,
                timestamp = it.timestamp,
                deleted = it.deleted,
                hasInfo = it.hasInfo,
                tempo = it.tempo,
                tags = it.tags,
                files = null,
                lessons = null
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

        val categories = mutableListOf<Category>()
        val categoryEntityList = mutableListOf<CategoryEntity>()
        val filterEntityList = mutableListOf<FilterEntity>()
        configApi.categoriesApi.forEach { categoryApi ->
            val categoryEntity = CategoryEntity(
                configId = configEntity.id,
                title = categoryApi.title
            )
            val filterEntity = FilterEntity(
                categoryId = categoryEntity.id,
                tags = categoryApi.filterApi.tags
            )
            val category = Category(
                title = categoryApi.title,
                filter = Filter(categoryApi.filterApi.tags)
            )
            categoryEntityList.add(categoryEntity)
            filterEntityList.add(filterEntity)
            categories.add(category)
        }

        val presetEntityList = mutableListOf<PresetEntity>()
        val fileEntityList = mutableListOf<FileEntity>()
        val lessonEntityList = mutableListOf<LessonEntity>()
        val padEntityList = mutableListOf<PadEntity>()
        val presets = mutableListOf<Preset>()

        configApi.presetsApi.forEach {
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
            presetEntityList.add(presetEntity)

            presetApi.files.forEach {
                val (_, fileApi) = it
                val fileEntity = FileEntity(
                    presetId = presetEntity.id,
                    looped = fileApi.looped,
                    filename = fileApi.filename,
                    choke = fileApi.choke,
                    color = fileApi.color,
                    stopOnRelease = fileApi.stopOnRelease
                )
                fileEntityList.add(fileEntity)
            }

            presetApi.beatSchool?.forEach {
                val (side, lessonApiList) = it
                lessonApiList.forEach {
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
                    lessonEntityList.add(lessonEntity)

                    val pads = it.pads.map {
                        val (padId, padApiArray) = it
                        padApiArray.map {
                            PadEntity(
                                lessonId = lessonEntity.id,
                                padId = padId.toIntOrNull() ?: -1,
                                start = it.start,
                                ambient = it.embient,
                                duration = it.duration
                            )
                        }.filter {
                            it.padId != -1
                        }
                    }.flatten()
                    padEntityList.addAll(pads)
                }
            }
            val preset = Preset(
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
                files = null,
                lessons = null
            )
            presets.add(preset)
        }

        categoryDao.upsertCategories(categoryEntityList)
        filterDao.upsertFilters(filterEntityList)
        fileDao.upsertFiles(fileEntityList)
        presetDao.upsertPresets(presetEntityList)
        lessonDao.upsertLessons(lessonEntityList)
        padDao.upsertPads(padEntityList)

        return Config(
            categories = categories,
            presets = presets
        )
    }
}