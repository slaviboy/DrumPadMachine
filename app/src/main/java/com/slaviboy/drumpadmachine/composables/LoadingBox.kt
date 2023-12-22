package com.slaviboy.drumpadmachine.composables

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.slaviboy.composeunits.dw
import com.slaviboy.composeunits.sw
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.ui.RobotoFont

@Composable
fun LoadingBox(
    boxScope: BoxScope,
    modifier: Modifier,
    textColor: Color = Color.White
) = with(boxScope) {
    Column(
        modifier = modifier
            .align(Alignment.Center)
            .wrapContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(0.15.dw),
            strokeWidth = 0.012.dw,
            strokeCap = StrokeCap.Round,
            color = Color(0xFF8F56BD),
            trackColor = Color.White,
        )
        Spacer(
            modifier = Modifier
                .height(0.04.dw)
        )
        Text(
            text = stringResource(id = R.string.loading).uppercase(),
            color = textColor,
            fontFamily = RobotoFont,
            fontSize = 0.035.sw,
            fontWeight = FontWeight.Bold
        )
    }
}