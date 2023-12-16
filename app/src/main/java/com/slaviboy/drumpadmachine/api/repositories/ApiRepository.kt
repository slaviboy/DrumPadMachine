package com.slaviboy.drumpadmachine.api.repositories

import com.slaviboy.drumpadmachine.api.services.ApiService
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class ApiRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getSoundConfigZip(version: Int): Response<ResponseBody> {
        return apiService.getSoundConfigZip(version)
    }

    suspend fun getAudioZipById(id: Int): Response<ResponseBody> {
        return apiService.getAudioZipById(id)
    }
}