package com.slaviboy.drumpadmachine.screens.settings.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.slaviboy.composeunits.DeviceHeight
import com.slaviboy.composeunits.DeviceWidth
import com.slaviboy.composeunits.Density
import com.slaviboy.composeunits.ScaleDensity
import com.slaviboy.composeunits.dw
import com.slaviboy.composeunits.sw
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.ui.RobotoFont

@Composable
private fun AppIconGrid() {
    Column(
        modifier = Modifier
            .size(0.1.dw)
            .clip(RoundedCornerShape(0.025.dw))
            .background(Color.White.copy(alpha = 0.06f))
            .padding(0.002.dw),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Image(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .scale(1.9f),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun SettingsAppInfoRow(appVersion: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.04.dw, vertical = 0.025.dw),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppIconGrid()
        Spacer(modifier = Modifier.width(0.03.dw))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(id = R.string.app_display_name),
                color = Color.White,
                fontFamily = RobotoFont,
                fontSize = 0.04.sw,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(id = R.string.app_version, appVersion),
                color = Color.LightGray,
                fontFamily = RobotoFont,
                fontSize = 0.032.sw,
                fontWeight = FontWeight.Normal
            )
            Text(
                text = stringResource(id = R.string.app_tagline),
                color = Color.Gray,
                fontFamily = RobotoFont,
                fontSize = 0.032.sw,
                fontWeight = FontWeight.Normal
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
private fun SettingsAppInfoRowPreview() {
    DeviceWidth = 1080f
    DeviceHeight = 2400f
    Density = 3f
    ScaleDensity = 3f
    SettingsAppInfoRow(appVersion = "1.0.0-dev")
}
