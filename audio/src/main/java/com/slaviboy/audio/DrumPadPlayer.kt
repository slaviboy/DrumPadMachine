package com.slaviboy.audio

import android.content.res.AssetManager
import android.util.Log
import java.io.IOException

class DrumPadPlayer {

    companion object {
        // Sample attributes
        val NUM_PLAY_CHANNELS: Int = 2  // The number of channels in the player Stream.
                                        // Stereo Playback, set to 1 for Mono playback
        // Sample Buffer IDs
        val BASSDRUM: Int = 0
        val SNAREDRUM: Int = 1
        val CRASHCYMBAL: Int = 2
        val RIDECYMBAL: Int = 3
        val MIDTOM: Int = 4
        val LOWTOM: Int = 5
        val HIHATOPEN: Int = 6
        val HIHATCLOSED: Int = 7

        // initial pan position for each drum sample
        val PAN_BASSDRUM: Float = 0f         // Dead Center
        val PAN_SNAREDRUM: Float = -0.75f    // Mostly Left
        val PAN_CRASHCYMBAL: Float = -0.75f  // Mostly Left
        val PAN_RIDECYMBAL: Float = 1.0f     // Hard Right
        val PAN_MIDTOM: Float = 0.25f        // A little Right
        val PAN_LOWTOM: Float = 0.75f        // Mostly Right
        val PAN_HIHATOPEN: Float = -1.0f     // Hard Left
        val PAN_HIHATCLOSED: Float = -1.0f   // Hard Left

        // Logging Tag
        val TAG: String = "DrumPadPlayer"

        init {
            System.loadLibrary("audio")
        }
    }

    fun setupAudioStream() {
        setupAudioStreamNative(NUM_PLAY_CHANNELS)
    }

    fun startAudioStream() {
        startAudioStreamNative()
    }

    fun teardownAudioStream() {
        teardownAudioStreamNative()
    }

    // asset-based samples
    fun loadWavAssets(assetMgr: AssetManager) {
        loadWavAsset(assetMgr, "01.wav", BASSDRUM, PAN_BASSDRUM)
        loadWavAsset(assetMgr, "02.wav", SNAREDRUM, PAN_SNAREDRUM)
        loadWavAsset(assetMgr, "03.wav", CRASHCYMBAL, PAN_CRASHCYMBAL)
        loadWavAsset(assetMgr, "04.wav", RIDECYMBAL, PAN_RIDECYMBAL)
        loadWavAsset(assetMgr, "05.wav", MIDTOM, PAN_MIDTOM)
        loadWavAsset(assetMgr, "06.wav", LOWTOM, PAN_LOWTOM)
        loadWavAsset(assetMgr, "07.wav", HIHATOPEN, PAN_HIHATOPEN)
        loadWavAsset(assetMgr, "08.wav", HIHATCLOSED, PAN_HIHATCLOSED)
    }

    fun unloadWavAssets() {
        unloadWavAssetsNative()
    }

    private fun loadWavAsset(assetMgr: AssetManager, assetName: String, index: Int, pan: Float) {
        try {
            val assetFD = assetMgr.openFd(assetName)
            val dataStream = assetFD.createInputStream()
            val dataLen = assetFD.getLength().toInt()
            val dataBytes = ByteArray(dataLen)
            dataStream.read(dataBytes, 0, dataLen)
            loadWavAssetNative(dataBytes, index, pan)
            assetFD.close()
        } catch (ex: IOException) {
            Log.i(TAG, "IOException$ex")
        }
    }

    private external fun setupAudioStreamNative(numChannels: Int)
    private external fun startAudioStreamNative()
    private external fun teardownAudioStreamNative()

    private external fun loadWavAssetNative(wavBytes: ByteArray, index: Int, pan: Float)
    private external fun unloadWavAssetsNative()

    external fun trigger(drumIndex: Int)
    external fun stopTrigger(drumIndex: Int)

    external fun setPan(index: Int, pan: Float)
    external fun getPan(index: Int): Float

    external fun setGain(index: Int, gain: Float)
    external fun getGain(index: Int): Float

    external fun getOutputReset() : Boolean
    external fun clearOutputReset()

    external fun restartStream()
}
