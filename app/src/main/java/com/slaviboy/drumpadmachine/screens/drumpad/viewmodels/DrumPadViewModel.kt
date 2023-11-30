package com.slaviboy.drumpadmachine.screens.drumpad.viewmodels

import android.content.res.AssetManager
import androidx.lifecycle.ViewModel
import com.slaviboy.audio.DrumPadPlayer
import com.slaviboy.drumpadmachine.api.repositories.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DrumPadViewModel @Inject constructor(
    private val repository: ApiRepository
) : ViewModel() {
    private var mDrumPadPlayer = DrumPadPlayer()

    fun init(assets: AssetManager) {
        mDrumPadPlayer.setupAudioStream()
        mDrumPadPlayer.loadWavAssets(assets)
        mDrumPadPlayer.startAudioStream()
    }

    fun terminate() {
        mDrumPadPlayer.teardownAudioStream()
        mDrumPadPlayer.unloadWavAssets()
    }

    fun playSound(row: Int, column: Int) {
        mDrumPadPlayer.trigger(DrumPadPlayer.RIDECYMBAL)
    }
}