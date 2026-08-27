package com.slaviboy.drumpadmachine.screens.settings.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.slaviboy.composeunits.sw
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.extensions.bounceClick
import com.slaviboy.drumpadmachine.ui.RobotoFont

@Composable
fun SettingsActionRow(
    icon: ImageVector,
    iconTint: Color,
    titleResId: Int,
    subtitleResId: Int,
    trailingText: String? = null,
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .let { if (enabled) it.bounceClick(onClick = onClick) else it }
            .padding(horizontal = 0.04.dw, vertical = 0.02.dw),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingsIconBadge(tint = if (enabled) iconTint else Color.Gray) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (enabled) iconTint else Color.Gray,
                modifier = Modifier.size(0.05.dw)
            )
        }
        Spacer(modifier = Modifier.width(0.03.dw))
        Box(modifier = Modifier.weight(1f)) {
            SettingsRowTexts(titleResId = titleResId, subtitleResId = subtitleResId, enabled = enabled)
        }
        trailingText?.let {
            Text(
                text = it,
                color = if (enabled) sliderAccentColor else Color.Gray,
                fontFamily = RobotoFont,
                fontSize = 0.034.sw,
                modifier = Modifier.padding(end = 0.01.dw)
            )
        }
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(0.055.dw)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF232339)
@Composable
private fun SettingsActionRowPreview() {
    DeviceWidth = 1080f
    DeviceHeight = 2400f
    Density = 3f
    ScaleDensity = 3f
    SettingsActionRow(
        icon = Icons.Filled.Delete,
        iconTint = badgeCache,
        titleResId = R.string.clear_cache,
        subtitleResId = R.string.clear_cache_subtitle,
        trailingText = "61.2 MB"
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF232339)
@Composable
private fun SettingsActionRowDisabledPreview() {
    DeviceWidth = 1080f
    DeviceHeight = 2400f
    Density = 3f
    ScaleDensity = 3f
    SettingsActionRow(
        icon = Icons.Filled.Delete,
        iconTint = badgeCache,
        titleResId = R.string.backup_restore,
        subtitleResId = R.string.backup_restore_subtitle,
        enabled = false
    )
}
