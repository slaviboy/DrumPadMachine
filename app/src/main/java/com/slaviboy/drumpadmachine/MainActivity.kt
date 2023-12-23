package com.slaviboy.drumpadmachine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.MemoryCategory
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import com.ramcosta.composedestinations.navigation.dependency
import com.slaviboy.composeunits.initSize
import com.slaviboy.drumpadmachine.extensions.hideSystemBars
import com.slaviboy.drumpadmachine.screens.NavGraphs
import com.slaviboy.drumpadmachine.screens.destinations.DrumPadComposableDestination
import com.slaviboy.drumpadmachine.screens.destinations.HomeComposableDestination
import com.slaviboy.drumpadmachine.screens.destinations.PresetsListComposableDestination
import com.slaviboy.drumpadmachine.screens.drumpad.viewmodels.DrumPadViewModel
import com.slaviboy.drumpadmachine.screens.home.composables.HomeComposable
import com.slaviboy.drumpadmachine.screens.home.viewmodels.HomeViewModel
import com.slaviboy.drumpadmachine.screens.presetslist.composables.PresetsListComposable
import com.slaviboy.drumpadmachine.screens.presetslist.viewmodels.PresetsListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

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
            val scaffoldState = remember { SnackbarHostState() }
            val snackbarCoroutineScope = rememberCoroutineScope()

            val onError: (error: String) -> Unit = {
                snackbarCoroutineScope.launch {
                    scaffoldState.showSnackbar(
                        message = it,
                        actionLabel = "Close",
                        duration = SnackbarDuration.Long
                    )
                }
            }

            Scaffold(
                snackbarHost = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        SnackbarHost(scaffoldState) { data ->
                            Snackbar(
                                actionColor = Color.White,
                                contentColor = Color.White,
                                containerColor = Color(0xFFF11F67),
                                snackbarData = data
                            )
                        }
                    }
                },
                content = {
                    DestinationsNavHost(
                        modifier = Modifier
                            .padding(it),
                        navController = navController,
                        navGraph = NavGraphs.root,
                        dependenciesContainerBuilder = {
                            dependency(DrumPadComposableDestination) {
                                hiltViewModel<DrumPadViewModel>()
                            }
                        }
                    ) {
                        composable(HomeComposableDestination) {
                            HomeComposable(
                                navigator = destinationsNavigator,
                                homeViewModel = hiltViewModel<HomeViewModel>(),
                                onError = onError
                            )
                        }
                        composable(PresetsListComposableDestination) {
                            PresetsListComposable(
                                navigator = destinationsNavigator,
                                presetsListViewModel = hiltViewModel<PresetsListViewModel>(),
                                onError = onError,
                                name = navArgs.name,
                                presets = navArgs.presets
                            )
                        }
                    }
                }
            )
        }
    }
}