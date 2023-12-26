package com.slaviboy.drumpadmachine

import android.annotation.SuppressLint
import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Application : Application() {

    @SuppressLint("DiscouragedPrivateApi")
    override fun onCreate() {
        super.onCreate()
    }
}