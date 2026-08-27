package com.slaviboy.drumpadmachine.screens.settings.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.slaviboy.composeunits.DeviceHeight
import com.slaviboy.composeunits.DeviceWidth
import com.slaviboy.composeunits.Density
import com.slaviboy.composeunits.ScaleDensity
import com.slaviboy.composeunits.dw
import com.slaviboy.composeunits.sw
import com.slaviboy.drumpadmachine.ui.RobotoFont

@Composable
fun SettingsSectionHeader(text: String) {
    Text(
        text = text.uppercase(),
        color = Color.White,
        fontFamily = RobotoFont,
        fontSize = 0.032.sw,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .padding(horizontal = 0.06.dw, vertical = 0.015.dw)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF232339)
@Composable
private fun SettingsSectionHeaderPreview() {
    DeviceWidth = 1080f
    DeviceHeight = 2400f
    Density = 3f
    ScaleDensity = 3f
    SettingsSectionHeader(text = "Audio")
}
