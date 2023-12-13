package com.slaviboy.audio

import android.content.res.AssetManager
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class DrumPadPlayer(
    private val numberOfRows: Int = 4,
    private val numberOfColumns: Int = 3
) {
    companion object {
        const val NUM_PLAY_CHANNELS: Int = 2  // The number of channels in the player Stream.

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

    fun loadWavFile(
        dirPath: String,
        pans: List<Float>? = null // [-1,1]
    ) {
        val directory = File(dirPath)
        val files = directory.listFiles() ?: return
        for (i in files.indices) {
            val dataBytes = getByteArrayFromWavFile(files[i].absolutePath) ?: return
            loadWavAssetNative(dataBytes, i, pans?.getOrNull(i) ?: 0f)
        }
    }

    fun loadWavAssets(
        assetManager: AssetManager,
        dirPath: String = "default_audio",
        pans: List<Float>? = null // [-1,1]
    ) {
        val listFilePaths = try {
            assetManager.list(dirPath) ?: arrayOf()
        } catch (e: IOException) {
            arrayOf()
        }
        listFilePaths.forEachIndexed { i, path ->
            loadWavAsset(assetManager, "$dirPath/$path", i, pans?.getOrNull(i) ?: 0f)
        }
    }

    fun unloadWavAssets() {
        unloadWavAssetsNative()
    }

    fun trigger(page: Int, row: Int, column: Int) {
        val i = (page * numberOfColumns * numberOfRows) + row * numberOfColumns + column
        trigger(i)
    }

    private fun getByteArrayFromWavFile(filePath: String): ByteArray? {
        return try {
            val file = File(filePath)
            val inputStream = FileInputStream(file)
            val dataLen = file.length().toInt()
            val byteBuffer = ByteArray(dataLen)
            inputStream.read(byteBuffer, 0, dataLen)
            inputStream.close()
            byteBuffer
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
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
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private external fun setupAudioStreamNative(numChannels: Int)
    private external fun startAudioStreamNative()
    private external fun teardownAudioStreamNative()

    private external fun loadWavAssetNative(wavBytes: ByteArray, index: Int, pan: Float)
    private external fun unloadWavAssetsNative()

    external fun trigger(index: Int)
    external fun stopTrigger(index: Int)

    external fun setPan(index: Int, pan: Float)
    external fun getPan(index: Int): Float

    external fun setGain(index: Int, gain: Float)
    external fun getGain(index: Int): Float

    external fun getOutputReset(): Boolean
    external fun clearOutputReset()

    external fun restartStream()
}
