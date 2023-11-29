package com.slaviboy.drumpadmachine.api.repositories

import com.slaviboy.drumpadmachine.api.entities.SoundLibraries
import com.slaviboy.drumpadmachine.api.services.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.Response
import javax.inject.Inject

class ApiRepository @Inject constructor(
    private val apiService: ApiService
) {
    fun getSoundLibraries(): Flow<SoundLibraries> = flow {
        val soundLibraries = apiService.getSoundLibraries()
        emit(soundLibraries)
    }

    suspend fun getAudioZipById(id: Int): Response {
        return apiService.getAudioZipById(id)
    }

    fun getCoverById(id: Int): Flow<Any> = flow {
        val audioZip = apiService.getAudioZipById(id)
        emit(audioZip)
    }

    fun getCoverIconById(id: Int): Flow<Any> = flow {
        val audioZip = apiService.getAudioZipById(id)
        emit(audioZip)
    }

    fun getAudioPreviewById(id: Int): Flow<Any> = flow {
        val audioZip = apiService.getAudioZipById(id)
        emit(audioZip)
    }
}