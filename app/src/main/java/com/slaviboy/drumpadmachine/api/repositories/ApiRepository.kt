package com.slaviboy.drumpadmachine.api.repositories

import com.slaviboy.drumpadmachine.api.entities.SoundLibraries
import com.slaviboy.drumpadmachine.api.services.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class ApiRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getSoundLibraries(): SoundLibraries {
        return apiService.getSoundLibraries()
    }

    suspend fun getAudioZipById(id: Int): Response<ResponseBody> {
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