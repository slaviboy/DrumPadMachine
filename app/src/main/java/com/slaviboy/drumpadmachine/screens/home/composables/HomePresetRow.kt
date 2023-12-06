package com.slaviboy.drumpadmachine.screens.home.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.bumptech.glide.integration.compose.CrossFade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.slaviboy.composeunits.dw
import com.slaviboy.composeunits.sw
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.data.entities.Preset
import com.slaviboy.drumpadmachine.extensions.bounceClick
import com.slaviboy.drumpadmachine.modules.NetworkModule
import com.slaviboy.drumpadmachine.ui.RobotoFont

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun HomePresetRow(
    lazyItemScope: LazyItemScope,
    categoryName: String,
    presets: List<Preset>?,
    onClick: (x: Float, y: Float, preset: Preset) -> Unit
) = with(lazyItemScope) {
    presets ?: return@with
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
            text = stringResource(id = R.string.see_all).uppercase(),
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
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        items(presets.size) { j ->
            val preset = presets[j]
            var x by remember {
                mutableFloatStateOf(0f)
            }
            var y by remember {
                mutableFloatStateOf(0f)
            }
            Column(
                modifier = Modifier
                    .offset(x = 0.03.dw)
                    .onGloballyPositioned {
                        val position = it.positionInRoot()
                        x = position.x
                        y = position.y
                    }
                    .bounceClick {
                        onClick(x, y, preset)
                    }
            ) {
                Box(
                    contentAlignment = Alignment.BottomEnd
                ) {
                    GlideImage(
                        model = NetworkModule.coverIconUrl(preset.id),
                        contentDescription = null,
                        modifier = Modifier
                            .size(0.35.dw)
                            .clip(RoundedCornerShape(0.04.dw)),
                        transition = CrossFade,
                        failure = placeholder(R.drawable.ic_no_image)
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
                Spacer(
                    modifier = Modifier
                        .height(0.02.dw)
                )
                Text(
                    text = preset.name ?: "",
                    color = Color.White,
                    fontFamily = RobotoFont,
                    fontSize = 0.035.sw,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = preset.author ?: "",
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