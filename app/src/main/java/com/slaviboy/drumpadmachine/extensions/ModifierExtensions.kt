package com.slaviboy.drumpadmachine.extensions

import android.annotation.SuppressLint
import android.os.SystemClock
import android.view.MotionEvent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInteropFilter
import com.slaviboy.drumpadmachine.enums.ButtonState

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.bounceClick(
    debounceTime: Long = 600L,
    shrinkFact: Float = 0.85f,
    onClick: () -> Unit = {}
) = composed {
    var lastClickTime by remember {
        mutableStateOf(0L)
    }
    var buttonState by remember { mutableStateOf(ButtonState.Idle) }
    val scale by animateFloatAsState(
        targetValue = if (buttonState == ButtonState.Pressed) {
            shrinkFact
        } else {
            1f
        },
        animationSpec = spring(),
        label = ""
    )
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInteropFilter {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    buttonState = ButtonState.Pressed
                }

                MotionEvent.ACTION_UP -> {
                    buttonState = ButtonState.Idle
                    if (SystemClock.elapsedRealtime() - lastClickTime > debounceTime) {
                        onClick()
                        lastClickTime = SystemClock.elapsedRealtime()
                    }
                }

                MotionEvent.ACTION_CANCEL -> {
                    buttonState = ButtonState.Idle
                }
            }
            true
        }
}

@SuppressLint("ModifierFactoryUnreferencedReceiver")
fun Modifier.click(
    debounceTime: Long = 600L,
    onClick: () -> Unit
): Modifier = composed {
    var lastClickTime by remember {
        mutableStateOf(0L)
    }
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) {
        if (SystemClock.elapsedRealtime() - lastClickTime > debounceTime) {
            onClick()
            lastClickTime = SystemClock.elapsedRealtime()
        }
    }
}