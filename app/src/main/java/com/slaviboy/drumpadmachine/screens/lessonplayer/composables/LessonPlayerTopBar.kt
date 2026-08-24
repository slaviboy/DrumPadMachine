package com.slaviboy.drumpadmachine.screens.lessonplayer.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
    val listenSegmentFraction = if (phase == LessonPhase.Listen) listenProgress else 1f
    val playSegmentFraction = when (phase) {
        LessonPhase.Listen -> 0f
        LessonPhase.Play -> playProgress
        LessonPhase.Result -> 1f
    }
    Column(modifier = modifier.fillMaxWidth()) {
        val listenColor = if (phase == LessonPhase.Listen) ActiveColor else InactiveColor
        val playColor = if (phase != LessonPhase.Listen) ActiveColor else InactiveColor
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.07.dw)
        ) {
            // Icons anchor exactly to the start/middle/end dot positions of the track below -
            // the start/end icons are centered ON the row's edge (not aligned by their own edge
            // to it), so they're shifted out by half their own width to sit on top of the dot.
            val edgeIconSize = 0.07.dw
            Image(
                painter = painterResource(id = R.drawable.ic_note),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = -(edgeIconSize / 2))
                    .size(edgeIconSize),
                colorFilter = ColorFilter.tint(listenColor)
            )
            Image(
                painter = painterResource(id = R.drawable.ic_play),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(0.085.dw)
            )
            Image(
                painter = painterResource(id = R.drawable.ic_circular_check),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = edgeIconSize / 2)
                    .size(edgeIconSize),
                colorFilter = ColorFilter.tint(if (phase == LessonPhase.Result) ActiveColor else InactiveColor)
            )
            // Labels are centered within their own half of the row, independent of the icons.
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth(0.5f)
            ) {
                Text(
                    text = stringResource(id = R.string.listen),
                    color = listenColor,
                    fontFamily = RobotoFont,
                    fontSize = 0.045.sw,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxWidth(0.5f)
            ) {
                Text(
                    text = stringResource(id = R.string.play),
                    color = playColor,
                    fontFamily = RobotoFont,
                    fontSize = 0.045.sw,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        Spacer(modifier = Modifier.height(0.025.dw))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.07.dw),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProgressDot(filled = true)
            ProgressSegment(
                fraction = listenSegmentFraction,
                modifier = Modifier
                    .weight(1f)
                    .height(0.008.dw)
            )
            ProgressDot(filled = listenSegmentFraction >= 1f)
            ProgressSegment(
                fraction = playSegmentFraction,
                modifier = Modifier
                    .weight(1f)
                    .height(0.008.dw)
            )
            ProgressDot(filled = playSegmentFraction >= 1f)
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

@Composable
private fun ProgressDot(filled: Boolean) {
    Box(
        modifier = Modifier
            .size(0.022.dw)
            .background(color = if (filled) ActiveColor else TrackColor, shape = CircleShape)
    )
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
