package com.slaviboy.drumpadmachine.screens.lessonplayer.composables

import android.view.MotionEvent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import com.slaviboy.composeunits.dw
import com.slaviboy.drumpadmachine.enums.PadColor
import com.slaviboy.drumpadmachine.screens.drumpad.helpers.DrumPadHelper
import com.slaviboy.drumpadmachine.screens.lessonplayer.models.LessonPlayerUiState

/**
 * Renders the lesson's fixed pad page (no side-toggle - a lesson is locked to whichever page
 * its pads live on) and forwards single taps to [onPadTapped]. Structurally mirrors
 * `DrumPadComposable`'s container-level [pointerInteropFilter] + [Rect] bounds tracking, but
 * only reacts to pointer-down (a lesson tap is a discrete hit, not a drag-glide gesture).
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LessonPadGrid(
    uiState: LessonPlayerUiState,
    onPadTapped: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var containerBound by remember { mutableStateOf(Rect.Zero) }
    val bounds = remember { MutableList(DrumPadHelper.numberItemsPerPage()) { Rect.Zero } }

    Column(
        modifier = modifier
            .pointerInteropFilter { event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN || event.actionMasked == MotionEvent.ACTION_POINTER_DOWN) {
                    val pointerIndex = event.actionIndex
                    val x = event.getX(pointerIndex) + containerBound.left
                    val y = event.getY(pointerIndex) + containerBound.top
                    val localIndex = bounds.indexOfFirst { it.contains(Offset(x, y)) }
                    if (localIndex != -1) {
                        onPadTapped(uiState.page * DrumPadHelper.numberItemsPerPage() + localIndex)
                    }
                }
                true
            }
            .onGloballyPositioned {
                containerBound = it.boundsInRoot()
            }
    ) {
        for (i in 0 until DrumPadHelper.numberOfRows) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.02.dw),
                horizontalArrangement = Arrangement.Center
            ) {
                for (j in 0 until DrumPadHelper.numberOfColumns) {
                    val localIndex = i * DrumPadHelper.numberOfColumns + j
                    val globalIndex = uiState.page * DrumPadHelper.numberItemsPerPage() + localIndex
                    LessonPadComposable(
                        padColor = uiState.padColors[globalIndex] ?: PadColor.None,
                        showGlow = globalIndex in uiState.glowingPads,
                        showTapIndicator = globalIndex in uiState.expectedPadIndices,
                        modifier = Modifier.weight(1f),
                        onPositionInParentChange = { rect -> bounds[localIndex] = rect }
                    )
                    if (j < DrumPadHelper.numberOfColumns - 1) {
                        Spacer(modifier = Modifier.width(0.01.dw))
                    }
                }
            }
            if (i < DrumPadHelper.numberOfRows - 1) {
                Spacer(modifier = Modifier.height(0.01.dw))
            }
        }
    }
}
