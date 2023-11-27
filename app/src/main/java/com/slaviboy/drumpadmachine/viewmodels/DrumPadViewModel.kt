package com.slaviboy.drumpadmachine.viewmodels

import android.content.res.AssetManager
import androidx.lifecycle.ViewModel
import com.slaviboy.audio.DrumPadPlayer

class DrumPadViewModel : ViewModel() {

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