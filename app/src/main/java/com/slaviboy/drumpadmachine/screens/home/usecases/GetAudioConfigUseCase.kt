package com.slaviboy.drumpadmachine.screens.home.usecases

import com.slaviboy.drumpadmachine.api.repositories.ApiRepository
import com.slaviboy.drumpadmachine.api.results.Result
import com.slaviboy.drumpadmachine.data.room.ConfigDao
import com.slaviboy.drumpadmachine.data.room.ConfigEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAudioConfigUseCase @Inject constructor(
    private val repository: ApiRepository,
    private val dao: ConfigDao
) {
    suspend fun execute(): Flow<Result<ConfigEntity>> = flow {
        emit(Result.Loading)

        dao.getConfigById().collect {
            emit(Result.Success(it))
        }
        val soundLibraries = repository.getSoundConfig().let {
            ConfigEntity(
                id = 0,
                categoriesApi = it.categoriesApi,
                presetsApi = it.presetsApi
            )
        }
        dao.upsertConfig(soundLibraries)
        emit(Result.Success(soundLibraries))
    }
}