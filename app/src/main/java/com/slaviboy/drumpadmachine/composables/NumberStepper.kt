package com.slaviboy.drumpadmachine.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.Text
import com.slaviboy.composeunits.dw
import com.slaviboy.composeunits.sw
import com.slaviboy.drumpadmachine.extensions.bounceClick
import com.slaviboy.drumpadmachine.ui.RobotoFont

/**
 * A "− value +" trio, shared between the Settings BPM row and the DrumPad tempo pill so both
 * read/write the same kind of stepped Int value with identical touch targets and styling.
 */
@Composable
fun NumberStepper(
    value: Int,
    range: IntRange,
    step: Int,
    accentColor: Color,
    onValueChange: (Int) -> Unit,
    valueColor: Color = Color.White
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "−",
            color = accentColor,
            fontFamily = RobotoFont,
            fontSize = 0.05.sw,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .bounceClick { onValueChange((value - step).coerceIn(range.first, range.last)) }
                .width(0.06.dw),
            textAlign = TextAlign.Center
        )
        Text(
            text = "$value",
            color = valueColor,
            fontFamily = RobotoFont,
            fontSize = 0.04.sw,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(0.1.dw)
        )
        Text(
            text = "+",
            color = accentColor,
            fontFamily = RobotoFont,
            fontSize = 0.05.sw,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .bounceClick { onValueChange((value + step).coerceIn(range.first, range.last)) }
                .width(0.06.dw),
            textAlign = TextAlign.Center
        )
    }
}
