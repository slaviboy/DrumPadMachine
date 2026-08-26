package com.slaviboy.drumpadmachine.screens.settings.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.slaviboy.composeunits.dw
import com.slaviboy.composeunits.sw
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.extensions.bounceClick
import com.slaviboy.drumpadmachine.screens.settings.viewmodels.SettingsViewModel
import com.slaviboy.drumpadmachine.ui.RobotoFont
import com.slaviboy.drumpadmachine.ui.backgroundGradientBottom
import com.slaviboy.drumpadmachine.ui.backgroundGradientTop
import kotlin.math.roundToInt

private val sliderAccentColor = Color(0xFFffd112)

@RootNavGraph(start = false)
@Destination
@Composable
fun SettingsComposable(
    navigator: DestinationsNavigator,
    settingsViewModel: SettingsViewModel
) {
    val uriHandler = LocalUriHandler.current
    Column(
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
                    navigator.navigateUp()
                },
            colorFilter = ColorFilter.tint(Color.White)
        )
        Spacer(
            modifier = Modifier
                .height(0.05.dw)
        )
        Text(
            text = stringResource(id = R.string.settings),
            color = Color.White,
            fontFamily = RobotoFont,
            fontSize = 0.063.sw,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(horizontal = 0.06.dw)
        )
        Spacer(
            modifier = Modifier
                .height(0.06.dw)
        )
        SettingsSlider(
            labelResId = R.string.pan,
            value = settingsViewModel.pan.value,
            valueRange = -100f..100f,
            onValueChange = { settingsViewModel.setPan(it) }
        )
        SettingsSlider(
            labelResId = R.string.reverb,
            value = settingsViewModel.reverb.value,
            valueRange = 0f..100f,
            onValueChange = { settingsViewModel.setReverb(it) }
        )
        SettingsSlider(
            labelResId = R.string.volume,
            value = settingsViewModel.volume.value,
            valueRange = 0f..150f,
            onValueChange = { settingsViewModel.setVolume(it) }
        )
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
                    text = stringResource(id = R.string.recreated_by),
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

@Composable
private fun SettingsSlider(
    labelResId: Int,
    value: Int,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.06.dw)
    ) {
        Text(
            text = "${stringResource(id = labelResId)}: $value",
            color = Color.White,
            fontFamily = RobotoFont,
            fontSize = 0.04.sw,
            fontWeight = FontWeight.Normal
        )
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.roundToInt()) },
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = sliderAccentColor,
                activeTrackColor = sliderAccentColor
            )
        )
    }
}
