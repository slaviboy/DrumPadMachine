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
import com.slaviboy.drumpadmachine.data.entities.Preset
import com.slaviboy.drumpadmachine.data.room.category.CategoryDao
import com.slaviboy.drumpadmachine.data.room.category.CategoryEntity
import com.slaviboy.drumpadmachine.data.room.config.ConfigDao
import com.slaviboy.drumpadmachine.data.room.config.ConfigEntity
import com.slaviboy.drumpadmachine.data.room.file.FileDao
import com.slaviboy.drumpadmachine.data.room.file.FileEntity
import com.slaviboy.drumpadmachine.data.room.filter.FilterDao
import com.slaviboy.drumpadmachine.data.room.filter.FilterEntity
import com.slaviboy.drumpadmachine.data.room.preset.PresetDao
import com.slaviboy.drumpadmachine.data.room.preset.PresetEntity
import com.slaviboy.drumpadmachine.data.room.relations.CategoryWithRelations
import com.slaviboy.drumpadmachine.data.room.relations.ConfigWithRelations
import com.slaviboy.drumpadmachine.data.room.relations.PresetWithRelations
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

fun ConfigWithRelations.toConfig(): Config {
    return Config(
        categories = this.categories.toCategoryList(),
        presets = this.presets.toPresetList()
    )
}

fun CategoryWithRelations.toCategory(): Category {
    return Category(
        title = this.owner.title,
        filter = this.filter.toFilter()
    )
}

fun List<CategoryWithRelations>.toCategoryList(): List<Category> {
    return this.map { it.toCategory() }
}

fun FilterEntity.toFilter(): Filter {
    return Filter(
        tags = this.tags
    )
}

fun List<PresetWithRelations>.toPresetList(): List<Preset> {
    return this.map { it.toPreset() }
}

fun PresetWithRelations.toPreset(): Preset {
    /*return Preset(
        id = this.presetId,
        name = this.name,
        author = this.author,
        price = this.price,
        orderBy = this.orderBy,
        timestamp = this.timestamp,
        deleted = this.deleted,
        hasInfo = this.hasInfo,
        tempo = this.tempo,
        tags = this.tags,
        files = null,
        lessons = null
    )*/
    return Preset(
        id = 0,
        name = "",
        author = null,
        price = 1,
        orderBy = "",
        timestamp = 1,
        deleted = false,
        hasInfo = false,
        tempo = 1,
        tags = null,
        files = null,
        lessons = null
    )
}

@Singleton
class GetPresetsConfigUseCaseImpl @Inject constructor(
    private val repository: ApiRepository,
    private val configDao: ConfigDao,
    private val categoryDao: CategoryDao,
    private val presetDao: PresetDao,
    private val filterDao: FilterDao,
    private val fileDao: FileDao,
    private val gson: Gson,
    private val context: Context,
    private val dispatchers: Dispatchers
) : GetPresetsConfigUseCase {

    override fun execute(version: Int): Flow<Result<Config>> = flow {
        emit(Result.Loading)

        // emit cached data
        configDao.getConfig()?.let {
            emit(Result.Success(it.toConfig()))
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
                lessons = null //presetApi
            )
        }

        return Config(
            categories = categories,
            presets = presets
        )
    }
}