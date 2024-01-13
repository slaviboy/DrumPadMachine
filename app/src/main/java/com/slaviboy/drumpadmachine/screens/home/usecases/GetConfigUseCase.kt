package com.slaviboy.drumpadmachine.screens.home.usecases

import android.content.Context
import com.google.gson.Gson
import com.slaviboy.drumpadmachine.api.entities.ConfigApi
import com.slaviboy.drumpadmachine.api.repositories.ApiRepository
import com.slaviboy.drumpadmachine.api.results.Result
import com.slaviboy.drumpadmachine.data.entities.Category
import com.slaviboy.drumpadmachine.data.entities.Config
import com.slaviboy.drumpadmachine.data.entities.Filter
import com.slaviboy.drumpadmachine.data.entities.Lesson
import com.slaviboy.drumpadmachine.data.entities.LessonState
import com.slaviboy.drumpadmachine.data.entities.Pad
import com.slaviboy.drumpadmachine.data.entities.Preset
import com.slaviboy.drumpadmachine.data.room.config.ConfigDao
import com.slaviboy.drumpadmachine.data.room.relations.ConfigWithRelations
import com.slaviboy.drumpadmachine.dispatchers.Dispatchers
import com.slaviboy.drumpadmachine.screens.home.helpers.ZipHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.internal.toLongOrDefault
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

interface GetConfigUseCase {
    fun execute(configVersion: Int): Flow<Result<Config>>
}

@Singleton
class GetConfigUseCaseImpl @Inject constructor(
    private val repository: ApiRepository,
    private val configDao: ConfigDao,
    private val gson: Gson,
    private val context: Context,
    private val dispatchers: Dispatchers
) : GetConfigUseCase {

    override fun execute(configVersion: Int): Flow<Result<Config>> = flow {
        emit(Result.Loading)

        // emit cached data
        configDao.getConfig()?.let {
            val config = getConfig(it)
            if (!config.categories.isNullOrEmpty() && !config.presets.isNullOrEmpty()) {
                emit(Result.Success(config))
                return@flow
            }
        }

        // make API request, and cache locally
        try {

            val path = File(context.cacheDir, "config/presets/v$configVersion/")
            if (!path.exists()) {
                path.mkdirs()
            }

            // try with cached config.json file
            readConfigFile(path)?.let {
                emit(Result.Success(it))
                return@flow
            }

            // make API call
            val response = repository.getSoundConfigZip(configVersion)
            if (!response.isSuccessful) {
                emit(Result.Fail("Failed to download ZIP file"))
                return@flow
            }
            val tempFile = File(path, "temp_config.zip")
            tempFile.createNewFile()
            response.body()?.byteStream()?.use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->

                    // extract Zip and save json file locally
                    inputStream.copyTo(outputStream)
                    ZipHelper.extractZip(tempFile, path)
                    tempFile.delete()

                    // extract ConfigApi from json file
                    readConfigFile(path)?.let {
                        emit(Result.Success(it))
                    }
                }
            } ?: run {
                emit(Result.Fail("Empty response body"))
            }

        } catch (e: Exception) {
            emit(Result.Fail("Network error!"))
        }

    }.flowOn(dispatchers.io)

    private suspend fun readConfigFile(path: File): Config? {
        val configFile = File(path, "config.json")
        if (!configFile.exists()) {
            return null
        }
        val bufferedReader = configFile.bufferedReader()
        val configText = bufferedReader.use { it.readText() }
        val configApi = gson.fromJson(configText, ConfigApi::class.java)
        return toCategoryEntity(configApi)
    }

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

    private fun toCategoryEntity(configApi: ConfigApi): Config {

        val categories = configApi.categoriesApi.map { categoryApi ->
            Category(
                title = categoryApi.title,
                filter = Filter(categoryApi.filterApi.tags)
            )
        }

        val presets = configApi.presetsApi.map {
            val (_, presetApi) = it

            val files = presetApi.files.map {
                val (_, fileApi) = it
                com.slaviboy.drumpadmachine.data.entities.File(
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