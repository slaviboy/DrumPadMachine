package com.slaviboy.drumpadmachine.screens.settings.composables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.slaviboy.composeunits.DeviceHeight
import com.slaviboy.composeunits.DeviceWidth
import com.slaviboy.composeunits.Density
import com.slaviboy.composeunits.ScaleDensity
import com.slaviboy.composeunits.dw
import com.slaviboy.drumpadmachine.R

@Composable
fun SettingsToggleRow(
    icon: ImageVector,
    iconTint: Color,
    titleResId: Int,
    subtitleResId: Int,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
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
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = sliderAccentColor,
                checkedTrackColor = sliderAccentColor.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
fun SettingsToggleRow(
    @DrawableRes iconResId: Int,
    iconTint: Color,
    titleResId: Int,
    subtitleResId: Int,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.04.dw, vertical = 0.02.dw),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingsIconBadge(tint = iconTint) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                modifier = Modifier.size(0.05.dw),
                colorFilter = ColorFilter.tint(iconTint)
            )
        }
        Spacer(modifier = Modifier.width(0.03.dw))
        Box(modifier = Modifier.weight(1f)) {
            SettingsRowTexts(titleResId = titleResId, subtitleResId = subtitleResId)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = sliderAccentColor,
                checkedTrackColor = sliderAccentColor.copy(alpha = 0.5f)
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF232339)
@Composable
private fun SettingsToggleRowPreview() {
    DeviceWidth = 1080f
    DeviceHeight = 2400f
    Density = 3f
    ScaleDensity = 3f
    var checked by remember { mutableStateOf(true) }
    SettingsToggleRow(
        icon = Icons.Filled.Vibration,
        iconTint = badgeHaptic,
        titleResId = R.string.haptic_feedback,
        subtitleResId = R.string.haptic_feedback_subtitle,
        checked = checked,
        onCheckedChange = { checked = it }
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF232339)
@Composable
private fun SettingsToggleRowDrawableIconPreview() {
    DeviceWidth = 1080f
    DeviceHeight = 2400f
    Density = 3f
    ScaleDensity = 3f
    var checked by remember { mutableStateOf(false) }
    SettingsToggleRow(
        iconResId = R.drawable.ic_metronome,
        iconTint = badgeMetronome,
        titleResId = R.string.metronome,
        subtitleResId = R.string.metronome_subtitle,
        checked = checked,
        onCheckedChange = { checked = it }
    )
}
