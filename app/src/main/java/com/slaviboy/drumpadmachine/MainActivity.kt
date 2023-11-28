package com.slaviboy.drumpadmachine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.dependency
import com.slaviboy.composeunits.initSize
import com.slaviboy.drumpadmachine.composables.NavGraphs
import com.slaviboy.drumpadmachine.composables.destinations.DrumPadComposableDestination
import com.slaviboy.drumpadmachine.viewmodels.DrumPadViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val drumPadViewModel: DrumPadViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        drumPadViewModel.init(assets)
    }

    override fun onStop() {
        super.onStop()
        drumPadViewModel.terminate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSize()
        setContent {
            val navController = rememberNavController()
            DestinationsNavHost(
                navController = navController,
                navGraph = NavGraphs.root,
                dependenciesContainerBuilder = {
                    dependency(DrumPadComposableDestination) {
                        drumPadViewModel
                    }
                }
            )
        }
    }
}