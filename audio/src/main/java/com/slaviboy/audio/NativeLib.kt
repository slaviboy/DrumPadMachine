package com.slaviboy.audio

class NativeLib {

    /**
     * A native method that is implemented by the 'audio' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'audio' library on application startup.
        init {
            System.loadLibrary("audio")
        }
    }
}