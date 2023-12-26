package com.slaviboy.drumpadmachine.screens.home.usecases

import android.content.Context
import com.google.gson.Gson
import com.slaviboy.drumpadmachine.api.entities.CategoryApi
import com.slaviboy.drumpadmachine.api.entities.ConfigApi
import com.slaviboy.drumpadmachine.api.entities.FilterApi
import com.slaviboy.drumpadmachine.api.entities.PresetApi
import com.slaviboy.drumpadmachine.api.repositories.ApiRepository
import com.slaviboy.drumpadmachine.api.results.Result
import com.slaviboy.drumpadmachine.data.entities.Category
import com.slaviboy.drumpadmachine.data.entities.Config
import com.slaviboy.drumpadmachine.data.entities.Filter
import com.slaviboy.drumpadmachine.data.entities.Preset
import com.slaviboy.drumpadmachine.data.room.category.CategoryDao
import com.slaviboy.drumpadmachine.data.room.category.CategoryEntity
import com.slaviboy.drumpadmachine.data.room.config.ConfigDao
import com.slaviboy.drumpadmachine.data.room.config.ConfigEntity
import com.slaviboy.drumpadmachine.data.room.filter.FilterDao
import com.slaviboy.drumpadmachine.data.room.filter.FilterEntity
import com.slaviboy.drumpadmachine.data.room.preset.PresetDao
import com.slaviboy.drumpadmachine.data.room.preset.PresetEntity
import com.slaviboy.drumpadmachine.data.room.relations.CategoryWithRelations
import com.slaviboy.drumpadmachine.data.room.relations.ConfigWithRelation
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

interface GetPresetsConfigUseCase {
    fun execute(version: Int): Flow<Result<Config>>
}


fun ConfigWithRelation.toConfig(): Config {
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

/*fun List<CategoryEntity>.toCategoryList(): List<Category> {
    return this.map { it.toCategory() }
}

fun CategoryEntity.toCategory(): Category {
    return Category(
        title = this.title,
        filter = this.f
    )
}*/

fun List<PresetEntity>.toPresetList(): List<Preset> {
    return this.map { it.toPreset() }
}

fun PresetEntity.toPreset(): Preset {
    return Preset(
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
    )
}

fun LinkedHashMap<String, PresetApi>.toPresetEntityList(config: ConfigEntity): List<PresetEntity> {
    return this.map {
        val (_, presetApi) = it
        PresetEntity(
            configId = config.id,
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
    }
}

fun List<CategoryApi>.toCategoryEntityList(config: ConfigEntity): List<CategoryEntity> {
    return this.map {
        CategoryEntity(
            title = it.title,
            configId = config.id
        )
    }
}

fun FilterApi.toFilter(): Filter {
    return Filter(
        tags = this.tags
    )
}

@Singleton
class GetPresetsConfigUseCaseImpl @Inject constructor(
    private val repository: ApiRepository,
    private val configDao: ConfigDao,
    private val categoryDao: CategoryDao,
    private val presetDao: PresetDao,
    private val filterDao: FilterDao,
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

                    // update/insert config
                    val configId = 0L
                    val configEntity = ConfigEntity()
                    configDao.upsertConfig(configEntity)

                    // update/insert config -> categories
                    /*val categoryEntityList = configApi.categoriesApi.toCategoryEntityList(configId)
                    categoryDao.upsertCategories(categoryEntityList)*/

                    // update/insert config -> categories -> filter
                    /* val filterEntity = configApi.categoriesApi.toFilterEntity(configId)
                     filterDao.upsertFilter(filterEntity)*/

                    // update/insert config -> categories
                    /*val presetEntityList = configApi.presetsApi.toPresetEntityList(configId)
                    presetDao.upsertPresets(presetEntityList)*/

                    // emit updated API data
                    /*val config = Config(
                        categories = categoryEntityList.toCategoryList(),
                        presets = presetEntityList.toPresetList()
                    )
                    emit(Result.Success(config))*/
                }
            } ?: run {
                emit(Result.Fail("Empty response body"))
            }

        } catch (e: Exception) {
            emit(Result.Fail("Network error!"))
        }
    }.flowOn(dispatchers.io)
}