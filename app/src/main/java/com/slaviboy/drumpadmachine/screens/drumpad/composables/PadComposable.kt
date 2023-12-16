package com.slaviboy.drumpadmachine.screens.drumpad.composables

import android.view.MotionEvent
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.enums.PadColor
import com.slaviboy.drumpadmachine.global.allTrue

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PadComposable(
    padColor: PadColor,
    modifier: Modifier = Modifier,
    onTouchDownEvent: (Int) -> Unit
) {
    var showGlow by remember {
        mutableStateOf(false)
    }
    var motionEvent by remember {
        mutableStateOf(MotionEvent.ACTION_UP)
    }
    val alpha: Float by animateFloatAsState(
        targetValue = if (showGlow) 1f else 0f,
        label = "",
        animationSpec = tween(
            durationMillis = 250,
            easing = FastOutLinearInEasing
        ),
        finishedListener = {
            val fingerIsNotDown = allTrue(
                motionEvent != MotionEvent.ACTION_DOWN,
                motionEvent != MotionEvent.ACTION_POINTER_DOWN
            )
            val fingerIsNotMoving = (motionEvent != MotionEvent.ACTION_MOVE)
            val isGlowShown = (it == 1f)
            if (isGlowShown && fingerIsNotDown && fingerIsNotMoving) {
                showGlow = false
            }
        }
    )
    Box(
        modifier = modifier
            .wrapContentSize()
            .pointerInteropFilter {
                motionEvent = it.action
                when (it.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                        showGlow = true
                        onTouchDownEvent(it.action)
                    }

                    MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                        val isGlowShown = (alpha == 1f)
                        if (isGlowShown) {
                            showGlow = false
                        }
                    }

                    MotionEvent.ACTION_MOVE -> {}

                    else -> false
                }
                true
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