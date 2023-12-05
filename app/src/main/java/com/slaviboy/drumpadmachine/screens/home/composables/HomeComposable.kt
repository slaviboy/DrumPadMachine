package com.slaviboy.drumpadmachine.screens.home.composables

import androidx.compose.animation.core.TargetBasedAnimation
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.CrossFade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.slaviboy.composeunits.dh
import com.slaviboy.composeunits.dw
import com.slaviboy.composeunits.sw
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.api.results.Result
import com.slaviboy.drumpadmachine.data.entities.Preset
import com.slaviboy.drumpadmachine.extensions.bounceClick
import com.slaviboy.drumpadmachine.extensions.click
import com.slaviboy.drumpadmachine.modules.NetworkModule.Companion.BASE_URL
import com.slaviboy.drumpadmachine.screens.destinations.DrumPadComposableDestination
import com.slaviboy.drumpadmachine.screens.home.viewmodels.HomeViewModel
import com.slaviboy.drumpadmachine.ui.RobotoFont
import com.slaviboy.drumpadmachine.ui.backgroundGradientBottom
import com.slaviboy.drumpadmachine.ui.backgroundGradientTop

@Composable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }

@Composable
fun Float.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }

fun Float.mapValue(fromY: Float, toY: Float): Float {
    return (toY - fromY) * this + fromY
}

fun Float.mapValue(fromY: Dp, toY: Dp): Dp {
    return (toY - fromY) * this + fromY
}

fun Float.accelerateValue(accelerationFactor: Float = 0.1f): Float {
    val acceleratedValue = this - accelerationFactor * this
    return acceleratedValue.coerceIn(0.0f, 1.0f)
}

fun Float.decelerateValue(decelerationFactor: Float = 0.7f): Float {
    val deceleratedValue = this + (1 - this) * (1 - decelerationFactor)
    return deceleratedValue.coerceIn(0.0f, 1.0f)
}

