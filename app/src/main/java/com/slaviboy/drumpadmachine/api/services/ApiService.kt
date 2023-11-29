package com.slaviboy.drumpadmachine.api.services

import com.slaviboy.drumpadmachine.api.entities.SoundLibraries
import okhttp3.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming

interface ApiService {

    @GET("configs/configs.json")
    suspend fun getSoundLibraries(): SoundLibraries

    @GET("configs/audio/{id}")
    @Streaming
    suspend fun getAudioZipById(@Path("id") id: Int): Response

    @GET("configs/covers/{id}")
    suspend fun getCoverById(@Path("id") id: Int): Any

    @GET("configs/cover_icons/{id}")
    suspend fun getCoverIconById(@Path("id") id: Int): Any

    @GET("configs/audio_previews/{id}")
    suspend fun getAudioPreviewById(@Path("id") id: Int): Any
}