package com.slaviboy.drumpadmachine.screens.home.composables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.slaviboy.composeunits.DpToPx
import com.slaviboy.composeunits.dh
import com.slaviboy.composeunits.dw
import com.slaviboy.composeunits.sw
import com.slaviboy.drumpadmachine.composables.SearchTextField
import com.slaviboy.drumpadmachine.screens.presets.composables.factMultiplyBy
import com.slaviboy.drumpadmachine.ui.RobotoFont
import com.slaviboy.drumpadmachine.ui.backgroundGradientBottom
import com.slaviboy.drumpadmachine.ui.backgroundGradientTop

@Composable
fun HomeTopBar(
    height: Dp,
    minHeight: Dp? = null,
    maxHeight: Dp? = null,
    title: String,
    subtitle: String,
    @DrawableRes leftIconResId: Int? = null,
    @DrawableRes rightIconResId: Int? = null,
    text: String,
    onTextChange: (text: String) -> Unit = {},
    onClearText: () -> Unit = {},
    onLeftButtonClicked: () -> Unit = {},
    onRightButtonClicked: () -> Unit = {}
) {
    val fact = if (minHeight != null && maxHeight != null) {
        (height - minHeight) / (maxHeight - minHeight)
    } else {
        1f
    }
    val fontFact = Math.max(0.8f, fact)
    val heightPx = with(LocalDensity.current) { height.toPx() }
    val cornerRadius = 0.02.dw * fact
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
    ) {
        TopBox(
            modifier = Modifier
                .alpha(fact.factMultiplyBy(2f))
        )
        Text(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(
                    y = 0.029.dh
                )
                .alpha(1f - fact),
            text = title,
            fontSize = 0.075.sw * fontFact,
            fontWeight = FontWeight.Bold,
            fontFamily = RobotoFont,
            color = Color.White
        )
        SearchTextField(
            text = text,
            onTextChange = onTextChange,
            onClearText = onClearText,
            modifier = Modifier
                .fillMaxWidth()
                .height(0.055.dh)
                .align(Alignment.TopCenter)
                .offset(
                    y = 0.275.dh * fact.factMultiplyBy(0.7f)
                )
                .padding(horizontal = 0.04.dw)
        )
    }
}