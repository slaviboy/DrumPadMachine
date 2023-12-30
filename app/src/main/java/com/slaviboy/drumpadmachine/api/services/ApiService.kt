package com.slaviboy.drumpadmachine.api.services

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming

interface ApiService {

    @GET("configs/presets/v{version}/config.zip")
    suspend fun getSoundConfigZip(@Path("version") version: Int): Response<ResponseBody>

    @GET("audio/{id}.zip")
    @Streaming
    suspend fun getAudioZipById(@Path("id") id: Long): Response<ResponseBody>
}