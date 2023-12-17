package com.slaviboy.drumpadmachine.screens.presets.composables

import androidx.compose.animation.core.TargetBasedAnimation
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.slaviboy.composeunits.dh
import com.slaviboy.composeunits.dw
import com.slaviboy.composeunits.sw
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.composables.NoItems
import com.slaviboy.drumpadmachine.composables.ScrollableContainer
import com.slaviboy.drumpadmachine.data.entities.Preset
import com.slaviboy.drumpadmachine.events.ErrorEvent
import com.slaviboy.drumpadmachine.events.NavigationEvent
import com.slaviboy.drumpadmachine.extensions.ObserveAsEvents
import com.slaviboy.drumpadmachine.extensions.mapValue
import com.slaviboy.drumpadmachine.screens.destinations.DrumPadComposableDestination
import com.slaviboy.drumpadmachine.screens.home.composables.HomePresetDetails
import com.slaviboy.drumpadmachine.screens.presets.viewmodels.PresetsViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalComposeUiApi::class)
@Destination
@Composable
fun PresetsComposable(
    navigator: DestinationsNavigator,
    presetsViewModel: PresetsViewModel,
    name: String,
    presets: Array<Preset>,
    onError: (error: String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(presets) {
        presetsViewModel.initPresets(presets)
    }

    val fromWidth by remember {
        mutableStateOf(0.41.dw)
    }
    val fromHeight by remember {
        mutableStateOf(0.41.dw)
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
    var topBarHeight by remember {
        mutableStateOf(0.dw)
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
    presetsViewModel.errorEventFlow.ObserveAsEvents {
        if (it is ErrorEvent.ErrorWithMessage) {
            onError(it.message)
        }
    }
    presetsViewModel.navigationEventFlow.ObserveAsEvents {
        if (it is NavigationEvent.NavigateToDrumPadScreen) {
            navigator.navigate(
                direction = DrumPadComposableDestination(
                    preset = it.preset
                )
            )
        }
    }
    ScrollableContainer(
        minHeight = 0.36.dw,
        maxHeight = 0.53.dw,
        topBar = { height, minHeight, maxHeight ->
            topBarHeight = height
            PresetsTopBar(
                title = name,
                subtitle = "Search for your favorite sound pack",
                height = height,
                minHeight = minHeight,
                maxHeight = maxHeight,
                leftIconResId = R.drawable.ic_arrow_left,
                text = presetsViewModel.searchTextState.value,
                onTextChange = {
                    presetsViewModel.changeText(it)
                },
                onClearText = {
                    presetsViewModel.changeText("")
                },
                onLeftButtonClicked = {
                    navigator.navigateUp()
                },
                onRightButtonClicked = { }
            )
        },
        contentHorizontalAlignment = Alignment.CenterHorizontally
    ) { _, topBarOffset ->
        item {
            Spacer(
                modifier = Modifier
                    .height(0.04.dw + topBarOffset)
            )
        }
        val list = presetsViewModel.filteredPresetsState.value
        val size = (list.size / 2.0).roundToInt()
        items(size) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PresetCard(
                    preset = list[it * 2],
                    titleTextSize = 0.045.sw,
                    subtitleTextSize = 0.03.sw,
                    coverSize = 0.41.dw,
                    onPresetClick = { x, y, preset ->
                        keyboardController?.hide()
                        fromX = x
                        fromY = y
                        isReversed = false
                        animationFlag = !(animationFlag ?: true)
                        clickedPreset = preset
                    }
                )
                if (it * 2 + 1 < list.size) {
                    PresetCard(
                        preset = list[it * 2 + 1],
                        titleTextSize = 0.045.sw,
                        subtitleTextSize = 0.03.sw,
                        coverSize = 0.41.dw,
                        onPresetClick = { x, y, preset ->
                            keyboardController?.hide()
                            fromX = x
                            fromY = y
                            isReversed = false
                            animationFlag = !(animationFlag ?: true)
                            clickedPreset = preset
                        }
                    )
                } else {
                    Spacer(
                        modifier = Modifier
                            .size(0.41.dw)
                    )
                }
            }
            Spacer(
                modifier = Modifier
                    .height(0.08.dw)
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        HomePresetDetails(
            boxScope = this,
            animatedValue = animatedValue,
            fromWidth = fromWidth,
            fromHeight = fromHeight,
            animatedWidth = animatedWidth,
            animatedHeight = animatedHeight,
            animatedX = animatedX,
            animatedY = animatedY,
            minHeight = 0.36.dw,
            clickedPreset = clickedPreset,
            onGloballyPositioned = { x, y ->
                toX = x
                toY = y
            },
            onGetPresetForFree = {
                presetsViewModel.getSoundForFree(it)
            },
            onGetAllPresets = {
                presetsViewModel.unlockAllSounds()
            },
            onCloseButtonClick = {
                isReversed = true
                animationFlag = !(animationFlag ?: true)
            }
        )

        presetsViewModel.noItemsState.value?.let {
            NoItems(
                boxScope = this,
                modifier = Modifier
                    .padding(
                        top = topBarHeight,
                        bottom = 0.075.dh
                    ),
                iconResId = it.iconResId,
                titleResId = R.string.no_items,
                subtitleResId = R.string.please_check_your_network
            )
        }
    }
}