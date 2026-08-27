package com.slaviboy.drumpadmachine.screens.settings.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.slaviboy.composeunits.DeviceHeight
import com.slaviboy.composeunits.DeviceWidth
import com.slaviboy.composeunits.Density
import com.slaviboy.composeunits.ScaleDensity
import com.slaviboy.composeunits.dw
import com.slaviboy.composeunits.sw
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.ui.RobotoFont
import kotlin.math.roundToInt

@Composable
fun SettingsSliderRow(
    icon: ImageVector,
    iconTint: Color,
    titleResId: Int,
    value: Int,
    valueRange: ClosedFloatingPointRange<Float>,
    valueText: String,
    onValueChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.04.dw, vertical = 0.015.dw),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingsIconBadge(tint = iconTint) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(0.05.dw))
        }
        Spacer(modifier = Modifier.width(0.03.dw))
        Text(
            text = stringResource(id = titleResId),
            color = Color.White,
            fontFamily = RobotoFont,
            fontSize = 0.036.sw,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(0.24.dw)
        )
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.roundToInt()) },
            valueRange = valueRange,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(
                thumbColor = sliderAccentColor,
                activeTrackColor = sliderAccentColor
            )
        )
        Spacer(modifier = Modifier.width(0.02.dw))
        Text(
            text = valueText,
            color = Color.LightGray,
            fontFamily = RobotoFont,
            fontSize = 0.032.sw,
            textAlign = TextAlign.End,
            modifier = Modifier.width(0.13.dw)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF232339)
@Composable
private fun SettingsSliderRowPreview() {
    DeviceWidth = 1080f
    DeviceHeight = 2400f
    Density = 3f
    ScaleDensity = 3f
    var value by remember { mutableIntStateOf(100) }
    SettingsSliderRow(
        icon = Icons.Filled.VolumeUp,
        iconTint = sliderAccentColor,
        titleResId = R.string.volume,
        value = value,
        valueRange = 0f..150f,
        valueText = "$value%",
        onValueChange = { value = it }
    )
}
