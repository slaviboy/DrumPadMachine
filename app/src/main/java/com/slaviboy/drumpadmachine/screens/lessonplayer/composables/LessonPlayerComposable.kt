package com.slaviboy.drumpadmachine.screens.lessonplayer.composables

import android.os.SystemClock
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.slaviboy.composeunits.dw
import com.slaviboy.composeunits.sw
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.data.entities.Lesson
import com.slaviboy.drumpadmachine.data.entities.Preset
import com.slaviboy.drumpadmachine.extensions.bounceClick
import com.slaviboy.drumpadmachine.screens.lessonplayer.models.LessonPhase
import com.slaviboy.drumpadmachine.screens.lessonplayer.models.LessonResultPayload
import com.slaviboy.drumpadmachine.screens.lessonplayer.viewmodels.LessonPlayerViewModel
import com.slaviboy.drumpadmachine.ui.RobotoFont
import com.slaviboy.drumpadmachine.ui.backgroundGradientBottom
import com.slaviboy.drumpadmachine.ui.backgroundGradientTop

@RootNavGraph(start = false)
@Destination
@Composable
fun LessonPlayerComposable(
    resultNavigator: ResultBackNavigator<LessonResultPayload>,
    lessonPlayerViewModel: LessonPlayerViewModel,
    preset: Preset,
    lesson: Lesson
) {
    LaunchedEffect(preset, lesson) {
        lessonPlayerViewModel.start(preset, lesson)
    }

    val uiState by lessonPlayerViewModel.uiState

    val finish: () -> Unit = {
        lessonPlayerViewModel.onDone()
        val payload = uiState.savedResultPayload
        if (payload != null) {
            resultNavigator.navigateBack(result = payload)
        } else {
            resultNavigator.navigateBack()
        }
    }

    BackHandler(onBack = finish)

    val listenProgress = remember { Animatable(0f) }
    LaunchedEffect(uiState.listenStartedAtElapsedRealtime) {
        val startedAt = uiState.listenStartedAtElapsedRealtime ?: return@LaunchedEffect
        listenProgress.snapTo(0f)
        val remaining = uiState.listenTotalDurationMs - (SystemClock.elapsedRealtime() - startedAt)
        if (remaining > 0) {
            listenProgress.animateTo(1f, tween(remaining.toInt(), easing = LinearEasing))
        } else {
            listenProgress.snapTo(1f)
        }
    }

    val playProgress = remember { Animatable(0f) }
    LaunchedEffect(uiState.playActivatedAtElapsedRealtime) {
        val activatedAt = uiState.playActivatedAtElapsedRealtime ?: return@LaunchedEffect
        playProgress.snapTo(uiState.playActivationFraction)
        val remainingMsAtActivation = uiState.playTotalDurationMs * (1f - uiState.playActivationFraction)
        val trueRemaining = remainingMsAtActivation - (SystemClock.elapsedRealtime() - activatedAt)
        if (trueRemaining > 0) {
            playProgress.animateTo(1f, tween(trueRemaining.toInt(), easing = LinearEasing))
        } else {
            playProgress.snapTo(1f)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(
                brush = Brush.verticalGradient(listOf(backgroundGradientTop, backgroundGradientBottom))
            )
    ) {
        Column(modifier = Modifier.fillMaxHeight()) {
            Spacer(modifier = Modifier.height(0.07.dw))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.04.dw),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_arrow_left),
                    contentDescription = null,
                    modifier = Modifier
                        .size(0.07.dw)
                        .bounceClick(onClick = finish),
                    colorFilter = ColorFilter.tint(Color.White)
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.lessons_number).format(lesson.id + 1),
                        color = Color.White,
                        fontFamily = RobotoFont,
                        fontSize = 0.048.sw,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = lesson.name,
                        color = Color.LightGray,
                        fontFamily = RobotoFont,
                        fontSize = 0.036.sw,
                        fontWeight = FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (uiState.phase == LessonPhase.Result) {
                    Spacer(modifier = Modifier.size(0.07.dw))
                } else {
                    Text(
                        text = stringResource(id = R.string.done),
                        color = Color(0xFFFFD011),
                        fontFamily = RobotoFont,
                        fontSize = 0.045.sw,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .bounceClick(onClick = finish)
                            .padding(0.01.dw)
                    )
                }
            }

            if (uiState.phase == LessonPhase.Result) {
                LessonResultComposable(
                    isPass = uiState.isPass,
                    scorePercent = uiState.finalScorePercent ?: 0,
                    bestScorePercent = uiState.bestScorePercent,
                    onReplay = { lessonPlayerViewModel.start(preset, lesson) },
                    onDone = finish,
                    modifier = Modifier.weight(1f)
                )
            } else {
                Spacer(modifier = Modifier.height(0.06.dw))
                LessonPlayerTopBar(
                    phase = uiState.phase,
                    listenProgress = listenProgress.value,
                    playProgress = playProgress.value
                )
                Spacer(modifier = Modifier.height(0.08.dw))
                LessonPadGrid(
                    uiState = uiState,
                    onPadTapped = lessonPlayerViewModel::onPadTapped
                )
            }
        }
    }
}
