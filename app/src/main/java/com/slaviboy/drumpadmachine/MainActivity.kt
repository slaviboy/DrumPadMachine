package com.slaviboy.drumpadmachine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.MemoryCategory
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.dependency
import com.slaviboy.composeunits.initSize
import com.slaviboy.drumpadmachine.extensions.hideSystemBars
import com.slaviboy.drumpadmachine.screens.NavGraphs
import com.slaviboy.drumpadmachine.screens.destinations.DrumPadComposableDestination
import com.slaviboy.drumpadmachine.screens.destinations.HomeComposableDestination
import com.slaviboy.drumpadmachine.screens.drumpad.viewmodels.DrumPadViewModel
import com.slaviboy.drumpadmachine.screens.home.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val drumPadViewModel: DrumPadViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

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
        Glide.get(this).setMemoryCategory(MemoryCategory.HIGH)
        installSplashScreen().apply {
            //setKeepOnScreenCondition { loginViewModel.isLoading }
        }
        hideSystemBars()
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
                    dependency(HomeComposableDestination) {
                        homeViewModel
                    }
                }
            )
        }
    }
}