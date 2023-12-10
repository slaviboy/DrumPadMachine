package com.slaviboy.drumpadmachine.screens.presets.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.slaviboy.composeunits.dw
import com.slaviboy.drumpadmachine.composables.SearchTextField
import com.slaviboy.drumpadmachine.screens.presets.viewmodels.PresetsViewModel
import com.slaviboy.drumpadmachine.ui.backgroundGradientBottom
import com.slaviboy.drumpadmachine.ui.backgroundGradientTop

@Destination
@Composable
fun PresetsComposable(
    navigator: DestinationsNavigator,
    presetsViewModel: PresetsViewModel,
    categoryName: String,
    onError: (error: String) -> Unit
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
        LazyColumn(
            modifier = Modifier
                .padding(bottom = 0.18.dw)
        ) {
            item {
                Spacer(
                    modifier = Modifier
                        .height(0.05.dw)
                )
                SearchTextField(
                    text = presetsViewModel.searchTextState.value,
                    onTextChange = {
                        presetsViewModel.changeText(it)
                    },
                    onSearchButtonClick = {
                        presetsViewModel.search()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 0.04.dw)
                )
                Spacer(
                    modifier = Modifier
                        .height(0.04.dw)
                )
            }
        }
    }
}