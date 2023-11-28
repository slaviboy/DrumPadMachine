package com.slaviboy.drumpadmachine.api.repositories

import com.slaviboy.drumpadmachine.api.entities.SoundLibraries
import com.slaviboy.drumpadmachine.api.services.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ApiRepository @Inject constructor(
    private val apiService: ApiService
) {
    fun getConfigs(): Flow<SoundLibraries> = flow {
        val data = apiService.getConfigs()
        emit(data)
    }
}