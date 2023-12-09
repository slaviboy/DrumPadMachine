package com.slaviboy.drumpadmachine.screens.home.composables

import androidx.compose.animation.core.TargetBasedAnimation
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.slaviboy.composeunits.dh
import com.slaviboy.composeunits.dw
import com.slaviboy.drumpadmachine.api.results.Result
import com.slaviboy.drumpadmachine.composables.LoadingBox
import com.slaviboy.drumpadmachine.composables.NavigationMenu
import com.slaviboy.drumpadmachine.composables.NoItems
import com.slaviboy.drumpadmachine.composables.SearchTextField
import com.slaviboy.drumpadmachine.data.entities.Preset
import com.slaviboy.drumpadmachine.events.ErrorEvent
import com.slaviboy.drumpadmachine.events.NavigationEvent
import com.slaviboy.drumpadmachine.extensions.ObserveAsEvents
import com.slaviboy.drumpadmachine.extensions.mapValue
import com.slaviboy.drumpadmachine.screens.destinations.DrumPadComposableDestination
import com.slaviboy.drumpadmachine.screens.home.viewmodels.HomeViewModel
import com.slaviboy.drumpadmachine.ui.backgroundGradientBottom
import com.slaviboy.drumpadmachine.ui.backgroundGradientTop

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
        val categoryMaps = homeViewModel.filteredCategoriesMapState.value
        homeViewModel.errorEventFlow.ObserveAsEvents {
            if (it is ErrorEvent.ErrorWithMessage) {
                onError(it.message)
            }
        }
        homeViewModel.navigationEventFlow.ObserveAsEvents {
            if (it is NavigationEvent.NavigateToDrumPadScreen) {
                navigator.navigate(
                    direction = DrumPadComposableDestination(it.presetId)
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
                        .height(0.05.dw)
                )
                SearchTextField(
                    text = homeViewModel.searchTextState.value,
                    onTextChange = {
                        homeViewModel.changeText(it)
                    },
                    onSearchButtonClick = {
                        homeViewModel.search()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 0.04.dw)
                )
                Spacer(
                    modifier = Modifier
                        .height(0.04.dw)
                )
            }
            items(categoryMaps.size) { i ->
                val categoryName = categoryMaps.keys.elementAt(i)
                HomePresetRow(
                    lazyItemScope = this,
                    categoryName = categoryName,
                    presets = categoryMaps[categoryName],
                    onClick = { x, y, preset ->
                        fromX = x
                        fromY = y
                        isReversed = false
                        animationFlag = !(animationFlag ?: true)
                        clickedPreset = preset
                    }
                )
            }
        }
        HomePresetDetails(
            boxScope = this,
            animatedValue = animatedValue,
            fromWidth = fromWidth,
            fromHeight = fromHeight,
            animatedWidth = animatedWidth,
            animatedHeight = animatedHeight,
            animatedX = animatedX,
            animatedY = animatedY,
            clickedPreset = clickedPreset,
            onGloballyPositioned = { x, y ->
                toX = x
                toY = y
            },
            onGetPresetForFree = {
                homeViewModel.getSoundForFree(it)
            },
            onGetAllPresets = {
                homeViewModel.unlockAllSounds()
            },
            onCloseButtonClick = {
                isReversed = true
                animationFlag = !(animationFlag ?: true)
            }
        )
        NavigationMenu(
            boxScope = this,
            menuItems = homeViewModel.menuItemsState.value,
            onItemClick = {
                homeViewModel.setMenuItem(it)
            }
        )
        if (homeViewModel.audioConfigState.value is Result.Loading) {
            LoadingBox(
                boxScope = this,
                modifier = Modifier
                    .padding(
                        top = 0.25.dh,
                        bottom = 0.18.dw
                    )
            )
        }
        if (homeViewModel.audioConfigState.value is Result.Error && categoryMaps.isEmpty()) {
            NoItems(
                boxScope = this,
                modifier = Modifier
                    .padding(
                        top = 0.25.dh,
                        bottom = 0.18.dw
                    )
            )
        }
    }
}