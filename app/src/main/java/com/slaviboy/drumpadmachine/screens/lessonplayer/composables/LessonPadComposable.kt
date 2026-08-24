package com.slaviboy.drumpadmachine.screens.lessonplayer.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.slaviboy.composeunits.dw
import com.slaviboy.composeunits.sw
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.enums.PadColor
import com.slaviboy.drumpadmachine.screens.drumpad.composables.PadComposable
import com.slaviboy.drumpadmachine.ui.RobotoFont

/**
 * Wraps the existing [PadComposable] (base color art + glow flash, reused as-is) with two new
 * overlays for the lesson player: a dark scrim for pads not used in this lesson, and a
 * white-ball + "Tap" indicator for the pad the user must currently hit.
 */
@Composable
fun LessonPadComposable(
    padColor: PadColor,
    enabled: Boolean,
    showGlow: Boolean,
    showTapIndicator: Boolean,
    modifier: Modifier = Modifier,
    onPositionInParentChange: (Rect) -> Unit
) {
    Box(modifier = modifier) {
        PadComposable(
            padColor = padColor,
            showGlow = showGlow,
            modifier = Modifier.fillMaxWidth(),
            onPositionInParentChange = onPositionInParentChange
        )
        if (!enabled) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color(0xCC232339))
            )
        }
        if (showTapIndicator) {
            Text(
                text = stringResource(id = R.string.tap).uppercase(),
                color = Color(0xFF232339),
                fontFamily = RobotoFont,
                fontWeight = FontWeight.Bold,
                fontSize = 0.04.sw,
                modifier = Modifier
                    .align(Alignment.Center)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(0.015.dw)
                    .size(0.045.dw)
                    .shadow(elevation = 0.006.dw, shape = CircleShape)
                    .background(color = Color.White, shape = CircleShape)
            )
        }
    }
}
