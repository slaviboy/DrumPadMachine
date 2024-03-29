package com.slaviboy.drumpadmachine.screens.presetslist.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.bumptech.glide.integration.compose.CrossFade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.slaviboy.composeunits.dw
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.data.entities.Preset
import com.slaviboy.drumpadmachine.extensions.bounceClick
import com.slaviboy.drumpadmachine.modules.NetworkModule
import com.slaviboy.drumpadmachine.ui.RobotoFont

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PresetCard(
    preset: Preset,
    titleTextSize: TextUnit,
    subtitleTextSize: TextUnit,
    coverSize: Dp,
    modifier: Modifier = Modifier,
    onPresetClick: (x: Float, y: Float, preset: Preset) -> Unit
) {
    var x by remember {
        mutableFloatStateOf(0f)
    }
    var y by remember {
        mutableFloatStateOf(0f)
    }
    Column(
        modifier = modifier
            .width(coverSize)
            .onGloballyPositioned {
                val position = it.positionInRoot()
                x = position.x
                y = position.y
            }
            .bounceClick {
                onPresetClick(x, y, preset)
            }
    ) {
        Box(
            contentAlignment = Alignment.BottomEnd
        ) {
            GlideImage(
                model = NetworkModule.coverIconUrl(preset.id),
                contentDescription = null,
                modifier = Modifier
                    .size(coverSize)
                    .clip(RoundedCornerShape(0.04.dw)),
                transition = CrossFade,
                failure = placeholder(R.drawable.ic_no_image)
            )
            Image(
                painter = painterResource(id = R.drawable.ic_play_button),
                contentDescription = null,
                modifier = Modifier
                    .size(coverSize * 0.18f)
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
            text = preset.name,
            color = Color.White,
            fontFamily = RobotoFont,
            fontSize = titleTextSize,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = preset.author ?: "",
            color = Color.LightGray,
            fontFamily = RobotoFont,
            fontSize = subtitleTextSize,
            fontWeight = FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}