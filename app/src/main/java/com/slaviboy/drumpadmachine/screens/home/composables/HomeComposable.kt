package com.slaviboy.drumpadmachine.screens.home.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.slaviboy.drumpadmachine.screens.home.viewmodels.HomeViewModel

@RootNavGraph(start = true)
@Destination
@Composable
fun HomeComposable(
    homeViewModel: HomeViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red)
            .clickable {
                homeViewModel.downloadAudioZip()
            }
    ) {

    }
}