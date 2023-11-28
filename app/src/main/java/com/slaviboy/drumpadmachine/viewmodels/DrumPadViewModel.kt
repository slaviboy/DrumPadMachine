package com.slaviboy.drumpadmachine.viewmodels

import android.content.res.AssetManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slaviboy.audio.DrumPadPlayer
import com.slaviboy.drumpadmachine.api.entities.SoundLibraries
import com.slaviboy.drumpadmachine.api.repositories.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DrumPadViewModel @Inject constructor(
    private val repository: ApiRepository
) : ViewModel() {

    private val _dataState = MutableStateFlow<SoundLibraries?>(null)
    val dataState = _dataState.asStateFlow()

    private var mDrumPadPlayer = DrumPadPlayer()

    fun downloadConfigs() {
        viewModelScope.launch {
            repository.getConfigs().collect {
                _dataState.value = it
            }
        }
    }

    fun init(assets: AssetManager) {
        mDrumPadPlayer.setupAudioStream()
        mDrumPadPlayer.loadWavAssets(assets)
        mDrumPadPlayer.startAudioStream()
    }

    fun terminate() {
        mDrumPadPlayer.teardownAudioStream()
        mDrumPadPlayer.unloadWavAssets()
    }

    fun playSound(row: Int, column: Int) {
        mDrumPadPlayer.trigger(DrumPadPlayer.RIDECYMBAL)
        downloadConfigs()
    }
}