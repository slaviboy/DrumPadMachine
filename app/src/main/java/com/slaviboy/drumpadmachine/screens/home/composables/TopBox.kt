package com.slaviboy.drumpadmachine.screens.home.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.slaviboy.composeunits.dh
import com.slaviboy.composeunits.dw
import com.slaviboy.composeunits.sw
import com.slaviboy.drumpadmachine.extensions.bounceClick
import com.slaviboy.drumpadmachine.ui.RobotoFont

@Composable
fun TopBox() {
    Box(
        modifier = Modifier
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