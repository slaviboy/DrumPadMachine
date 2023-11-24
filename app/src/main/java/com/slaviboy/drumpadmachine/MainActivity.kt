package com.slaviboy.drumpadmachine

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.slaviboy.audio.NativeLib
import com.slaviboy.composeunits.dw
import com.slaviboy.composeunits.initSize
import com.slaviboy.drumpadmachine.global.allTrue
import com.slaviboy.drumpadmachine.ui.backgroundGradientBottom
import com.slaviboy.drumpadmachine.ui.backgroundGradientTop

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val a = NativeLib().stringFromJNI()
        initSize()
        setContent {
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
                                Pad(
                                    Pad(color = PadColor.Aqua, isActive = true),
                                    modifier = Modifier
                                        .weight(1f)
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
    }
}

enum class PadColor(@DrawableRes val value: Int) {
    Red(R.drawable.ic_rect_red),
    Green(R.drawable.ic_rect_green),
    Blue(R.drawable.ic_rect_blue),
    Pink(R.drawable.ic_rect_pink),
    Orange(R.drawable.ic_rect_orange),
    Purple(R.drawable.ic_rect_purple),
    Aqua(R.drawable.ic_rect_aqua)
}

data class Pad(
    val color: PadColor,
    val isActive: Boolean
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Pad(
    pad: Pad,
    modifier: Modifier = Modifier
) {
    var showGlow by remember {
        mutableStateOf(false)
    }
    var motionEvent by remember {
        mutableIntStateOf(MotionEvent.ACTION_UP)
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
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {
                        showGlow = true
                    }

                    MotionEvent.ACTION_UP -> {
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
            painter = painterResource(id = pad.color.value),
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