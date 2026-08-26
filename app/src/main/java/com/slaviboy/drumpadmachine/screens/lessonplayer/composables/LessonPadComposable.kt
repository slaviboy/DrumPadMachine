package com.slaviboy.drumpadmachine.screens.lessonplayer.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.slaviboy.composeunits.DeviceHeight
import com.slaviboy.composeunits.DeviceWidth
import com.slaviboy.composeunits.Density
import com.slaviboy.composeunits.ScaleDensity
import com.slaviboy.composeunits.dw
import com.slaviboy.composeunits.sw
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.enums.PadColor
import com.slaviboy.drumpadmachine.screens.drumpad.composables.PadComposable
import com.slaviboy.drumpadmachine.ui.RobotoFont

/** Dimmed resting opacity for a pad that's used in the lesson but not currently "live". */
private const val UsedPadRestingAlpha = 0.75f

/**
 * Wraps the existing [PadComposable] (base color art + glow flash, reused as-is) with the
 * white-ball + "Tap" indicator for the pad the user must currently hit. Pads not used in this
 * lesson are simply passed [PadColor.None] by the caller - that drawable is already a styled
 * dim/inactive rect, so no extra disabled overlay is drawn here (layering one on top produced
 * a mismatched second background, since the scrim's square corners didn't match the art's
 * rounded ones).
 *
 * Pads that are used in the lesson rest at [UsedPadRestingAlpha] so the currently-relevant pad
 * stands out, then jump to full opacity while it's being played back ([showGlow], the "Listen"
 * part) or is the one the user must tap ([showTapIndicator], the "Play" part).
 */
@Composable
fun LessonPadComposable(
    padColor: PadColor,
    showGlow: Boolean,
    showTapIndicator: Boolean,
    modifier: Modifier = Modifier,
    onPositionInParentChange: (Rect) -> Unit
) {
    val isUsedPad = padColor != PadColor.None
    val alpha: Float by animateFloatAsState(
        targetValue = if (isUsedPad && !showGlow && !showTapIndicator) UsedPadRestingAlpha else 1f,
        label = "",
        animationSpec = tween(durationMillis = 150)
    )
    Box(modifier = modifier.alpha(alpha)) {
        PadComposable(
            padColor = padColor,
            showGlow = showGlow,
            modifier = Modifier.fillMaxWidth(),
            onPositionInParentChange = onPositionInParentChange
        )
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

/** Fixed reference metrics so `dw`/`sw` percentage units resolve to sane values in previews. */
private fun initPreviewUnits() {
    DeviceWidth = 1080f
    DeviceHeight = 2400f
    Density = 3f
    ScaleDensity = 3f
}

@Preview(showBackground = true, backgroundColor = 0xFF232339)
@Composable
private fun LessonPadComposablePreview() {
    initPreviewUnits()
    Row {
        // Idle - unused pad, dim/inactive art
        LessonPadComposable(
            padColor = PadColor.None,
            showGlow = false,
            showTapIndicator = false,
            modifier = Modifier.width(90.dp),
            onPositionInParentChange = {}
        )
        // Idle - used pad, its real preset color
        LessonPadComposable(
            padColor = PadColor.Blue,
            showGlow = false,
            showTapIndicator = false,
            modifier = Modifier.width(90.dp),
            onPositionInParentChange = {}
        )
        // Listen-phase flash / correct-tap feedback
        LessonPadComposable(
            padColor = PadColor.Orange,
            showGlow = true,
            showTapIndicator = false,
            modifier = Modifier.width(90.dp),
            onPositionInParentChange = {}
        )
        // Play-phase "tap here" target
        LessonPadComposable(
            padColor = PadColor.Blue,
            showGlow = false,
            showTapIndicator = true,
            modifier = Modifier.width(90.dp),
            onPositionInParentChange = {}
        )
    }
}
