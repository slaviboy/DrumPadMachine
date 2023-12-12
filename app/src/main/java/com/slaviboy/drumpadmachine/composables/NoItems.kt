package com.slaviboy.drumpadmachine.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.slaviboy.composeunits.dw
import com.slaviboy.composeunits.sw
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.ui.RobotoFont

@Composable
fun NoItems(
    boxScope: BoxScope,
    modifier: Modifier
) = with(boxScope) {
    Column(
        modifier = modifier
            .align(Alignment.Center)
            .wrapContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .width(0.15.dw)
                .wrapContentHeight(),
            painter = painterResource(
                id = R.drawable.ic_no_internet
            ),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            colorFilter = ColorFilter.tint(Color.White)
        )
        Spacer(
            modifier = Modifier
                .height(0.02.dw)
        )
        Text(
            text = stringResource(id = R.string.no_items),
            color = Color.White,
            fontFamily = RobotoFont,
            fontSize = 0.05.sw,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(id = R.string.please_check_your_network),
            color = Color.White,
            fontFamily = RobotoFont,
            fontSize = 0.035.sw,
            fontWeight = FontWeight.Normal
        )
    }
}