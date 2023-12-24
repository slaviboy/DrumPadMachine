package com.slaviboy.drumpadmachine.screens.drumpad.composables

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.bumptech.glide.integration.compose.CrossFade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.slaviboy.composeunits.dw
import com.slaviboy.composeunits.sw
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.composables.ImageButtonWithText
import com.slaviboy.drumpadmachine.data.entities.Preset
import com.slaviboy.drumpadmachine.extensions.bounceClick
import com.slaviboy.drumpadmachine.modules.NetworkModule
import com.slaviboy.drumpadmachine.screens.destinations.LessonsListComposableDestination
import com.slaviboy.drumpadmachine.screens.drumpad.viewmodels.DrumPadViewModel
import com.slaviboy.drumpadmachine.ui.RobotoFont
import com.slaviboy.drumpadmachine.ui.backgroundGradientBottom
import com.slaviboy.drumpadmachine.ui.backgroundGradientTop

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalComposeUiApi::class)
@RootNavGraph(start = false)
@Destination
@Composable
fun DrumPadComposable(
    navigator: DestinationsNavigator,
    drumPadViewModel: DrumPadViewModel,
    preset: Preset
) {
    val uriHandler = LocalUriHandler.current
    LaunchedEffect(preset) {
        drumPadViewModel.loadSounds(preset)
    }
    BackHandler {
        drumPadViewModel.terminate()
        navigator.navigateUp()
    }
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
            Spacer(
                modifier = Modifier
                    .height(0.07.dw)
            )
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_left),
                contentDescription = null,
                modifier = Modifier
                    .size(0.07.dw)
                    .offset(x = 0.04.dw)
                    .bounceClick {
                        drumPadViewModel.terminate()
                        navigator.navigateUp()
                    },
                colorFilter = ColorFilter.tint(Color.White)
            )
            Spacer(
                modifier = Modifier
                    .height(0.05.dw)
            )
            Row(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlideImage(
                    model = NetworkModule.coverIconUrl(preset.id),
                    contentDescription = null,
                    modifier = Modifier
                        .size(0.14.dw)
                        .clip(RoundedCornerShape(0.02.dw)),
                    transition = CrossFade,
                    failure = placeholder(R.drawable.ic_no_image),
                    loading = placeholder(R.drawable.ic_default_image)
                )
                Spacer(
                    modifier = Modifier
                        .width(0.02.dw)
                )
                Column {
                    Text(
                        text = preset.name,
                        color = Color.White,
                        fontFamily = RobotoFont,
                        fontSize = 0.063.sw,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(horizontal = 0.01.dw),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = preset.author ?: "",
                        color = Color.LightGray,
                        fontFamily = RobotoFont,
                        fontSize = 0.032.sw,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier
                            .padding(horizontal = 0.01.dw),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(
                    modifier = Modifier
                        .width(0.04.dw)
                )
            }
            Spacer(
                modifier = Modifier
                    .height(0.07.dw)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ImageButtonWithText(
                    iconResId = R.drawable.ic_metronome,
                    textResId = R.string.tempo,
                    onClick = {

                    }
                )
                ImageButtonWithText(
                    iconResId = R.drawable.ic_record,
                    textResId = R.string.record,
                    onClick = {

                    }
                )
                ImageButtonWithText(
                    iconResId = if (drumPadViewModel.page.value == 0) {
                        R.drawable.ic_side_a
                    } else {
                        R.drawable.ic_side_b
                    },
                    textResId = R.string.side,
                    onClick = {
                        drumPadViewModel.movePage()
                    }
                )
                ImageButtonWithText(
                    iconResId = R.drawable.ic_lessons,
                    textResId = R.string.lessons,
                    onClick = {
                        navigator.navigate(
                            direction = LessonsListComposableDestination(
                                //preset = preset
                            )
                        )
                    }
                )
            }
            Spacer(
                modifier = Modifier
                    .height(0.08.dw)
            )
            Column(
                modifier = Modifier
                    .pointerInteropFilter {
                        drumPadViewModel.onContainerTouch(it)
                        true
                    }
                    .onGloballyPositioned {
                        drumPadViewModel.setContainerBound(it.boundsInRoot())
                    }
            ) {
                for (i in 0 until drumPadViewModel.numberOfRows) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 0.02.dw),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        for (j in 0 until drumPadViewModel.numberOfColumns) {
                            PadComposable(
                                padColor = drumPadViewModel.getPadColor(
                                    row = i,
                                    column = j
                                ),
                                showGlow = drumPadViewModel.getShowGlow(
                                    row = i,
                                    column = j
                                ),
                                modifier = Modifier
                                    .weight(1f),
                                onPositionInParentChange = {
                                    drumPadViewModel.onPositionInParentChange(
                                        rect = it,
                                        row = i,
                                        column = j
                                    )
                                }
                            )
                            if (j < drumPadViewModel.numberOfColumns - 1) {
                                Spacer(
                                    modifier = Modifier
                                        .width(0.01.dw)
                                )
                            }
                        }
                    }
                    if (i < drumPadViewModel.numberOfRows - 1) {
                        Spacer(
                            modifier = Modifier
                                .height(0.01.dw)
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .wrapContentHeight()
                    .weight(1f)
                    .align(Alignment.CenterHorizontally)
                    .bounceClick {
                        uriHandler.openUri("https://github.com/slaviboy")
                    }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_github),
                    contentDescription = null,
                    modifier = Modifier
                        .size(0.1.dw),
                    colorFilter = ColorFilter.tint(Color.White)
                )
                Spacer(
                    modifier = Modifier
                        .width(0.01.dw)
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.rected_by),
                        color = Color.Gray,
                        fontFamily = RobotoFont,
                        fontSize = 0.028.sw,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier
                            .padding(horizontal = 0.01.dw)
                    )
                    Text(
                        text = "Slaviboy",
                        color = Color.White,
                        fontFamily = RobotoFont,
                        fontSize = 0.038.sw,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(horizontal = 0.01.dw)
                    )
                }
            }
        }
    }
}