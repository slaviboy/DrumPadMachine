package com.slaviboy.drumpadmachine.screens.drumpad.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.slaviboy.composeunits.dw
import com.slaviboy.drumpadmachine.data.Pad
import com.slaviboy.drumpadmachine.enums.PadColor
import com.slaviboy.drumpadmachine.screens.drumpad.viewmodels.DrumPadViewModel
import com.slaviboy.drumpadmachine.ui.backgroundGradientBottom
import com.slaviboy.drumpadmachine.ui.backgroundGradientTop

@RootNavGraph(start = false)
@Destination
@Composable
fun DrumPadComposable(
    navigator: DestinationsNavigator,
    drumPadViewModel: DrumPadViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        backgroundGradientTop,
                        backgroundGradientBottom
                    )
                )
            )
    ) {
        Column {
            for (i in 0 until 5) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.05.dw),
                    horizontalArrangement = Arrangement.Center
                ) {
                    for (j in 0 until 3) {
                        PadComposable(
                            pad = Pad(color = PadColor.Aqua, isActive = true),
                            modifier = Modifier
                                .weight(1f),
                            onMotionActionChanged = {
                                drumPadViewModel.playSound(
                                    row = j,
                                    column = i
                                )
                            }
                        )
                        if (j < 2) {
                            Spacer(
                                modifier = Modifier
                                    .width(0.03.dw)
                            )
                        }
                    }
                }
                if (i < 4) {
                    Spacer(
                        modifier = Modifier
                            .height(0.03.dw)
                    )
                }
            }
        }
    }
}