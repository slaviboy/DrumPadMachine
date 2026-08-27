package com.slaviboy.drumpadmachine.screens.settings.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
fun ResetSettingsButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.045.dw)
            .clip(RoundedCornerShape(0.035.dw))
            .background(dangerColor.copy(alpha = 0.08f))
            .border(
                width = 1.dp,
                color = dangerColor.copy(alpha = 0.4f),
                shape = RoundedCornerShape(0.035.dw)
            )
            .bounceClick(onClick = onClick)
            .padding(vertical = 0.03.dw),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Restore,
            contentDescription = null,
            tint = dangerColor,
            modifier = Modifier.size(0.045.dw)
        )
        Spacer(modifier = Modifier.width(0.02.dw))
        Text(
            text = stringResource(id = R.string.reset_to_defaults),
            color = dangerColor,
            fontFamily = RobotoFont,
            fontSize = 0.04.sw,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF232339)
@Composable
private fun ResetSettingsButtonPreview() {
    DeviceWidth = 1080f
    DeviceHeight = 2400f
    Density = 3f
    ScaleDensity = 3f
    ResetSettingsButton(onClick = {})
}
