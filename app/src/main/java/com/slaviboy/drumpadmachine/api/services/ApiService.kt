package com.slaviboy.drumpadmachine.api.services

import com.slaviboy.drumpadmachine.api.entities.ConfigApi
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming

interface ApiService {

    @GET("configs/configs.json")
    suspend fun getSoundConfig(): ConfigApi

    @GET("audio/{id}.zip")
    @Streaming
    suspend fun getAudioZipById(@Path("id") id: Int): Response<ResponseBody>

    @GET("covers/{id}")
    suspend fun getCoverById(@Path("id") id: Int): Any

    @GET("cover_icons/{id}")
    suspend fun getCoverIconById(@Path("id") id: Int): Any

    @GET("audio_previews/{id}")
    suspend fun getAudioPreviewById(@Path("id") id: Int): Any
}