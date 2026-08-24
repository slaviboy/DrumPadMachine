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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
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
            size = 0.16.dw,
            x = 0.34.dw,
            y = -0.075.dh
        )
        PromoCover(
            drawableRes = R.drawable.cover_horns_of_jericho,
            size = 0.18.dw,
            x = 0.40.dw,
            y = 0.095.dh
        )
        PromoCover(
            drawableRes = R.drawable.cover_could_this_be_love,
            size = 0.17.dw,
            x = 0.80.dw,
            y = 0.11.dh
        )
        PromoCover(
            drawableRes = R.drawable.cover_saxo_bass,
            size = 0.19.dw,
            x = 0.64.dw,
            y = -0.02.dh
        )
        PromoCover(
            drawableRes = R.drawable.cover_inspiring_folk,
            size = 0.18.dw,
            x = 0.95.dw,
            y = -0.01.dh
        )
        PromoCover(
            drawableRes = R.drawable.cover_zombo_step,
            size = 0.27.dw,
            x = 0.48.dw,
            y = -0.135.dh
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

@Composable
private fun PromoCover(
    drawableRes: Int,
    size: Dp,
    x: Dp,
    y: Dp
) {
    Image(
        painter = painterResource(id = drawableRes),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .offset(x = x, y = y)
            .size(size)
            .rotate(TILT_ANGLE)
            .shadow(elevation = 0.015.dw, shape = RoundedCornerShape(0.02.dw))
            .clip(RoundedCornerShape(0.02.dw))
    )
}
