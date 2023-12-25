package com.slaviboy.drumpadmachine

import android.annotation.SuppressLint
import android.app.Application
import android.database.CursorWindow
import dagger.hilt.android.HiltAndroidApp
import java.lang.reflect.Field

@HiltAndroidApp
class Application : Application() {

    @SuppressLint("DiscouragedPrivateApi")
    override fun onCreate() {
        super.onCreate()

        // increase DB cursor size, as it saves whole data as json string and app crashes
        // TODO: replace json string as DB different schemas, and remove this code
        try {
            val field: Field = CursorWindow::class.java.getDeclaredField("sCursorWindowSize")
            field.isAccessible = true
            field.set(null, 100 * 1024 * 1024) // 100MB is the new size
        } catch (e: Exception) {
        }
    }
}