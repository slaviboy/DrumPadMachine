package com.slaviboy.drumpadmachine.screens.home.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.slaviboy.composeunits.DeviceHeight
import com.slaviboy.composeunits.DeviceWidth
import com.slaviboy.composeunits.Density
import com.slaviboy.composeunits.ScaleDensity
import com.slaviboy.composeunits.dh
import com.slaviboy.composeunits.dw
import com.slaviboy.composeunits.sw
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.extensions.bounceClick
import com.slaviboy.drumpadmachine.ui.RobotoFont

/** Shared tilt for the cascading cover cluster and the decorative accent rectangles. */
private const val TILT_ANGLE = -16f

@Composable
fun TopBox(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(0.25.dh)
            .background(
                brush = Brush.horizontalGradient(
                    listOf(
                        Color(0xFF274973),
                        Color(0xFFE361F9)
                    )
                )
            )
    ) {
        // Decorative tilted accent rectangles, same angle as the covers
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 0.06.dw, y = 0.145.dh)
                .size(0.05.dw)
                .rotate(TILT_ANGLE)
                .clip(RoundedCornerShape(0.006.dw))
                .background(Color(0xFFFF8A3D))
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 0.155.dw, y = 0.165.dh)
                .size(0.06.dw)
                .rotate(TILT_ANGLE)
                .clip(RoundedCornerShape(0.006.dw))
                .background(Color(0xFF9B5CFF))
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 0.27.dw, y = 0.14.dh)
                .size(0.04.dw)
                .rotate(TILT_ANGLE)
                .clip(RoundedCornerShape(0.006.dw))
                .background(Color(0xFF34E4E0))
        )

        // Cascading, tilted sound-pack covers fanning off the top-right corner
        PromoCover(
            drawableRes = R.drawable.cover_acoustic_dreams,
            size = 0.155.dw,
            x = 0.30.dw,
            y = -0.065.dh
        )
        PromoCover(
            drawableRes = R.drawable.cover_horns_of_jericho,
            size = 0.185.dw,
            x = 0.37.dw,
            y = 0.105.dh
        )
        PromoCover(
            drawableRes = R.drawable.cover_could_this_be_love,
            size = 0.165.dw,
            x = 0.76.dw,
            y = 0.115.dh
        )
        PromoCover(
            drawableRes = R.drawable.cover_saxo_bass,
            size = 0.205.dw,
            x = 0.60.dw,
            y = -0.015.dh
        )
        PromoCover(
            drawableRes = R.drawable.cover_inspiring_folk,
            size = 0.175.dw,
            x = 0.91.dw,
            y = -0.005.dh
        )
        PromoCover(
            drawableRes = R.drawable.cover_zombo_step,
            size = 0.285.dw,
            x = 0.45.dw,
            y = -0.14.dh
        )

        Text(
            text = "Unlock\nall sounds".uppercase(),
            color = Color.White,
            fontFamily = RobotoFont,
            fontSize = 0.06.sw,
            fontWeight = FontWeight.Black,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = 0.04.dw)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = -(0.04).dw, y = -(0.04).dw)
                .bounceClick {

                }
                .background(Color(0xFFFFD112), CircleShape)
                .padding(horizontal = 0.07.dw, vertical = 0.03.dw),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Upgrade".uppercase(),
                color = Color(0xFF21212B),
                fontFamily = RobotoFont,
                fontSize = 0.034.sw,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF232339)
@Composable
private fun TopBoxPreview() {
    DeviceWidth = 1080f
    DeviceHeight = 2400f
    Density = 3f
    ScaleDensity = 3f
    TopBox()
}

@Composable
private fun PromoCover(
    drawableRes: Int,
    size: Dp,
    x: Dp,
    y: Dp
) {
    Box(
        modifier = Modifier
            .offset(x = x, y = y)
            .size(size)
    ) {
        // Soft drop shadow beneath the tilted cover, offset slightly down-right
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = -0.007.dw, y = 0.014.dw)
                .rotate(TILT_ANGLE)
                .clip(RoundedCornerShape(0.02.dw))
                .background(Color.Black.copy(alpha = 0.2f))
        )
        Image(
            painter = painterResource(id = drawableRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .matchParentSize()
                .rotate(TILT_ANGLE)
                .clip(RoundedCornerShape(0.02.dw))
        )
    }
}
