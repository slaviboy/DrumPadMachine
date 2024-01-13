package com.slaviboy.drumpadmachine.screens.home.composables

import androidx.compose.animation.core.TargetBasedAnimation
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.slaviboy.composeunits.dh
import com.slaviboy.composeunits.dw
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.api.results.Result
import com.slaviboy.drumpadmachine.composables.LoadingBox
import com.slaviboy.drumpadmachine.composables.NavigationMenu
import com.slaviboy.drumpadmachine.composables.NoItems
import com.slaviboy.drumpadmachine.composables.ScrollableContainer
import com.slaviboy.drumpadmachine.data.entities.Preset
import com.slaviboy.drumpadmachine.events.ErrorEvent
import com.slaviboy.drumpadmachine.events.NavigationEvent
import com.slaviboy.drumpadmachine.extensions.ObserveAsEvents
import com.slaviboy.drumpadmachine.extensions.mapValue
import com.slaviboy.drumpadmachine.screens.destinations.DrumPadComposableDestination
import com.slaviboy.drumpadmachine.screens.destinations.PresetsListComposableDestination
import com.slaviboy.drumpadmachine.screens.home.viewmodels.HomeViewModel

@OptIn(ExperimentalComposeUiApi::class)
@RootNavGraph(start = true)
@Destination
@Composable
fun HomeComposable(
    navigator: DestinationsNavigator,
    homeViewModel: HomeViewModel,
    onError: (error: String) -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current
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
    val filteredCategories = homeViewModel.filteredCategoriesMapState.value
    homeViewModel.errorEventFlow.ObserveAsEvents {
        if (it is ErrorEvent.ErrorWithMessage) {
            onError(it.message)
        }
    }
    homeViewModel.navigationEventFlow.ObserveAsEvents {
        if (it is NavigationEvent.NavigateToDrumPadScreen) {
            navigator.navigate(
                direction = DrumPadComposableDestination(it.preset)
            )
            homeViewModel.resetPresetIdState()
        }
    }
    var topBarHeight by remember {
        mutableStateOf(0.dw)
    }
    ScrollableContainer(
        minHeight = 0.165.dh,
        maxHeight = 0.35.dh,
        topBar = { height, minHeight, maxHeight ->
            topBarHeight = height - 0.055.dh
            HomeTopBar(
                title = stringResource(id = R.string.sound_packs).uppercase(),
                height = height,
                minHeight = minHeight,
                maxHeight = maxHeight,
                searchText = homeViewModel.searchTextState.value,
                onSearchTextChange = {
                    homeViewModel.changeText(it)
                },
                onClearSearchText = {
                    homeViewModel.changeText("")
                }
            )
        },
        contentHorizontalAlignment = Alignment.CenterHorizontally
    ) { _, topBarOffset ->
        item {
            Spacer(
                modifier = Modifier
                    .height(topBarOffset)
            )
        }
        items(filteredCategories.size) { i ->
            val categoryName = filteredCategories.keys.elementAt(i)
            HomePresetRow(
                lazyItemScope = this,
                categoryName = categoryName,
                presets = filteredCategories[categoryName],
                onPresetClick = { x, y, preset ->
                    keyboardController?.hide()
                    fromX = x
                    fromY = y
                    isReversed = false
                    animationFlag = !(animationFlag ?: true)
                    clickedPreset = preset
                    homeViewModel.setInitPresetStatus()
                },
                onSeeAllClick = {
                    keyboardController?.hide()
                    navigator.navigate(
                        direction = PresetsListComposableDestination(
                            name = categoryName,
                            presets = filteredCategories[it]?.toTypedArray() ?: arrayOf()
                        )
                    )
                }
            )
        }
        item {
            Spacer(
                modifier = Modifier
                    .height(0.075.dh)
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
            clickedPreset = clickedPreset,
            isLoading = (homeViewModel.presetIdState.value is Result.Loading),
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
                homeViewModel.cancelDownload()
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
                        top = topBarHeight,
                        bottom = 0.075.dh
                    )
            )
        }
        homeViewModel.noItemsState.value?.let {
            NoItems(
                boxScope = this,
                modifier = Modifier
                    .padding(
                        top = topBarHeight,
                        bottom = 0.075.dh
                    ),
                iconResId = it.iconResId,
                titleResId = it.titleResId,
                subtitleResId = it.subtitleResId
            )
        }
    }
}