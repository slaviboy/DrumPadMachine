package com.slaviboy.drumpadmachine.data.room.managers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.slaviboy.drumpadmachine.api.entities.ConfigApi
import com.slaviboy.drumpadmachine.data.entities.LessonState
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
import com.slaviboy.drumpadmachine.dispatchers.Dispatchers
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.withContext
import okhttp3.internal.toLongOrDefault
import java.io.File

@HiltWorker
class StoreDatabaseWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val configDao: ConfigDao,
    private val categoryDao: CategoryDao,
    private val presetDao: PresetDao,
    private val filterDao: FilterDao,
    private val fileDao: FileDao,
    private val lessonDao: LessonDao,
    private val padDao: PadDao,
    private val dispatchers: Dispatchers,
    private val gson: Gson
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result = withContext(dispatchers.io) {
        try {
            val version = 12
            val path = File(applicationContext.cacheDir, "config/presets/v$version/")
            if (path.exists()) {

                // try with cached config.json file
                readConfigFile(path)?.let {
                    saveRoomDatabaseEntities(it)
                }
            }

        } catch (e: Exception) {
            print(e)
        }
        return@withContext Result.success()
    }

    private fun readConfigFile(path: File): ConfigApi? {
        val configFile = File(path, "config.json")
        if (!configFile.exists()) {
            return null
        }
        val bufferedReader = configFile.bufferedReader()
        val configText = bufferedReader.use { it.readText() }
        return gson.fromJson(configText, ConfigApi::class.java)
    }

    private suspend fun saveRoomDatabaseEntities(configApi: ConfigApi) {
        val configEntity = ConfigEntity()
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
            categoryEntityList.add(categoryEntity)
            filterEntityList.add(filterEntity)
        }

        val presetEntityList = mutableListOf<PresetEntity>()
        val fileEntityList = mutableListOf<FileEntity>()
        val lessonEntityList = mutableListOf<LessonEntity>()
        val padEntityList = mutableListOf<PadEntity>()

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
        }

        configDao.upsertConfig(configEntity)
        categoryDao.upsertCategories(categoryEntityList)
        filterDao.upsertFilters(filterEntityList)
        fileDao.upsertFiles(fileEntityList)
        presetDao.upsertPresets(presetEntityList)
        lessonDao.upsertLessons(lessonEntityList)
        padDao.upsertPads(padEntityList)
    }
}