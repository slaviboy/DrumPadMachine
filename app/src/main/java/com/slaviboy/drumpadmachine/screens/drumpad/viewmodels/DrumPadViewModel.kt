package com.slaviboy.drumpadmachine.screens.drumpad.viewmodels

import android.content.Context
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
import com.slaviboy.drumpadmachine.screens.drumpad.helpers.DrumPadHelper
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

    private var containerBound: Rect = Rect.Zero
    private var bounds: MutableList<Rect> = MutableList(24) { Rect.Zero }

    private var _isMoved: MutableState<List<Boolean>> = mutableStateOf(MutableList(24) { false })
    val isMoved: State<List<Boolean>> = _isMoved

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
        bounds = MutableList(filesSize) { Rect.Zero }
    }

    fun movePage() {
        _page.value = if (page.value == 0) {
            1
        } else {
            0
        }
    }

    fun getPadColor(row: Int, column: Int): PadColor {
        val index = DrumPadHelper.getIndex(_page.value, row, column)
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

    fun getShowGlow(row: Int, column: Int): Boolean {
        return _isMoved.value[DrumPadHelper.getIndex(_page.value, row, column)]
    }

    fun onContainerTouch(event: MotionEvent) = viewModelScope.launch {
        val isMoved = _isMoved.value.toMutableList()
        val action = event.actionMasked
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
            val (x, y) = getPositionForFinger(event, event.actionIndex)
            val index = findMatchItemIndex(x, y)
            if (index != -1) {
                playSoundAtIndex(index)
                isMoved[index] = true
            }
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_CANCEL) {
            val (x, y) = getPositionForFinger(event, event.actionIndex)
            val index = findMatchItemIndex(x, y)
            if (index != -1) {
                val currentFile = getFileAtIndex(index) ?: return@launch
                if (currentFile.stopOnRelease == "1") {
                    drumPadPlayer?.stopTrigger(index)
                }
                isMoved[index] = false
            }
        } else if (action == MotionEvent.ACTION_MOVE) {
            val matchIndices = mutableListOf<Int>()
            for (j in 0 until event.pointerCount) {
                val (x, y) = getPositionForFinger(event, j)
                val index = findMatchItemIndex(x, y)
                if (index != -1) {
                    matchIndices.add(index)
                }
                if (index != -1 && !isMoved[index]) {
                    playSoundAtIndex(index)
                    isMoved[index] = true
                }
            }
            isMoved.forEachIndexed { index, _ ->
                if (!matchIndices.contains(index)) {
                    isMoved[index] = false
                }
            }
        }
        this@DrumPadViewModel._isMoved.value = isMoved
    }

    fun onPositionInParentChange(
        rect: Rect,
        row: Int,
        column: Int
    ) {
        val index = DrumPadHelper.getIndex(_page.value, row, column)
        bounds[index] = rect
    }

    fun setContainerBound(bound: Rect) {
        containerBound = bound
    }

    private fun getPositionForFinger(event: MotionEvent, fingerIndex: Int): Pair<Float, Float> {
        val mActivePointerId = event.getPointerId(fingerIndex)
        return event.findPointerIndex(mActivePointerId).let { pointerIndex ->
            event.getX(pointerIndex) + containerBound.left to event.getY(pointerIndex) + containerBound.top
        }
    }

    private fun findMatchItemIndex(x: Float, y: Float): Int {
        bounds.forEachIndexed { i, rect ->
            if (rect.contains(Offset(x, y)) && DrumPadHelper.isIndexInPage(_page.value, i)) {
                return i
            }
        }
        return -1
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
}