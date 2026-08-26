package com.slaviboy.drumpadmachine.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.settingsDataStore by preferencesDataStore(name = "settings")

class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val VOLUME = intPreferencesKey("volume")
        val REVERB = intPreferencesKey("reverb")
        val PAN = intPreferencesKey("pan")
    }

    val volume: Flow<Int> = context.settingsDataStore.data.map { it[Keys.VOLUME] ?: 100 } // [0,150]
    val reverb: Flow<Int> = context.settingsDataStore.data.map { it[Keys.REVERB] ?: 0 } // [0,100]
    val pan: Flow<Int> = context.settingsDataStore.data.map { it[Keys.PAN] ?: 0 } // [-100,100]

    suspend fun setVolume(value: Int) {
        context.settingsDataStore.edit { it[Keys.VOLUME] = value.coerceIn(0, 150) }
    }

    suspend fun setReverb(value: Int) {
        context.settingsDataStore.edit { it[Keys.REVERB] = value.coerceIn(0, 100) }
    }

    suspend fun setPan(value: Int) {
        context.settingsDataStore.edit { it[Keys.PAN] = value.coerceIn(-100, 100) }
    }
}
