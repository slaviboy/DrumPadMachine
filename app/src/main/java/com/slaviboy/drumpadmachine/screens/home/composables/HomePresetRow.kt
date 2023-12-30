package com.slaviboy.drumpadmachine.screens.home.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.slaviboy.composeunits.dw
import com.slaviboy.composeunits.sw
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.data.entities.Preset
import com.slaviboy.drumpadmachine.extensions.bounceClick
import com.slaviboy.drumpadmachine.screens.presetslist.composables.PresetCard
import com.slaviboy.drumpadmachine.ui.RobotoFont

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomePresetRow(
    lazyItemScope: LazyItemScope,
    categoryName: String,
    presets: List<Preset>?,
    onPresetClick: (x: Float, y: Float, preset: Preset) -> Unit,
    onSeeAllClick: (categoryName: String) -> Unit
) = with(lazyItemScope) {
    presets ?: return@with
    Row(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(horizontal = 0.04.dw),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = categoryName,
            color = Color.White,
            fontFamily = RobotoFont,
            fontSize = 0.06.sw,
            fontWeight = FontWeight.Black
        )
        Text(
            text = stringResource(id = R.string.see_all).uppercase(),
            color = Color.White,
            fontFamily = RobotoFont,
            fontSize = 0.032.sw,
            fontWeight = FontWeight.Black,
            modifier = Modifier
                .bounceClick {
                    onSeeAllClick(categoryName)
                }
        )
    }
    Spacer(
        modifier = Modifier
            .height(0.04.dw)
    )
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        items(presets.size) { j ->
            val preset = presets[j]
            PresetCard(
                preset = preset,
                titleTextSize = 0.035.sw,
                subtitleTextSize = 0.023.sw,
                coverSize = 0.35.dw,
                onPresetClick = onPresetClick,
                modifier = Modifier
                    .offset(x = 0.04.dw)
                    .animateItemPlacement()
            )
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