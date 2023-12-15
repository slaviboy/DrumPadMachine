package com.slaviboy.drumpadmachine.screens.drumpad.viewmodels

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slaviboy.audio.DrumPadPlayer
import com.slaviboy.drumpadmachine.api.repositories.ApiRepository
import com.slaviboy.drumpadmachine.data.entities.Preset
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DrumPadViewModel @Inject constructor(
    private val repository: ApiRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private var drumPadPlayer: DrumPadPlayer? = null

    private val _page: MutableState<Int> = mutableIntStateOf(0)
    val page: State<Int> = _page // 0,1

    val numberOfRows = NUMBER_OF_ROWS
    val numberOfColumns = NUMBER_OF_COLUMNS

    fun init() = viewModelScope.launch {
        drumPadPlayer?.apply {
            setupAudioStream()
            loadWavAssets(context.assets)
            startAudioStream()
        }
    }

    fun terminate() = viewModelScope.launch {
        drumPadPlayer?.apply {
            teardownAudioStream()
            unloadWavAssets()
        }
        drumPadPlayer = null
    }

    fun playSound(row: Int, column: Int) = viewModelScope.launch {
        drumPadPlayer?.trigger(page.value, row, column)
    }

    fun loadSounds(preset: Preset) = viewModelScope.launch {
        terminate()
        awaitFrame()
        drumPadPlayer = DrumPadPlayer(numberOfRows, numberOfColumns).apply {
            setupAudioStream()
            loadWavFile("${context.cacheDir}/audio/${preset.id}")
            startAudioStream()
        }
    }

    fun movePage() {
        _page.value = if (page.value == 0) {
            1
        } else {
            0
        }
    }

    companion object {
        const val NUMBER_OF_ROWS = 4
        const val NUMBER_OF_COLUMNS = 3
    }
}