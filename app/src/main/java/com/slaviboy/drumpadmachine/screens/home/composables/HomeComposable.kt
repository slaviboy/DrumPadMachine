package com.slaviboy.drumpadmachine.screens.home.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.bumptech.glide.integration.compose.CrossFade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.slaviboy.composeunits.dh
import com.slaviboy.composeunits.dw
import com.slaviboy.composeunits.sw
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.extensions.bounceClick
import com.slaviboy.drumpadmachine.modules.NetworkModule.Companion.BASE_URL
import com.slaviboy.drumpadmachine.screens.home.viewmodels.HomeViewModel
import com.slaviboy.drumpadmachine.ui.RobotoFont
import com.slaviboy.drumpadmachine.ui.backgroundGradientBottom
import com.slaviboy.drumpadmachine.ui.backgroundGradientTop

@OptIn(ExperimentalGlideComposeApi::class)
@RootNavGraph(start = true)
@Destination
@Composable
fun HomeComposable(
    homeViewModel: HomeViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        backgroundGradientTop,
                        backgroundGradientBottom
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            TopBox()

            Spacer(
                modifier = Modifier
                    .height(0.06.dw)
            )
            homeViewModel.categoriesMapState.value.forEach {
                val categoryName = it.key
                val presets = it.value
                Row(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(horizontal = 0.03.dw),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = categoryName,
                        color = Color.White,
                        fontFamily = RobotoFont,
                        fontSize = 0.055.sw,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "See all".uppercase(),
                        color = Color.White,
                        fontFamily = RobotoFont,
                        fontSize = 0.032.sw,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(
                    modifier = Modifier
                        .height(0.02.dw)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 0.03.dw)
                ) {
                    presets.forEach {
                        Column {
                            /*GlideImage(
                                model = "${BASE_URL}cover_icons/${it.id}.jpg",
                                contentDescription = null,
                                modifier = Modifier
                                    .size(0.35.dw)
                                    .clip(RoundedCornerShape(0.04.dw)),
                                transition = CrossFade
                            )*/
                            Box(
                                contentAlignment = Alignment.BottomEnd
                            ) {
                                GlideImage(
                                    model = "${BASE_URL}cover_icons/${it.id}.jpg",
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(0.35.dw)
                                        .clip(RoundedCornerShape(0.04.dw)),
                                    transition = CrossFade
                                )
                                Image(
                                    painter = painterResource(id = R.drawable.ic_play_button),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(0.06.dw)
                                        .offset(
                                            x = (-0.02).dw,
                                            y = (-0.02).dw
                                        )
                                )
                            }
                            /*Image(
                                painter = rememberAsyncImagePainter("${BASE_URL}cover_icons/${it.id}.jpg"),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(0.35.dw)
                                    .clip(RoundedCornerShape(0.04.dw))
                            )*/
                            Spacer(
                                modifier = Modifier
                                    .height(0.02.dw)
                            )
                            Text(
                                text = it.name ?: "",
                                color = Color.White,
                                fontFamily = RobotoFont,
                                fontSize = 0.035.sw,
                                fontWeight = FontWeight.Normal
                            )
                            Text(
                                text = it.author ?: "",
                                color = Color.White,
                                fontFamily = RobotoFont,
                                fontSize = 0.023.sw,
                                fontWeight = FontWeight.Normal
                            )
                        }
                        Spacer(
                            modifier = Modifier
                                .width(0.04.dw)
                        )
                    }
                }
                Spacer(
                    modifier = Modifier
                        .height(0.06.dw)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.18.dw)
                .background(
                    Color(0xFF19182C)
                )
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            homeViewModel.menuItemsState.value.forEach {
                val color by remember {
                    mutableStateOf(
                        if (it.isSelected) {
                            Color(0xFFffd112)
                        } else {
                            Color(0xFF444350)
                        }
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight()
                        .bounceClick {

                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = it.iconResId),
                        contentDescription = null,
                        modifier = Modifier
                            .size(0.09.dw),
                        colorFilter = ColorFilter.tint(color)
                    )
                    Text(
                        text = stringResource(id = it.titleResId),
                        color = color,
                        fontFamily = RobotoFont,
                        fontSize = 0.02.sw,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
fun TopBox() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.25.dh)
            .background(
                brush = Brush.horizontalGradient(
                    listOf(
                        Color(0xFF274973),
                        Color(0xFFE361F9)
                    )
                )
            )
    ) {
        Text(
            text = "Unlock\nall sounds".uppercase(),
            color = Color.White,
            fontFamily = RobotoFont,
            fontSize = 0.06.sw,
            fontWeight = FontWeight.Black,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = 0.04.dw)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = -(0.04).dw, y = -(0.04).dw)
                .bounceClick {

                }
                .background(Color(0xFFFFD112), CircleShape)
                .padding(horizontal = 0.07.dw, vertical = 0.03.dw),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Upgrade".uppercase(),
                color = Color(0xFF21212B),
                fontFamily = RobotoFont,
                fontSize = 0.034.sw,
                fontWeight = FontWeight.Black
            )
        }
    }
}