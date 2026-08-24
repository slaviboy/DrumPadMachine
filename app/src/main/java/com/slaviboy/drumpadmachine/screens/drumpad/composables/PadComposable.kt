package com.slaviboy.drumpadmachine.screens.drumpad.composables

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.enums.PadColor

@Composable
fun PadComposable(
    padColor: PadColor,
    showGlow: Boolean,
    modifier: Modifier = Modifier,
    glowDuration: Int = 250,
    glowFadeInDuration: Int = 40,
    onPositionInParentChange: (Rect) -> Unit
) {
    val alpha: Float by animateFloatAsState(
        targetValue = if (showGlow) 1f else 0f,
        label = "",
        animationSpec = tween(
            durationMillis = if (showGlow) glowFadeInDuration else glowDuration,
            easing = LinearEasing
        )
    )
    Box(
        modifier = modifier
            .wrapContentSize()
            .onGloballyPositioned {
                onPositionInParentChange(it.boundsInRoot())
            }
    ) {
        Image(
            painter = painterResource(id = padColor.value),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        )
        Image(
            painter = painterResource(id = R.drawable.ic_rect_glow),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .alpha(alpha)
        )
    }
}