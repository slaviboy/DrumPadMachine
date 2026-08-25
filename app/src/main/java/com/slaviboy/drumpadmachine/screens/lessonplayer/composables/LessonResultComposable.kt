package com.slaviboy.drumpadmachine.screens.lessonplayer.composables

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.slaviboy.composeunits.DeviceHeight
import com.slaviboy.composeunits.DeviceWidth
import com.slaviboy.composeunits.Density
import com.slaviboy.composeunits.ScaleDensity
import com.slaviboy.composeunits.dw
import com.slaviboy.composeunits.sw
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.extensions.bounceClick
import com.slaviboy.drumpadmachine.ui.RobotoFont
import kotlin.math.roundToInt

private val PassColor = Color(0xFFFFD011)
private val FailColor = Color(0xFFFF4D67)

private fun starsFor(scorePercent: Int): Int = when {
    scorePercent >= 90 -> 3
    scorePercent >= 60 -> 2
    scorePercent > 0 -> 1
    else -> 0
}

/** Small rotated square used as a "romb" accent scattered around the result ring. */
@Composable
private fun DiamondDecoration(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(0.025.dw)
            .rotate(45f)
            .background(color = Color(0x33FFFFFF), shape = RoundedCornerShape(0.004.dw))
    )
}

/** Graduation-cap accent scattered around the result ring, dimmed so it stays in the background. */
@Composable
private fun GraduationCapDecoration(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.ic_graduation),
        contentDescription = null,
        modifier = modifier
            .size(0.085.dw)
            .alpha(0.35f)
    )
}

@Composable
fun LessonResultComposable(
    isPass: Boolean,
    scorePercent: Int,
    bestScorePercent: Int,
    onReplay: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ringColor = if (isPass) PassColor else FailColor
    val animatedScore = remember { Animatable(0f) }
    LaunchedEffect(scorePercent) {
        animatedScore.snapTo(0f)
        animatedScore.animateTo(
            targetValue = scorePercent / 100f,
            animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing)
        )
    }
    val animatedScorePercent = (animatedScore.value * 100f).roundToInt()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 0.08.dw),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(0.1.dw))
        Text(
            text = stringResource(id = if (isPass) R.string.congratulations else R.string.oops),
            color = Color.White,
            fontFamily = RobotoFont,
            fontSize = 0.08.sw,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(0.18.dw))
        Box(
            modifier = Modifier.size(0.55.dw),
            contentAlignment = Alignment.Center
        ) {
            DiamondDecoration(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 0.03.dw, y = (-0.02).dw)
            )
            GraduationCapDecoration(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 0.15.dw, y = 0.16.dw)
            )
            DiamondDecoration(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = (-0.08).dw, y = (-0.2).dw)
            )
            GraduationCapDecoration(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = (-0.16).dw, y = 0.06.dw)
            )
            DiamondDecoration(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-0.03).dw, y = 0.04.dw)
            )
            GraduationCapDecoration(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 0.05.dw, y = 0.06.dw)
            )
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = size.minDimension * 0.045f
                drawArc(
                    color = Color(0x33FFFFFF),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
                drawArc(
                    color = ringColor,
                    startAngle = -90f,
                    sweepAngle = 360f * animatedScore.value.coerceIn(0f, 1f),
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val stars = starsFor(scorePercent)
                Row {
                    for (i in 0 until 3) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_star),
                            contentDescription = null,
                            modifier = Modifier
                                .size(0.07.dw)
                                .clip(RoundedCornerShape(0)),
                            colorFilter = ColorFilter.tint(if (i < stars) PassColor else Color(0xFF6D6C7D))
                        )
                    }
                }
                Spacer(modifier = Modifier.height(0.02.dw))
                Text(
                    text = "$animatedScorePercent%",
                    color = Color.White,
                    fontFamily = RobotoFont,
                    fontSize = 0.1.sw,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(0.015.dw))
                Text(
                    text = stringResource(id = R.string.best_result).format(bestScorePercent),
                    color = Color.LightGray,
                    fontFamily = RobotoFont,
                    fontSize = 0.038.sw,
                    fontWeight = FontWeight.Normal
                )
            }
        }
        Spacer(modifier = Modifier.height(0.11.dw))
        Text(
            text = stringResource(id = if (isPass) R.string.lesson_completed else R.string.you_can_do_better),
            color = Color.White,
            fontFamily = RobotoFont,
            fontSize = 0.05.sw,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 0.08.dw),
            horizontalArrangement = Arrangement.spacedBy(0.04.dw)
        ) {
            Text(
                text = stringResource(id = R.string.replay).uppercase(),
                color = Color.White,
                fontFamily = RobotoFont,
                fontSize = 0.04.sw,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .bounceClick(onClick = onReplay)
                    .background(
                        color = Color(0x3348475C),
                        shape = RoundedCornerShape(0.02.dw)
                    )
                    .padding(0.035.dw)
            )
            Text(
                text = stringResource(id = R.string.done).uppercase(),
                color = Color.Black,
                fontFamily = RobotoFont,
                fontSize = 0.04.sw,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .bounceClick(onClick = onDone)
                    .background(
                        color = PassColor,
                        shape = RoundedCornerShape(0.02.dw)
                    )
                    .padding(0.035.dw)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF232339)
@Composable
private fun LessonResultComposablePassPreview() {
    DeviceWidth = 1080f
    DeviceHeight = 2400f
    Density = 3f
    ScaleDensity = 3f
    LessonResultComposable(
        isPass = true,
        scorePercent = 100,
        bestScorePercent = 100,
        onReplay = {},
        onDone = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF232339)
@Composable
private fun LessonResultComposableFailPreview() {
    DeviceWidth = 1080f
    DeviceHeight = 2400f
    Density = 3f
    ScaleDensity = 3f
    LessonResultComposable(
        isPass = false,
        scorePercent = 17,
        bestScorePercent = 17,
        onReplay = {},
        onDone = {}
    )
}
