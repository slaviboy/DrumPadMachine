package com.slaviboy.drumpadmachine.screens.home.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.CrossFade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.slaviboy.composeunits.dh
import com.slaviboy.composeunits.dw
import com.slaviboy.composeunits.sw
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.composables.LoadingBox
import com.slaviboy.drumpadmachine.data.entities.Preset
import com.slaviboy.drumpadmachine.extensions.bounceClick
import com.slaviboy.drumpadmachine.extensions.click
import com.slaviboy.drumpadmachine.extensions.pxToDp
import com.slaviboy.drumpadmachine.modules.NetworkModule
import com.slaviboy.drumpadmachine.ui.RobotoFont

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun HomePresetDetails(
    boxScope: BoxScope,
    animatedValue: Float,
    fromWidth: Dp,
    fromHeight: Dp,
    animatedWidth: Dp,
    animatedHeight: Dp,
    animatedX: Float,
    animatedY: Float,
    minHeight: Dp = 0.36.dw,
    clickedPreset: Preset?,
    isLoading: Boolean,
    onGloballyPositioned: (x: Float, y: Float) -> Unit,
    onGetPresetForFree: (presetId: Long) -> Unit,
    onGetAllPresets: () -> Unit,
    onCloseButtonClick: () -> Unit
) = with(boxScope) {
    clickedPreset ?: return@with
    if (animatedValue <= 0f) return@with
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                alpha = animatedValue
            }
            .background(Color(0xC3000000))
            .click { }
    ) {
        Box(
            modifier = Modifier
                .width(0.76.dw)
                .height(0.51.dh)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(0.043.dw)
                )
                .align(Alignment.Center)
                .onGloballyPositioned {
                    val position = it.positionInWindow()
                    onGloballyPositioned(position.x, position.y)
                }
        ) {
            Box(
                modifier = Modifier
                    .width(fromWidth)
                    .height(fromHeight)
                    .clip(RoundedCornerShape(topStart = 0.04.dw, topEnd = 0.04.dw))
            )
            if (isLoading) {
                LoadingBox(
                    boxScope = this,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.18.dh)
                        .align(Alignment.BottomCenter)
                        .padding(
                            horizontal = 0.03.dw,
                            vertical = 0.03.dw
                        ),
                    textColor = Color(0xFF050505)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.18.dh)
                        .align(Alignment.BottomCenter)
                        .padding(
                            horizontal = 0.03.dw,
                            vertical = 0.03.dw
                        ),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .bounceClick {
                                onGetPresetForFree(clickedPreset.id)
                            }
                            .border(1.dp, Color(0xFFBFBFC0), RoundedCornerShape(0.02.dw))
                            .padding(start = 0.045.dw),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .wrapContentHeight()
                                .weight(1f)
                        ) {
                            Text(
                                text = stringResource(id = R.string.get_preset_for_free_title),
                                color = Color(0xFF0A0A0F),
                                fontFamily = RobotoFont,
                                fontSize = 0.042.sw,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = stringResource(id = R.string.get_preset_for_free_subtitle),
                                color = Color(0xFF78787B),
                                fontFamily = RobotoFont,
                                fontSize = 0.032.sw,
                                fontWeight = FontWeight.Normal
                            )
                        }
                        Image(
                            painter = painterResource(id = R.drawable.ic_arrow_right),
                            contentDescription = null,
                            modifier = Modifier
                                .size(0.12.dw)
                                .padding(0.03.dw)
                        )
                    }
                    Spacer(
                        modifier = Modifier
                            .height(0.02.dw)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .bounceClick {
                                onGetAllPresets()
                            }
                            .background(Color(0xFFFFD112), RoundedCornerShape(0.02.dw))
                            .padding(start = 0.045.dw),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .wrapContentHeight()
                                .weight(1f)
                        ) {
                            Text(
                                text = stringResource(id = R.string.get_all_presets_title),
                                color = Color(0xFF0A0A0F),
                                fontFamily = RobotoFont,
                                fontSize = 0.042.sw,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = stringResource(id = R.string.get_all_presets_subtitle),
                                color = Color(0xFF78787B),
                                fontFamily = RobotoFont,
                                fontSize = 0.032.sw,
                                fontWeight = FontWeight.Normal
                            )
                        }
                        Image(
                            painter = painterResource(id = R.drawable.ic_arrow_right),
                            contentDescription = null,
                            modifier = Modifier
                                .size(0.12.dw)
                                .padding(0.03.dw)
                        )
                    }
                }
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawWithContent {
                clipRect(top = minHeight.toPx()) {
                    this@drawWithContent.drawContent()
                }
            }
    ) {
        Box(
            modifier = Modifier
                .offset(
                    x = animatedX.pxToDp(),
                    y = animatedY.pxToDp()
                )
                .width(animatedWidth)
                .height(animatedHeight)
                .clip(
                    RoundedCornerShape(
                        topStart = 0.04.dw,
                        topEnd = 0.04.dw,
                        bottomStart = 0.04.dw * (1f - animatedValue),
                        bottomEnd = 0.04.dw * (1f - animatedValue)
                    )
                )
                .onGloballyPositioned {

                }
        ) {
            GlideImage(
                model = NetworkModule.coverUrl(clickedPreset.id),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Crop,
                transition = CrossFade,
                failure = placeholder(R.drawable.ic_no_image)
            ) {
                // if no internet try loading the cached lower quality cover-icon
                it.clone()
                    .thumbnail(
                        it
                            .load(NetworkModule.coverIconUrl(clickedPreset.id))
                            .signature(it.signature)
                    )
            }
            Image(
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = null,
                modifier = Modifier
                    .size(0.07.dw)
                    .align(Alignment.TopEnd)
                    .offset(
                        x = (-0.035).dw,
                        y = (0.035).dw
                    )
                    .alpha(0.85f * animatedValue)
                    .bounceClick(onClick = onCloseButtonClick)
            )
        }
    }
}