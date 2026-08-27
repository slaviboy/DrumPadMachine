package com.slaviboy.drumpadmachine.screens.settings.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.slaviboy.composeunits.DeviceHeight
import com.slaviboy.composeunits.DeviceWidth
import com.slaviboy.composeunits.Density
import com.slaviboy.composeunits.DpToPx
import com.slaviboy.composeunits.ScaleDensity
import com.slaviboy.composeunits.dh
import com.slaviboy.composeunits.dw
import com.slaviboy.composeunits.sw
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.extensions.bounceClick
import com.slaviboy.drumpadmachine.extensions.factMultiplyBy
import com.slaviboy.drumpadmachine.ui.RobotoFont
import com.slaviboy.drumpadmachine.ui.backgroundGradientBottom
import com.slaviboy.drumpadmachine.ui.backgroundGradientTop

@Composable
fun SettingsTopBar(
    height: Dp,
    minHeight: Dp? = null,
    maxHeight: Dp? = null,
    title: String,
    onLeftButtonClicked: () -> Unit = {}
) {
    val fact = if (minHeight != null && maxHeight != null) {
        (height - minHeight) / (maxHeight - minHeight)
    } else {
        1f
    }
    val fontFact = Math.max(0.8f, fact)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        backgroundGradientTop,
                        backgroundGradientBottom
                    ),
                    endY = 1.dh.value.DpToPx
                )
            )
            .padding(horizontal = 0.04.dw)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_arrow_left),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(y = 0.07.dw)
                .width(0.08.dw)
                .wrapContentHeight()
                .clip(CircleShape)
                .bounceClick {
                    onLeftButtonClicked()
                },
            contentScale = ContentScale.FillWidth,
            colorFilter = ColorFilter.tint(Color.White)
        )
        Text(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(
                    y = 0.029.dh
                )
                .alpha(1f - fact.factMultiplyBy(2f)),
            text = title,
            fontSize = 0.08.sw * fontFact,
            fontWeight = FontWeight.Bold,
            fontFamily = RobotoFont,
            color = Color.White
        )
        val titleAlpha = fact.factMultiplyBy(2.5f)
        if (titleAlpha > 0) {
            Text(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(
                        y = 0.08.dh * fact.factMultiplyBy(0.62f)
                    )
                    .alpha(titleAlpha),
                text = title,
                fontSize = 0.08.sw,
                fontWeight = FontWeight.Bold,
                fontFamily = RobotoFont,
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF232339)
@Composable
private fun SettingsTopBarExpandedPreview() {
    DeviceWidth = 1080f
    DeviceHeight = 2400f
    Density = 3f
    ScaleDensity = 3f
    SettingsTopBar(
        height = 0.32.dw,
        minHeight = 0.2.dw,
        maxHeight = 0.32.dw,
        title = "Settings"
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF232339)
@Composable
private fun SettingsTopBarCollapsedPreview() {
    DeviceWidth = 1080f
    DeviceHeight = 2400f
    Density = 3f
    ScaleDensity = 3f
    SettingsTopBar(
        height = 0.2.dw,
        minHeight = 0.2.dw,
        maxHeight = 0.32.dw,
        title = "Settings"
    )
}
