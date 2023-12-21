package com.slaviboy.drumpadmachine.screens.drumpad.viewmodels

import android.content.Context
import android.util.Log
import android.view.MotionEvent
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slaviboy.audio.DrumPadPlayer
import com.slaviboy.drumpadmachine.api.repositories.ApiRepository
import com.slaviboy.drumpadmachine.data.entities.File
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
        val filesSize = preset.files?.size ?: 0
        rects = MutableList(filesSize) { Rect.Zero }
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

    private var rects: MutableList<Rect> = MutableList(24) { Rect.Zero }
    private var isMoved: MutableList<Int> = MutableList(10) { -1 }

    fun onTouchEvent(event: MotionEvent, row: Int, column: Int) {
        val index = getIndex(row, column)
        val currentFile = getFileAtIndex(index) ?: return
        val action = event.actionMasked
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
            playSoundAtIndex(index)
            isMoved[event.pointerCount - 1] = index
            Log.i("jojo", "$index")
        }
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
            if (currentFile.stopOnRelease == "1") {
                drumPadPlayer?.stopTrigger(index)
            }
            isMoved[event.pointerCount - 1] = -1
        }
        if (action == MotionEvent.ACTION_MOVE) {
            for (j in 0 until event.pointerCount) {
                val mActivePointerId = event.getPointerId(j)
                val (x, y) = event.findPointerIndex(mActivePointerId).let { pointerIndex ->
                    Pair((event.getX(pointerIndex) + rects[index].left), (event.getY(pointerIndex) + rects[index].top))
                }
                rects.forEachIndexed { i, rect ->
                    if (rect.contains(Offset(x, y)) && isIndexInPage(i) && isMoved[j] != i) {
                        Log.i("jojo", "$i ---  ${isMoved[j]}")
                        playSoundAtIndex(i)
                        isMoved[j] = i
                    }
                }
            }
        }
    }

    private fun getFileAtIndex(index: Int): File? {
        return _preset.value?.files?.get(index)
    }

    private fun playSoundAtIndex(index: Int) {
        val currentFile = getFileAtIndex(index) ?: return
        _preset.value?.files?.forEachIndexed { i, file ->
            if (file.choke == currentFile.choke && file.choke != 0) {
                drumPadPlayer?.stopTrigger(i)
            }
        }
        drumPadPlayer?.trigger(index)
    }

    private fun getIndex(row: Int, column: Int): Int {
        return (_page.value * numberItemsPerPage()) + row * numberOfColumns + column
    }

    private fun isIndexInPage(index: Int): Boolean {
        return (_page.value == index / numberItemsPerPage())
    }

    private fun numberItemsPerPage(): Int {
        return numberOfColumns * numberOfRows
    }

    fun onPositionInParentChange(
        rect: Rect,
        row: Int,
        column: Int
    ) {
        val index = getIndex(row, column)
        rects[index] = rect
    }

    companion object {
        private const val NUMBER_OF_ROWS = 4
        private const val NUMBER_OF_COLUMNS = 3
    }
}