package com.slaviboy.drumpadmachine.screens.home.usecases

import com.slaviboy.drumpadmachine.api.entities.SoundLibraries
import com.slaviboy.drumpadmachine.api.repositories.ApiRepository
import com.slaviboy.drumpadmachine.api.results.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadAudioConfigUseCase @Inject constructor(
    private val repository: ApiRepository
) {
    suspend fun execute(): Flow<Result<SoundLibraries>> = flow {
        val soundLibraries = repository.getSoundLibraries()
        emit(Result.Success(soundLibraries))
    }
}