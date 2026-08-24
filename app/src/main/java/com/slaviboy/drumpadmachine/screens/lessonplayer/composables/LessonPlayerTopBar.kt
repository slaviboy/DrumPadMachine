package com.slaviboy.drumpadmachine.screens.lessonplayer.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.slaviboy.composeunits.DeviceHeight
import com.slaviboy.composeunits.DeviceWidth
import com.slaviboy.composeunits.Density
import com.slaviboy.composeunits.ScaleDensity
import com.slaviboy.composeunits.dw
import com.slaviboy.composeunits.sw
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.screens.lessonplayer.models.LessonPhase
import com.slaviboy.drumpadmachine.ui.RobotoFont

private val ActiveColor = Color(0xFFFFD011)
private val InactiveColor = Color(0xFF6D6C7D)
private val TrackColor = Color(0xFF3A3B4F)

@Composable
fun LessonPlayerTopBar(
    phase: LessonPhase,
    listenProgress: Float,
    playProgress: Float,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.08.dw),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val listenColor = if (phase == LessonPhase.Listen) ActiveColor else InactiveColor
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_metronome),
                    contentDescription = null,
                    modifier = Modifier.size(0.045.dw),
                    colorFilter = ColorFilter.tint(listenColor)
                )
                Spacer(modifier = Modifier.width(0.015.dw))
                Text(
                    text = stringResource(id = R.string.listen).uppercase(),
                    color = listenColor,
                    fontFamily = RobotoFont,
                    fontSize = 0.04.sw,
                    fontWeight = FontWeight.Bold
                )
            }
            val playColor = if (phase != LessonPhase.Listen) ActiveColor else InactiveColor
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.play).uppercase(),
                    color = playColor,
                    fontFamily = RobotoFont,
                    fontSize = 0.04.sw,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(0.015.dw))
                Image(
                    painter = painterResource(id = R.drawable.ic_circular_check),
                    contentDescription = null,
                    modifier = Modifier.size(0.045.dw),
                    colorFilter = ColorFilter.tint(if (phase == LessonPhase.Result) ActiveColor else InactiveColor)
                )
            }
        }
        Spacer(modifier = Modifier.height(0.02.dw))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.08.dw)
                .height(0.008.dw),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProgressSegment(
                fraction = if (phase == LessonPhase.Listen) listenProgress else 1f,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
            Spacer(modifier = Modifier.width(0.02.dw))
            ProgressSegment(
                fraction = when (phase) {
                    LessonPhase.Listen -> 0f
                    LessonPhase.Play -> playProgress
                    LessonPhase.Result -> 1f
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }
    }
}

@Composable
private fun ProgressSegment(fraction: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(color = TrackColor, shape = RoundedCornerShape(50))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction.coerceIn(0f, 1f))
                .background(color = ActiveColor, shape = RoundedCornerShape(50))
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF232339)
@Composable
private fun LessonPlayerTopBarListenPreview() {
    DeviceWidth = 1080f
    DeviceHeight = 2400f
    Density = 3f
    ScaleDensity = 3f
    LessonPlayerTopBar(phase = LessonPhase.Listen, listenProgress = 0.45f, playProgress = 0f)
}

@Preview(showBackground = true, backgroundColor = 0xFF232339)
@Composable
private fun LessonPlayerTopBarPlayPreview() {
    DeviceWidth = 1080f
    DeviceHeight = 2400f
    Density = 3f
    ScaleDensity = 3f
    LessonPlayerTopBar(phase = LessonPhase.Play, listenProgress = 1f, playProgress = 0.3f)
}

@Preview(showBackground = true, backgroundColor = 0xFF232339)
@Composable
private fun LessonPlayerTopBarResultPreview() {
    DeviceWidth = 1080f
    DeviceHeight = 2400f
    Density = 3f
    ScaleDensity = 3f
    LessonPlayerTopBar(phase = LessonPhase.Result, listenProgress = 1f, playProgress = 1f)
}
