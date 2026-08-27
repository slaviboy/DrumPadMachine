package com.slaviboy.drumpadmachine.screens.settings.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.slaviboy.composeunits.DeviceHeight
import com.slaviboy.composeunits.DeviceWidth
import com.slaviboy.composeunits.Density
import com.slaviboy.composeunits.ScaleDensity
import com.slaviboy.composeunits.dw

@Composable
fun SettingsIconBadge(tint: Color, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(0.1.dw)
            .clip(RoundedCornerShape(0.025.dw))
            .background(tint.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF232339)
@Composable
private fun SettingsIconBadgePreview() {
    DeviceWidth = 1080f
    DeviceHeight = 2400f
    Density = 3f
    ScaleDensity = 3f
    SettingsIconBadge(tint = badgeHaptic) {
        Icon(imageVector = Icons.Filled.Vibration, contentDescription = null, tint = badgeHaptic, modifier = Modifier.size(0.05.dw))
    }
}
