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
import androidx.compose.runtime.remember
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.slaviboy.composeunits.DeviceHeight
import com.slaviboy.composeunits.DeviceWidth
import com.slaviboy.composeunits.Density
import com.slaviboy.composeunits.ScaleDensity
import com.slaviboy.composeunits.dh
import com.slaviboy.composeunits.dw
import com.slaviboy.composeunits.sw
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.composables.CachedAsyncImage
import com.slaviboy.drumpadmachine.composables.LoadingBox
import com.slaviboy.drumpadmachine.data.entities.Preset
import com.slaviboy.drumpadmachine.extensions.bounceClick
import com.slaviboy.drumpadmachine.extensions.click
import com.slaviboy.drumpadmachine.extensions.pxToDp
import com.slaviboy.drumpadmachine.modules.NetworkModule
import com.slaviboy.drumpadmachine.ui.RobotoFont

@Composable
fun HomePresetDetails(
    boxScope: BoxScope,
    animatedValue: Float,
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
    val cardCornerRadius = 0.043.dw
    // small downward overlap so the growing cover image always fully covers the
    // card's white background at the seam, even with sub-pixel rounding differences
    val coverBottomOverlap = 2.dp
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
                .align(Alignment.Center)
                .onGloballyPositioned {
                    val position = it.positionInWindow()
                    onGloballyPositioned(position.x, position.y)
                }
        ) {
            // Only the button area is painted white. The top area is covered
            // exclusively by the animated cover image as it grows into place,
            // so no white background is ever exposed behind it mid-transition.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.18.dh)
                    .align(Alignment.BottomCenter)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(bottomStart = cardCornerRadius, bottomEnd = cardCornerRadius)
                    )
            )
            if (isLoading) {
                LoadingBox(
                    boxScope = this,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.18.dh)
                        .align(Alignment.BottomCenter)
                        .padding(
                            start = 0.03.dw,
                            end = 0.03.dw,
                            top = 0.03.dw,
                            bottom = 0.03.dw
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
                            start = 0.03.dw,
                            end = 0.03.dw,
                            top = 0.03.dw,
                            bottom = 0.03.dw
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
                .height(animatedHeight + coverBottomOverlap)
                .clip(
                    RoundedCornerShape(
                        topStart = cardCornerRadius,
                        topEnd = cardCornerRadius,
                        bottomStart = cardCornerRadius * (1f - animatedValue),
                        bottomEnd = cardCornerRadius * (1f - animatedValue)
                    )
                )
                .onGloballyPositioned {

                }
        ) {
            val context = LocalContext.current
            // Already-cached (from the preset list/card) lower quality cover-icon, kept mounted
            // as a permanent base layer underneath - never swapped out - so there's no blank
            // cover during the open transition. The full cover fades in on top of it once
            // loaded; if it fails outright (e.g. no internet), this icon just stays visible.
            CachedAsyncImage(
                model = NetworkModule.coverIconUrl(clickedPreset.id),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.ic_no_image)
            )
            SubcomposeAsyncImage(
                model = remember(clickedPreset.id) {
                    ImageRequest.Builder(context)
                        .data(NetworkModule.coverUrl(clickedPreset.id))
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .crossfade(true)
                        .build()
                },
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            ) {
                if (painter.state is AsyncImagePainter.State.Success) {
                    SubcomposeAsyncImageContent()
                }
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

private val previewPreset = Preset(
    id = 1,
    name = "Beautiful Vibes",
    author = "Slaviboy",
    price = 100,
    orderBy = "1",
    timestamp = null,
    deleted = false,
    hasInfo = true,
    tempo = 150,
    tags = listOf("#dubstep"),
    files = null,
    lessons = null
)

@Preview(showBackground = true, backgroundColor = 0xFF232339)
@Composable
private fun HomePresetDetailsPreview() {
    DeviceWidth = 1080f
    DeviceHeight = 2400f
    Density = 3f
    ScaleDensity = 3f
    Box(modifier = Modifier.fillMaxSize()) {
        HomePresetDetails(
            boxScope = this,
            animatedValue = 1f,
            animatedWidth = 0.76.dw,
            animatedHeight = 0.33.dh,
            animatedX = 0f,
            animatedY = 0f,
            clickedPreset = previewPreset,
            isLoading = false,
            onGloballyPositioned = { _, _ -> },
            onGetPresetForFree = {},
            onGetAllPresets = {},
            onCloseButtonClick = {}
        )
    }
}