package com.slaviboy.drumpadmachine.screens.settings.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.slaviboy.composeunits.DeviceHeight
import com.slaviboy.composeunits.DeviceWidth
import com.slaviboy.composeunits.Density
import com.slaviboy.composeunits.ScaleDensity
import com.slaviboy.composeunits.sw
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.ui.RobotoFont

@Composable
fun SettingsRowTexts(titleResId: Int, subtitleResId: Int, enabled: Boolean = true) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = titleResId),
            color = if (enabled) Color.White else Color.Gray,
            fontFamily = RobotoFont,
            fontSize = 0.04.sw,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(id = subtitleResId),
            color = Color.Gray,
            fontFamily = RobotoFont,
            fontSize = 0.032.sw,
            fontWeight = FontWeight.Normal
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF232339)
@Composable
private fun SettingsRowTextsPreview() {
    DeviceWidth = 1080f
    DeviceHeight = 2400f
    Density = 3f
    ScaleDensity = 3f
    SettingsRowTexts(
        titleResId = R.string.haptic_feedback,
        subtitleResId = R.string.haptic_feedback_subtitle
    )
}
