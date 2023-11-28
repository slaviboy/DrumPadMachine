package com.slaviboy.drumpadmachine.api.services

import com.slaviboy.drumpadmachine.api.entities.SoundLibraries
import retrofit2.http.GET

interface ApiService {

    @GET("configs/configs.json")
    suspend fun getConfigs(): SoundLibraries
}