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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.enums.PadColor
import kotlinx.coroutines.delay

@Composable
fun PadComposable(
    padColor: PadColor,
    showGlow: Boolean,
    modifier: Modifier = Modifier,
    glowDuration: Int = 250,
    onPositionInParentChange: (Rect) -> Unit
) {
    var showGlowInner by remember {
        mutableStateOf(showGlow)
    }
    val alpha: Float by animateFloatAsState(
        targetValue = if (showGlowInner) 1f else 0f,
        label = "",
        animationSpec = tween(
            durationMillis = glowDuration,
            easing = LinearEasing
        )
    )
    LaunchedEffect(showGlow) {
        val delayMs = if (!showGlow && alpha < 1f) {
            glowDuration.toLong()
        } else {
            0L
        }
        delay(delayMs)
        showGlowInner = showGlow
    }
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