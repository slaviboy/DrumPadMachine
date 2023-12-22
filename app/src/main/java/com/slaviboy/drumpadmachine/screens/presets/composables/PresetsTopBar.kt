package com.slaviboy.drumpadmachine.screens.presets.composables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.slaviboy.composeunits.DpToPx
import com.slaviboy.composeunits.dh
import com.slaviboy.composeunits.dw
import com.slaviboy.composeunits.sw
import com.slaviboy.drumpadmachine.composables.SearchTextField
import com.slaviboy.drumpadmachine.extensions.bounceClick
import com.slaviboy.drumpadmachine.ui.RobotoFont
import com.slaviboy.drumpadmachine.ui.backgroundGradientBottom
import com.slaviboy.drumpadmachine.ui.backgroundGradientTop

@Composable
fun PresetsTopBar(
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
            .padding(horizontal = 0.04.dw)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(
                    y = 0.07.dw
                ),
            horizontalArrangement = if (leftIconResId != null && rightIconResId != null) {
                Arrangement.SpaceBetween
            } else if (leftIconResId != null) {
                Arrangement.Start
            } else {
                Arrangement.End
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            leftIconResId?.let {
                Image(
                    modifier = Modifier
                        .width(0.08.dw)
                        .wrapContentHeight()
                        .clip(CircleShape)
                        .bounceClick {
                            onLeftButtonClicked()
                        },
                    contentScale = ContentScale.FillWidth,
                    painter = painterResource(id = it),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
            rightIconResId?.let {
                Image(
                    modifier = Modifier
                        .width(0.04.dw)
                        .wrapContentHeight()
                        .clip(CircleShape)
                        .alpha(fact)
                        .bounceClick {
                            onRightButtonClicked()
                        },
                    contentScale = ContentScale.FillWidth,
                    painter = painterResource(
                        id = it
                    ),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
        }
        Text(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(
                    y = 0.029.dh
                )
                .alpha(1f - fact.factMultiplyBy(2f)),
            text = title,
            fontSize = 0.075.sw * fontFact,
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
                fontSize = 0.075.sw,
                fontWeight = FontWeight.Bold,
                fontFamily = RobotoFont,
                color = Color.White
            )
        }
        val descriptionAlpha = fact.factMultiplyBy(2f)
        if (descriptionAlpha > 0) {
            Text(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(
                        y = 0.128.dh * fact.factMultiplyBy(0.72f)
                    )
                    .alpha(descriptionAlpha),
                text = subtitle,
                fontSize = 0.038.sw,
                fontWeight = FontWeight.Normal,
                fontFamily = RobotoFont,
                color = Color.LightGray
            )
        }
        SearchTextField(
            text = text,
            onTextChange = onTextChange,
            onClearText = onClearText,
            modifier = Modifier
                .fillMaxWidth()
                .height(0.122.dw)
                .align(Alignment.TopCenter)
                .offset(
                    y = 0.175.dh * fact.factMultiplyBy(0.5f)
                )
        )
    }
}

fun Float.factMultiplyBy(value: Float): Float {
    return 1f + (1f - this) * -(value)
}
