package com.slaviboy.drumpadmachine.composables

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.slaviboy.composeunits.dw
import com.slaviboy.composeunits.sw
import com.slaviboy.drumpadmachine.extensions.bounceClick
import com.slaviboy.drumpadmachine.ui.RobotoFont

@Composable
fun ImageButtonWithText(
    modifier: Modifier = Modifier,
    @DrawableRes iconResId: Int,
    @StringRes textResId: Int,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .wrapContentWidth()
            .bounceClick(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            modifier = Modifier
                .size(0.08.dw),
            colorFilter = ColorFilter.tint(Color.Gray)
        )
        Text(
            text = stringResource(id = textResId).uppercase(),
            color = Color.Gray,
            fontFamily = RobotoFont,
            fontSize = 0.035.sw,
            fontWeight = FontWeight.Normal
        )
    }
}