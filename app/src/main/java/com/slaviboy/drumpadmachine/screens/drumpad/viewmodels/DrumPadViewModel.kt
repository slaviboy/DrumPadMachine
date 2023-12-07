package com.slaviboy.drumpadmachine.screens.drumpad.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slaviboy.audio.DrumPadPlayer
import com.slaviboy.drumpadmachine.api.repositories.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DrumPadViewModel @Inject constructor(
    private val repository: ApiRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private var drumPadPlayer = DrumPadPlayer()
    private var page = 1

    fun init() = viewModelScope.launch {
        drumPadPlayer.apply {
            setupAudioStream()
            loadWavAssets(context.assets)
            startAudioStream()
        }
    }

    fun terminate() = viewModelScope.launch {
        drumPadPlayer.apply {
            teardownAudioStream()
            unloadWavAssets()
        }
    }

    fun playSound(row: Int, column: Int) = viewModelScope.launch {
        drumPadPlayer.trigger(page, row, column)
    }

    fun loadSounds(presetId: Int) = viewModelScope.launch {
        drumPadPlayer.apply {
            setupAudioStream()
            loadWavFile("${context.cacheDir}/audio/$presetId")
            startAudioStream()
        }
    }
}