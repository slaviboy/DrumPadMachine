package com.slaviboy.drumpadmachine.extensions

import android.app.ActivityManager
import android.content.Context

@Suppress("DEPRECATION")
inline fun <reified T> Context.isServiceRunning(): Boolean {
    return (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
        .getRunningServices(Integer.MAX_VALUE)
        .any {
            it.service.className == T::class.java.name
        }
}