package com.slaviboy.drumpadmachine.screens.lessonslist.composables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.CrossFade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.slaviboy.composeunits.DpToPx
import com.slaviboy.composeunits.dh
import com.slaviboy.composeunits.dw
import com.slaviboy.composeunits.sw
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.composables.ScrollableContainer
import com.slaviboy.drumpadmachine.composables.SearchTextField
import com.slaviboy.drumpadmachine.data.entities.Lesson
import com.slaviboy.drumpadmachine.data.entities.LessonState
import com.slaviboy.drumpadmachine.data.entities.Pad
import com.slaviboy.drumpadmachine.data.entities.Preset
import com.slaviboy.drumpadmachine.extensions.bounceClick
import com.slaviboy.drumpadmachine.extensions.factMultiplyBy
import com.slaviboy.drumpadmachine.modules.NetworkModule
import com.slaviboy.drumpadmachine.screens.drumpad.helpers.DrumPadHelper
import com.slaviboy.drumpadmachine.screens.lessonslist.viewmodels.LessonsListViewModel
import com.slaviboy.drumpadmachine.ui.RobotoFont
import com.slaviboy.drumpadmachine.ui.backgroundGradientBottom
import com.slaviboy.drumpadmachine.ui.backgroundGradientTop

@OptIn(ExperimentalComposeUiApi::class, ExperimentalGlideComposeApi::class)
@Destination
@Composable
@RootNavGraph(start = true)
fun LessonsListComposable(
    navigator: DestinationsNavigator,
    lessonsListViewModel: LessonsListViewModel,
    onError: (error: String) -> Unit = {},
    // preset: Preset
) {
    val preset: Preset = Preset(
        0, "Hey", "What yo", null, null, null, null, false, 1, null, null,

        listOf(
            Lesson(
                id = 0,
                side = "a",
                version = 0,
                name = "01",
                orderBy = 0,
                sequencerSize = 33,
                rating = 3,
                lastScore = 20,
                bestScore = 60,
                lessonState = LessonState.Replay,
                pads = hashMapOf(
                    "9" to arrayOf(Pad(0, false), Pad(16, false)),
                    "10" to arrayOf(Pad(4, false), Pad(12, false), Pad(20, false), Pad(28, false)),
                    "11" to arrayOf(Pad(8, false), Pad(24, false))
                )
            ),
            Lesson(
                id = 1,
                side = "b",
                version = 0,
                name = "02",
                orderBy = 0,
                sequencerSize = 65,
                rating = 0,
                lastScore = 0,
                bestScore = 0,
                lessonState = LessonState.Play,
                pads = hashMapOf(
                    "0" to arrayOf(Pad(0, false)),
                    "1" to arrayOf(Pad(16, false)),
                    "2" to arrayOf(Pad(8, false)),
                    "9" to arrayOf(Pad(8, false), Pad(16, false)),
                    "1" to arrayOf(Pad(4, false), Pad(12, false), Pad(20, false), Pad(28, false)),
                    "11" to arrayOf(Pad(8, false), Pad(24, false))
                )
            )
        )
    )
    LaunchedEffect(preset) {
        lessonsListViewModel.init(preset)
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    var topBarHeight by remember {
        mutableStateOf(0.dw)
    }
    ScrollableContainer(
        minHeight = 0.36.dw,
        maxHeight = 0.53.dw,
        topBar = { height, minHeight, maxHeight ->
            topBarHeight = height
            LessonsListTopBar(
                title = preset.name,
                subtitle = preset.author ?: "",
                height = height,
                minHeight = minHeight,
                maxHeight = maxHeight,
                leftIconResId = R.drawable.ic_arrow_left,
                text = lessonsListViewModel.searchTextState.value,
                presetId = preset.id,
                onTextChange = {
                    lessonsListViewModel.changeText(it)
                },
                onClearText = {
                    lessonsListViewModel.changeText("")
                },
                onLeftButtonClicked = {
                    navigator.navigateUp()
                },
                onRightButtonClicked = { }
            )
        },
        contentHorizontalAlignment = Alignment.CenterHorizontally
    ) { _, topBarOffset ->
        item {
            Spacer(
                modifier = Modifier
                    .height(0.04.dw + topBarOffset)
            )
        }
        val list = lessonsListViewModel.filteredLessonsState.value
        items(list.size) {
            LessonItem(list[it])
            Spacer(
                modifier = Modifier
                    .height(0.06.dw)
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun LessonsListTopBar(
    height: Dp,
    minHeight: Dp? = null,
    maxHeight: Dp? = null,
    title: String,
    subtitle: String,
    presetId: Int,
    @DrawableRes leftIconResId: Int? = null,
    @DrawableRes rightIconResId: Int? = null,
    text: String,
    onTextChange: (text: String) -> Unit = {},
    onClearText: () -> Unit = {},
    onLeftButtonClicked: () -> Unit = {},
    onRightButtonClicked: () -> Unit = {}
) {
    val fact = if (minHeight != null && maxHeight != null) {
        (height - minHeight) / (maxHeight - minHeight)
    } else {
        1f
    }
    val fontFact = Math.max(0.8f, fact)
    val heightPx = with(LocalDensity.current) { height.toPx() }
    val cornerRadius = 0.02.dw * fact
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        backgroundGradientTop,
                        backgroundGradientBottom
                    ),
                    endY = 1.dh.value.DpToPx
                )
            )
            .padding(horizontal = 0.04.dw)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(
                    y = 0.07.dw
                ),
            horizontalArrangement = if (leftIconResId != null && rightIconResId != null) {
                Arrangement.SpaceBetween
            } else if (leftIconResId != null) {
                Arrangement.Start
            } else {
                Arrangement.End
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            leftIconResId?.let {
                Image(
                    modifier = Modifier
                        .width(0.08.dw)
                        .wrapContentHeight()
                        .clip(CircleShape)
                        .bounceClick {
                            onLeftButtonClicked()
                        },
                    contentScale = ContentScale.FillWidth,
                    painter = painterResource(id = it),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
            rightIconResId?.let {
                Image(
                    modifier = Modifier
                        .width(0.04.dw)
                        .wrapContentHeight()
                        .clip(CircleShape)
                        .alpha(fact)
                        .bounceClick {
                            onRightButtonClicked()
                        },
                    contentScale = ContentScale.FillWidth,
                    painter = painterResource(
                        id = it
                    ),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
        }
        Text(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(
                    y = 0.029.dh
                )
                .alpha(1f - fact.factMultiplyBy(2f)),
            text = title,
            fontSize = 0.075.sw * fontFact,
            fontWeight = FontWeight.Bold,
            fontFamily = RobotoFont,
            color = Color.White
        )
        val presetAlpha = fact.factMultiplyBy(2f)
        if (presetAlpha > 0) {
            Row(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .align(Alignment.TopCenter)
                    .offset(
                        y = 0.05.dh * fact.factMultiplyBy(0.62f)
                    )
                    .alpha(presetAlpha),
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
                Spacer(
                    modifier = Modifier
                        .width(0.02.dw)
                )
                Column {
                    Text(
                        text = title,
                        color = Color.White,
                        fontFamily = RobotoFont,
                        fontSize = 0.063.sw,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(horizontal = 0.01.dw),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = subtitle,
                        color = Color.LightGray,
                        fontFamily = RobotoFont,
                        fontSize = 0.032.sw,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier
                            .padding(horizontal = 0.01.dw),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(
                    modifier = Modifier
                        .width(0.04.dw)
                )
            }
        }

        val infoAlpha = fact.factMultiplyBy(2f)
        if (infoAlpha > 0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .align(Alignment.TopCenter)
                    .offset(
                        y = 0.13.dh * fact.factMultiplyBy(0.62f)
                    )
                    .alpha(infoAlpha),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        modifier = Modifier
                            .width(0.06.dw)
                            .wrapContentHeight()
                            .clip(CircleShape)
                            .alpha(fact),
                        contentScale = ContentScale.FillWidth,
                        painter = painterResource(
                            id = R.drawable.ic_lessons
                        ),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(Color(0xFFFFD011))
                    )
                    Spacer(
                        modifier = Modifier
                            .width(0.03.dw)
                    )
                    Text(
                        text = "1/15",
                        color = Color.LightGray,
                        fontFamily = RobotoFont,
                        fontSize = 0.042.sw,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        modifier = Modifier
                            .width(0.05.dw)
                            .wrapContentHeight()
                            .clip(CircleShape)
                            .alpha(fact),
                        contentScale = ContentScale.FillWidth,
                        painter = painterResource(
                            id = R.drawable.ic_star
                        ),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(Color(0xFFFFD011))
                    )
                    Spacer(
                        modifier = Modifier
                            .width(0.03.dw)
                    )
                    Text(
                        text = "1/15",
                        color = Color.LightGray,
                        fontFamily = RobotoFont,
                        fontSize = 0.042.sw,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        SearchTextField(
            text = text,
            onTextChange = onTextChange,
            onClearText = onClearText,
            modifier = Modifier
                .fillMaxWidth()
                .height(0.122.dw)
                .align(Alignment.TopCenter)
                .offset(
                    y = 0.18.dh * fact.factMultiplyBy(0.5f)
                )
        )
    }
}

@Composable
fun LessonItem(
    lesson: Lesson,
    numberOfRows: Int = 4,
    numberOfColumns: Int = 3
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(
                horizontal = 0.04.dw
            )
            .background(
                color = Color(0x3348475C),
                shape = RoundedCornerShape(0.02.dw)
            )
            .padding(
                horizontal = 0.03.dw,
                vertical = 0.02.dw
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier
                        .width(0.09.dw)
                        .wrapContentHeight()
                        .clip(CircleShape),
                    contentScale = ContentScale.FillWidth,
                    painter = painterResource(
                        id = R.drawable.ic_circular_check
                    ),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color(0xFFFFD011))
                )
                Spacer(
                    modifier = Modifier
                        .width(0.02.dw)
                )
                Text(
                    text = "Lesson ${lesson.id + 1}",
                    color = Color.LightGray,
                    fontFamily = RobotoFont,
                    fontSize = 0.048.sw,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row {
                for (i in 0 until 5) {
                    val iconColor = if (i < lesson.rating) {
                        Color(0xFFFFD011)
                    } else {
                        Color(0xFF6D6C7D)
                    }
                    Image(
                        modifier = Modifier
                            .width(0.05.dw)
                            .wrapContentHeight()
                            .clip(CircleShape),
                        contentScale = ContentScale.FillWidth,
                        painter = painterResource(
                            id = R.drawable.ic_star
                        ),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(iconColor)
                    )
                }
            }
        }
        Spacer(
            modifier = Modifier
                .height(0.04.dw)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    for (i in 0 until numberOfRows) {
                        Row {
                            for (j in 0 until numberOfColumns) {
                                val index = DrumPadHelper.getIndex(i, j)
                                val padColor = if (lesson.pads.containsKey("$index")) {
                                    Color(0xFF908F9C)
                                } else {
                                    Color(0xFF606071)
                                }
                                Box(
                                    modifier = Modifier
                                        .size(0.043.dw)
                                        .padding(0.002.dw)
                                        .background(
                                            color = padColor,
                                            shape = RoundedCornerShape(0.01.dw)
                                        )
                                )
                            }
                        }
                    }
                    Spacer(
                        modifier = Modifier
                            .height(0.02.dw)
                    )
                    Text(
                        text = lesson.side.uppercase(),
                        color = Color.LightGray,
                        fontFamily = RobotoFont,
                        fontSize = 0.048.sw,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Row(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Row {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Text(
                                text = stringResource(id = R.string.last),
                                color = Color.LightGray,
                                fontFamily = RobotoFont,
                                fontSize = 0.036.sw,
                                fontWeight = FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${lesson.lastScore}%",
                                color = Color.White,
                                fontFamily = RobotoFont,
                                fontSize = 0.043.sw,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Text(
                                text = stringResource(id = R.string.best),
                                color = Color.LightGray,
                                fontFamily = RobotoFont,
                                fontSize = 0.036.sw,
                                fontWeight = FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${lesson.bestScore}%",
                                color = Color.White,
                                fontFamily = RobotoFont,
                                fontSize = 0.043.sw,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    Spacer(
                        modifier = Modifier
                            .height(0.03.dw)
                    )
                    when (lesson.lessonState) {
                        LessonState.Replay, LessonState.Unlock -> {
                            Text(
                                color = Color.White,
                                fontFamily = RobotoFont,
                                fontSize = 0.038.sw,
                                fontWeight = FontWeight.Bold,
                                text = stringResource(id = lesson.lessonState.textResId).uppercase(),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .bounceClick {

                                    }
                                    .border(
                                        width = 1.dp,
                                        color = Color(0xFF59586B),
                                        shape = RoundedCornerShape(0.02.dw)
                                    )
                                    .padding(0.021.dw)
                            )
                        }

                        LessonState.Play -> {
                            Text(
                                color = Color.Black,
                                fontFamily = RobotoFont,
                                fontSize = 0.038.sw,
                                fontWeight = FontWeight.Bold,
                                text = stringResource(id = lesson.lessonState.textResId).uppercase(),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .bounceClick {

                                    }
                                    .background(
                                        color = Color(0xFFFFD011),
                                        shape = RoundedCornerShape(0.02.dw)
                                    )
                                    .padding(0.021.dw)
                            )
                        }
                    }
                }
            }
        }
        Spacer(
            modifier = Modifier
                .height(0.04.dw)
        )
    }
}