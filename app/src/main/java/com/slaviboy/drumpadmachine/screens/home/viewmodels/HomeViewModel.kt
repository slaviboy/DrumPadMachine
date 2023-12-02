package com.slaviboy.drumpadmachine.screens.home.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slaviboy.drumpadmachine.api.results.Result
import com.slaviboy.drumpadmachine.data.entities.Config
import com.slaviboy.drumpadmachine.screens.home.usecases.DownloadAudioZipUseCase
import com.slaviboy.drumpadmachine.screens.home.usecases.GetAudioConfigUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val downloadAudioZipUseCase: DownloadAudioZipUseCase,
    private val getAudioConfigUseCase: GetAudioConfigUseCase,
    private val context: Context
) : ViewModel() {

    private val _dataState = MutableStateFlow<Config?>(null)
    val dataState = _dataState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getAudioConfigUseCase.execute().collect {
                when (it) {
                    is Result.Loading -> {

                    }

                    is Result.Success -> {
                        _dataState.value = it.data
                    }

                    is Result.Error -> {

                    }
                }
            }
        }
    }

    fun downloadAudioZip() {
        viewModelScope.launch(Dispatchers.IO) {
            downloadAudioZipUseCase.execute(context.cacheDir, 11).collect {
                val b = 3
            }
            getAudioConfigUseCase.execute().collect {
                val b = 3
            }
        }
    }
}