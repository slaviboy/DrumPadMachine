package com.slaviboy.drumpadmachine.screens.settings.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.slaviboy.composeunits.DeviceHeight
import com.slaviboy.composeunits.DeviceWidth
import com.slaviboy.composeunits.Density
import com.slaviboy.composeunits.ScaleDensity
import com.slaviboy.composeunits.dw
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.composables.NumberStepper

@Composable
fun SettingsStepperRow(
    icon: ImageVector,
    iconTint: Color,
    titleResId: Int,
    subtitleResId: Int,
    value: Int,
    range: IntRange,
    step: Int,
    onValueChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.04.dw, vertical = 0.02.dw),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingsIconBadge(tint = iconTint) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(0.05.dw))
        }
        Spacer(modifier = Modifier.width(0.03.dw))
        Box(modifier = Modifier.weight(1f)) {
            SettingsRowTexts(titleResId = titleResId, subtitleResId = subtitleResId)
        }
        NumberStepper(
            value = value,
            range = range,
            step = step,
            accentColor = sliderAccentColor,
            onValueChange = onValueChange
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF232339)
@Composable
private fun SettingsStepperRowPreview() {
    DeviceWidth = 1080f
    DeviceHeight = 2400f
    Density = 3f
    ScaleDensity = 3f
    var value by remember { mutableIntStateOf(108) }
    SettingsStepperRow(
        icon = Icons.Filled.Timer,
        iconTint = badgeBpm,
        titleResId = R.string.default_bpm,
        subtitleResId = R.string.default_bpm_subtitle,
        value = value,
        range = 40..240,
        step = 1,
        onValueChange = { value = it }
    )
}
