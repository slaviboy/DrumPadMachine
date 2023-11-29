package com.slaviboy.drumpadmachine.screens.home.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slaviboy.drumpadmachine.screens.home.usecases.DownloadAudioZipUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val downloadAudioZipUseCase: DownloadAudioZipUseCase,
    private val context: Context
) : ViewModel() {

    fun downloadAudioZip() {
        viewModelScope.launch(Dispatchers.IO) {
            downloadAudioZipUseCase.execute(context.cacheDir, 10).collect {
                val b = 3
            }
        }
    }
}