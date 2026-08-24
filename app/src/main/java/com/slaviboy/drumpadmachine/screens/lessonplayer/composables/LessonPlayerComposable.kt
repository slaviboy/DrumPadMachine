package com.slaviboy.drumpadmachine.screens.lessonplayer.composables

import android.os.SystemClock
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.bumptech.glide.integration.compose.CrossFade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.slaviboy.composeunits.DeviceHeight
import com.slaviboy.composeunits.DeviceWidth
import com.slaviboy.composeunits.Density
import com.slaviboy.composeunits.ScaleDensity
import com.slaviboy.composeunits.dw
import com.slaviboy.composeunits.sw
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.data.entities.Lesson
import com.slaviboy.drumpadmachine.data.entities.Preset
import com.slaviboy.drumpadmachine.enums.PadColor
import com.slaviboy.drumpadmachine.extensions.bounceClick
import com.slaviboy.drumpadmachine.modules.NetworkModule
import com.slaviboy.drumpadmachine.screens.lessonplayer.models.LessonPhase
import com.slaviboy.drumpadmachine.screens.lessonplayer.models.LessonPlayerUiState
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

    LessonPlayerScreenContent(
        presetId = preset.id,
        lessonNumber = lesson.id + 1,
        lessonName = lesson.name,
        uiState = uiState,
        listenProgress = listenProgress.value,
        playProgress = playProgress.value,
        onBack = finish,
        onDone = finish,
        onReplay = { lessonPlayerViewModel.start(preset, lesson) },
        onPadTapped = lessonPlayerViewModel::onPadTapped
    )
}

/**
 * Pure UI body of the lesson player, split out from [LessonPlayerComposable] so it can be
 * previewed with hand-built [LessonPlayerUiState] values - the destination composable above
 * owns the Hilt [LessonPlayerViewModel] and its native-audio side effects, which can't run
 * inside a Compose preview.
 */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun LessonPlayerScreenContent(
    presetId: Long,
    lessonNumber: Int,
    lessonName: String,
    uiState: LessonPlayerUiState,
    listenProgress: Float,
    playProgress: Float,
    onBack: () -> Unit,
    onDone: () -> Unit,
    onReplay: () -> Unit,
    onPadTapped: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(
                brush = Brush.verticalGradient(listOf(backgroundGradientTop, backgroundGradientBottom))
            )
    ) {
        Column(modifier = Modifier.fillMaxHeight()) {
            Spacer(modifier = Modifier.height(0.1.dw))
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
                        .bounceClick(onClick = onBack),
                    colorFilter = ColorFilter.tint(Color.White)
                )
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlideImage(
                        model = NetworkModule.coverIconUrl(presetId),
                        contentDescription = null,
                        modifier = Modifier
                            .size(0.14.dw)
                            .clip(RoundedCornerShape(0.02.dw)),
                        transition = CrossFade,
                        failure = placeholder(R.drawable.ic_no_image),
                        loading = placeholder(R.drawable.ic_default_image)
                    )
                    Spacer(modifier = Modifier.width(0.02.dw))
                    Column {
                        Text(
                            text = stringResource(id = R.string.lessons_number).format(lessonNumber),
                            color = Color.White,
                            fontFamily = RobotoFont,
                            fontSize = 0.048.sw,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = lessonName,
                            color = Color.LightGray,
                            fontFamily = RobotoFont,
                            fontSize = 0.036.sw,
                            fontWeight = FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
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
                            .bounceClick(onClick = onDone)
                            .padding(0.01.dw)
                    )
                }
            }

            if (uiState.phase == LessonPhase.Result) {
                LessonResultComposable(
                    isPass = uiState.isPass,
                    scorePercent = uiState.finalScorePercent ?: 0,
                    bestScorePercent = uiState.bestScorePercent,
                    onReplay = onReplay,
                    onDone = onDone,
                    modifier = Modifier.weight(1f)
                )
            } else {
                Spacer(modifier = Modifier.height(0.08.dw))
                LessonPlayerTopBar(
                    phase = uiState.phase,
                    listenProgress = listenProgress,
                    playProgress = playProgress
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    LessonPadGrid(
                        uiState = uiState,
                        onPadTapped = onPadTapped
                    )
                }
            }
        }
    }
}

private fun initPreviewUnits() {
    DeviceWidth = 1080f
    DeviceHeight = 2400f
    Density = 3f
    ScaleDensity = 3f
}

private val previewUsedPadColors = mapOf(9 to PadColor.Blue, 10 to PadColor.Orange, 11 to PadColor.Blue)

@Preview(showBackground = true, backgroundColor = 0xFF232339)
@Composable
private fun LessonPlayerScreenListenPreview() {
    initPreviewUnits()
    LessonPlayerScreenContent(
        presetId = 1,
        lessonNumber = 1,
        lessonName = "New Tone",
        uiState = LessonPlayerUiState(
            phase = LessonPhase.Listen,
            usedPadIndices = setOf(9, 10, 11),
            padColors = previewUsedPadColors,
            glowingPads = setOf(10)
        ),
        listenProgress = 0.45f,
        playProgress = 0f,
        onBack = {},
        onDone = {},
        onReplay = {},
        onPadTapped = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF232339)
@Composable
private fun LessonPlayerScreenPlayPreview() {
    initPreviewUnits()
    LessonPlayerScreenContent(
        presetId = 1,
        lessonNumber = 1,
        lessonName = "New Tone",
        uiState = LessonPlayerUiState(
            phase = LessonPhase.Play,
            usedPadIndices = setOf(9, 10, 11),
            padColors = previewUsedPadColors,
            expectedPadIndices = setOf(9)
        ),
        listenProgress = 1f,
        playProgress = 0f,
        onBack = {},
        onDone = {},
        onReplay = {},
        onPadTapped = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF232339)
@Composable
private fun LessonPlayerScreenResultPassPreview() {
    initPreviewUnits()
    LessonPlayerScreenContent(
        presetId = 1,
        lessonNumber = 1,
        lessonName = "New Tone",
        uiState = LessonPlayerUiState(
            phase = LessonPhase.Result,
            finalScorePercent = 100,
            bestScorePercent = 100,
            isPass = true
        ),
        listenProgress = 1f,
        playProgress = 1f,
        onBack = {},
        onDone = {},
        onReplay = {},
        onPadTapped = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF232339)
@Composable
private fun LessonPlayerScreenResultFailPreview() {
    initPreviewUnits()
    LessonPlayerScreenContent(
        presetId = 1,
        lessonNumber = 1,
        lessonName = "New Tone",
        uiState = LessonPlayerUiState(
            phase = LessonPhase.Result,
            finalScorePercent = 17,
            bestScorePercent = 17,
            isPass = false
        ),
        listenProgress = 1f,
        playProgress = 1f,
        onBack = {},
        onDone = {},
        onReplay = {},
        onPadTapped = {}
    )
}
