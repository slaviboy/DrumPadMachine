package com.slaviboy.drumpadmachine.screens.drumpad.viewmodels

import android.content.Context
import android.view.MotionEvent
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slaviboy.audio.DrumPadPlayer
import com.slaviboy.drumpadmachine.api.repositories.ApiRepository
import com.slaviboy.drumpadmachine.data.entities.Preset
import com.slaviboy.drumpadmachine.enums.PadColor
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

    private val _preset: MutableState<Preset?> = mutableStateOf(null)
    val preset: State<Preset?> = _preset

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

    fun loadSounds(preset: Preset) = viewModelScope.launch {
        _preset.value = preset
        terminate()
        awaitFrame()
        drumPadPlayer = DrumPadPlayer().apply {
            setupAudioStream()
            loadWavFile(
                dirPath = "${context.cacheDir}/audio/${preset.id}",
                filenames = _preset.value?.files?.map {
                    it.filename
                } ?: listOf()
            )
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

    fun getPadColor(row: Int, column: Int): PadColor {
        val index = getIndex(row, column)
        val color = _preset.value?.files?.getOrNull(index)?.color ?: return PadColor.None
        return when (color) {
            "red" -> PadColor.Red
            "blue" -> PadColor.Blue
            "green" -> PadColor.Green
            "purple" -> PadColor.Purple
            "yellow" -> PadColor.Orange
            else -> PadColor.None
        }
    }

    fun onTouchEvent(motionEvent: Int, row: Int, column: Int) {
        val index = getIndex(row, column)
        val currentFile = _preset.value?.files?.get(index) ?: return
        if (motionEvent == MotionEvent.ACTION_DOWN || motionEvent == MotionEvent.ACTION_POINTER_DOWN) {
            _preset.value?.files?.forEachIndexed { i, file ->
                if (file.choke == currentFile.choke) {
                    drumPadPlayer?.stopTrigger(i)
                }
            }
            playSound(row, column)
        }
        if (motionEvent == MotionEvent.ACTION_UP || motionEvent == MotionEvent.ACTION_POINTER_UP) {
            if (currentFile.stopOnRelease == "1") {
                stopSound(row, column)
            }
        }
    }

    private fun getIndex(row: Int, column: Int): Int {
        return (_page.value * numberOfColumns * numberOfRows) + row * numberOfColumns + column
    }

    private fun playSound(row: Int, column: Int) = viewModelScope.launch {
        drumPadPlayer?.trigger(
            index = getIndex(row, column)
        )
    }

    private fun stopSound(row: Int, column: Int) = viewModelScope.launch {
        drumPadPlayer?.stopTrigger(
            index = getIndex(row, column)
        )
    }

    companion object {
        private const val NUMBER_OF_ROWS = 4
        private const val NUMBER_OF_COLUMNS = 3
    }
}