@OptIn(ExperimentalGlideComposeApi::class)
@RootNavGraph(start = true)
@Destination
@Composable
fun HomeComposable(
    navigator: DestinationsNavigator,
    homeViewModel: HomeViewModel,
    onError: (error: String) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        backgroundGradientTop,
                        backgroundGradientBottom
                    )
                )
            )
    ) {
        val fromWidth by remember {
            mutableStateOf(0.35.dw)
        }
        val fromHeight by remember {
            mutableStateOf(0.35.dw)
        }
        val toWidth by remember {
            mutableStateOf(0.76.dw)
        }
        val toHeight by remember {
            mutableStateOf(0.33.dh)
        }
        var animatedWidth by remember {
            mutableStateOf(0.35.dw)
        }
        var animatedHeight by remember {
            mutableStateOf(0.35.dw)
        }
        var fromX by remember {
            mutableFloatStateOf(0f)
        }
        var fromY by remember {
            mutableFloatStateOf(0f)
        }
        var toX by remember {
            mutableFloatStateOf(0f)
        }
        var toY by remember {
            mutableFloatStateOf(0f)
        }
        var animatedX by remember {
            mutableFloatStateOf(0f)
        }
        var animatedY by remember {
            mutableFloatStateOf(0f)
        }
        var animationFlag by remember {
            mutableStateOf<Boolean?>(null)
        }
        var isReversed by remember {
            mutableStateOf(true)
        }
        var animatedValue by remember {
            mutableFloatStateOf(0f)
        }
        var clickedPreset by remember {
            mutableStateOf<Preset?>(null)
        }
        val animation = remember {
            TargetBasedAnimation(
                animationSpec = tween(400),
                typeConverter = Float.VectorConverter,
                initialValue = 0f,
                targetValue = 1f
            )
        }
        LaunchedEffect(animationFlag) {
            animationFlag ?: return@LaunchedEffect
            val startTime = withFrameNanos { it }
            var playTime: Long
            do {
                playTime = withFrameNanos { it } - startTime
                var value = animation.getValueFromNanos(playTime) // [0,1]
                if (isReversed) {
                    value = Math.abs(value - 1f)
                }
                animatedValue = value
                animatedX = value.mapValue(fromX, toX)
                animatedY = value.mapValue(fromY, toY)
                animatedWidth = value.mapValue(fromWidth, toWidth)
                animatedHeight = value.mapValue(fromHeight, toHeight)
            } while (playTime <= animation.durationNanos)
        }
        val audioConfigState = homeViewModel.audioConfigState.value
        val audioZipState = homeViewModel.audioZipState.value
        val categoryMaps = homeViewModel.categoriesMapState.value
        LaunchedEffect(audioConfigState) {
            if (audioConfigState is Result.Error) {
                onError(audioConfigState.errorMessage)
            }
        }
        LaunchedEffect(audioZipState) {
            if (audioZipState is Result.Error) {
                onError(audioZipState.errorMessage)
            }
            if (audioZipState is Result.Success) {
                navigator.navigate(
                    DrumPadComposableDestination(audioZipState.data)
                )
            }
        }
        LazyColumn(
            modifier = Modifier
                .padding(bottom = 0.18.dw)
        ) {
            item {
                TopBox()
                Spacer(
                    modifier = Modifier
                        .height(0.06.dw)
                )
            }
            items(categoryMaps.size) { i ->
                val categoryName = categoryMaps.keys.elementAt(i)
                val presets = categoryMaps[categoryName]!!
                Row(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(horizontal = 0.03.dw),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = categoryName,
                        color = Color.White,
                        fontFamily = RobotoFont,
                        fontSize = 0.055.sw,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "See all".uppercase(),
                        color = Color.White,
                        fontFamily = RobotoFont,
                        fontSize = 0.032.sw,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(
                    modifier = Modifier
                        .height(0.02.dw)
                )
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.03.dw)
                ) {
                    items(presets.size) { j ->
                        val preset = presets[j]
                        var x by remember {
                            mutableFloatStateOf(0f)
                        }
                        var y by remember {
                            mutableFloatStateOf(0f)
                        }
                        Column(
                            modifier = Modifier
                                .onGloballyPositioned {
                                    val position = it.positionInRoot()
                                    x = position.x
                                    y = position.y
                                }
                                .bounceClick {
                                    fromX = x
                                    fromY = y
                                    isReversed = false
                                    animationFlag = !(animationFlag ?: true)
                                    clickedPreset = preset
                                }
                        ) {
                            Box(
                                contentAlignment = Alignment.BottomEnd
                            ) {
                                GlideImage(
                                    model = "${BASE_URL}cover_icons/${preset.id}.jpg",
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(0.35.dw)
                                        .clip(RoundedCornerShape(0.04.dw)),
                                    transition = CrossFade
                                )
                                Image(
                                    painter = painterResource(id = R.drawable.ic_play_button),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(0.06.dw)
                                        .offset(
                                            x = (-0.02).dw,
                                            y = (-0.02).dw
                                        )
                                )
                            }
                            Spacer(
                                modifier = Modifier
                                    .height(0.02.dw)
                            )
                            Text(
                                text = preset.name ?: "",
                                color = Color.White,
                                fontFamily = RobotoFont,
                                fontSize = 0.035.sw,
                                fontWeight = FontWeight.Normal
                            )
                            Text(
                                text = preset.author ?: "",
                                color = Color.White,
                                fontFamily = RobotoFont,
                                fontSize = 0.023.sw,
                                fontWeight = FontWeight.Normal
                            )
                        }
                        Spacer(
                            modifier = Modifier
                                .width(0.04.dw)
                        )
                    }
                }
                Spacer(
                    modifier = Modifier
                        .height(0.06.dw)
                )
            }
        }

        if (animatedValue > 0f)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(animatedValue)
                    //.alpha(if (animatedValue < 0.5f) 0f else animatedValue.decelerateValue())
                    .background(Color(0x88000000))
                    .click { }
            ) {}

        if (animatedValue > 0)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = animatedValue
                    }
                    .background(Color(0x88000000))
                    .click { }
            ) {
                Box(
                    modifier = Modifier
                        .width(0.76.dw)
                        .height(0.51.dh)
                        .background(Color.White, RoundedCornerShape(0.04.dw))
                        .align(Alignment.Center)
                        .onGloballyPositioned {
                            val position = it.positionInWindow()
                            toX = position.x
                            toY = position.y
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .width(fromWidth)
                            .height(fromHeight)
                            .clip(RoundedCornerShape(topStart = 0.04.dw, topEnd = 0.04.dw))
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.18.dh)
                            .align(Alignment.BottomCenter)
                            .padding(
                                horizontal = 0.03.dw,
                                vertical = 0.03.dw
                            ),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .bounceClick {
                                    homeViewModel.getSoundForFree(clickedPreset?.id)
                                }
                                .border(1.dp, Color(0xFFBFBFC0), RoundedCornerShape(0.02.dw))
                                .padding(start = 0.045.dw),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier
                                    .wrapContentHeight()
                                    .weight(1f)
                            ) {
                                Text(
                                    text = "Get this sound for free",
                                    color = Color(0xFF0A0A0F),
                                    fontFamily = RobotoFont,
                                    fontSize = 0.042.sw,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Watch a short video",
                                    color = Color(0xFF78787B),
                                    fontFamily = RobotoFont,
                                    fontSize = 0.032.sw,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                            Image(
                                painter = painterResource(id = R.drawable.ic_arrow_right),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(0.12.dw)
                                    .padding(0.03.dw)
                            )
                        }
                        Spacer(
                            modifier = Modifier
                                .height(0.02.dw)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .bounceClick {
                                    homeViewModel.unlockAllSounds()
                                }
                                .background(Color(0xFFFFD112), RoundedCornerShape(0.02.dw))
                                .padding(start = 0.045.dw),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier
                                    .wrapContentHeight()
                                    .weight(1f)
                            ) {
                                Text(
                                    text = "Unlock all sounds",
                                    color = Color(0xFF0A0A0F),
                                    fontFamily = RobotoFont,
                                    fontSize = 0.042.sw,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Try for free",
                                    color = Color(0xFF78787B),
                                    fontFamily = RobotoFont,
                                    fontSize = 0.032.sw,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                            Image(
                                painter = painterResource(id = R.drawable.ic_arrow_right),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(0.12.dw)
                                    .padding(0.03.dw)
                            )
                        }
                    }
                }
            }

        if (animatedValue > 0)
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .offset(
                            x = animatedX.pxToDp(),
                            y = animatedY.pxToDp()
                        )
                        .width(animatedWidth)
                        .height(animatedHeight)
                        .clip(
                            RoundedCornerShape(
                                topStart = 0.04.dw,
                                topEnd = 0.04.dw,
                                bottomStart = 0.04.dw * (1f - animatedValue),
                                bottomEnd = 0.04.dw * (1f - animatedValue)
                            )
                        )
                        .onGloballyPositioned {

                        }
                ) {
                    GlideImage(
                        model = "${BASE_URL}covers/${clickedPreset?.id}.jpg",
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        transition = CrossFade
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = null,
                        modifier = Modifier
                            .size(0.07.dw)
                            .align(Alignment.TopEnd)
                            .offset(
                                x = (-0.035).dw,
                                y = (0.035).dw
                            )
                            .alpha(0.85f * animatedValue)
                            .bounceClick {
                                isReversed = true
                                animationFlag = !(animationFlag ?: true)
                            }
                    )
                }
            }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.18.dw)
                .background(
                    Color(0xFF19182C)
                )
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            homeViewModel.menuItemsState.value.forEach {
                val color by remember {
                    mutableStateOf(
                        if (it.isSelected) {
                            Color(0xFFffd112)
                        } else {
                            Color(0xFF444350)
                        }
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight()
                        .bounceClick {

                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = it.iconResId),
                        contentDescription = null,
                        modifier = Modifier
                            .size(0.09.dw),
                        colorFilter = ColorFilter.tint(color)
                    )
                    Text(
                        text = stringResource(id = it.titleResId),
                        color = color,
                        fontFamily = RobotoFont,
                        fontSize = 0.02.sw,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
        if (homeViewModel.audioConfigState.value is Result.Loading) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .wrapContentSize()
                    .padding(
                        top = 0.25.dh,
                        bottom = 0.18.dw
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(0.15.dw),
                    strokeWidth = 0.012.dw,
                    strokeCap = StrokeCap.Round,
                    color = Color(0xFF8F56BD),
                    trackColor = Color.White,
                )
                Spacer(
                    modifier = Modifier
                        .height(0.02.dw)
                )
                Text(
                    text = stringResource(id = R.string.loading).uppercase(),
                    color = Color.White,
                    fontFamily = RobotoFont,
                    fontSize = 0.035.sw,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun TopBox() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.25.dh)
            .background(
                brush = Brush.horizontalGradient(
                    listOf(
                        Color(0xFF274973),
                        Color(0xFFE361F9)
                    )
                )
            )
    ) {
        Text(
            text = "Unlock\nall sounds".uppercase(),
            color = Color.White,
            fontFamily = RobotoFont,
            fontSize = 0.06.sw,
            fontWeight = FontWeight.Black,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = 0.04.dw)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = -(0.04).dw, y = -(0.04).dw)
                .bounceClick {

                }
                .background(Color(0xFFFFD112), CircleShape)
                .padding(horizontal = 0.07.dw, vertical = 0.03.dw),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Upgrade".uppercase(),
                color = Color(0xFF21212B),
                fontFamily = RobotoFont,
                fontSize = 0.034.sw,
                fontWeight = FontWeight.Black
            )
        }
    }
}