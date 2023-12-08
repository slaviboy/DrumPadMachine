package com.slaviboy.drumpadmachine.screens.home.usecases

import com.slaviboy.drumpadmachine.api.repositories.ApiRepository
import com.slaviboy.drumpadmachine.api.results.Result
import com.slaviboy.drumpadmachine.data.entities.Category
import com.slaviboy.drumpadmachine.data.entities.Config
import com.slaviboy.drumpadmachine.data.entities.Filter
import com.slaviboy.drumpadmachine.data.entities.Preset
import com.slaviboy.drumpadmachine.data.room.ConfigDao
import com.slaviboy.drumpadmachine.data.room.ConfigEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetPresetsConfigUseCase @Inject constructor(
    private val repository: ApiRepository,
    private val dao: ConfigDao
) {
    suspend fun execute(): Flow<Result<Config>> = flow {
        emit(Result.Loading)

        // for testing only remove!!!
        delay(4000)

        // emit cached data
        dao.getConfig()?.let {
            val config = Config(it.categories, it.presets)
            emit(Result.Success(config))
        }

        // make API request, and cache locally
        try {
            val configEntity = repository.getSoundConfig().let {
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
                            presetApi.id.toIntOrNull() ?: 0,
                            presetApi.name,
                            presetApi.author,
                            presetApi.price,
                            presetApi.orderBy,
                            presetApi.timestamp,
                            presetApi.deleted,
                            presetApi.tags
                        )
                    }
                )
            }
            dao.upsertConfig(configEntity)

            // emit updated API data
            val config = Config(configEntity.categories, configEntity.presets)
            emit(Result.Success(config))

        } catch (e: Exception) {
            emit(Result.Error("Network error!"))
        }
    }
}