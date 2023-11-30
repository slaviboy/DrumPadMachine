package com.slaviboy.drumpadmachine.screens.home.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slaviboy.drumpadmachine.api.entities.SoundLibraries
import com.slaviboy.drumpadmachine.screens.home.usecases.DownloadAudioConfigUseCase
import com.slaviboy.drumpadmachine.screens.home.usecases.DownloadAudioZipUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val downloadAudioZipUseCase: DownloadAudioZipUseCase,
    private val downloadAudioConfigUseCase: DownloadAudioConfigUseCase,
    private val context: Context
) : ViewModel() {

    private val _dataState = MutableStateFlow<SoundLibraries?>(null)
    val dataState = _dataState.asStateFlow()

    fun downloadAudioZip() {
        viewModelScope.launch(Dispatchers.IO) {
            downloadAudioZipUseCase.execute(context.cacheDir, 11).collect {
                val b = 3
            }
            downloadAudioConfigUseCase.execute().collect {
                val b = 3
            }
        }
    }
}