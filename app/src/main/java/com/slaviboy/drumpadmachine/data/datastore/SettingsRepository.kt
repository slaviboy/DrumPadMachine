package com.slaviboy.drumpadmachine.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

private val Context.settingsDataStore by preferencesDataStore(name = "settings")

class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val VOLUME = intPreferencesKey("volume")
        val REVERB = intPreferencesKey("reverb")
        val PAN = intPreferencesKey("pan")
        val KEEP_SCREEN_ON = booleanPreferencesKey("keep_screen_on")
        val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
        val METRONOME_ENABLED = booleanPreferencesKey("metronome_enabled")
        val METRONOME_VOLUME = intPreferencesKey("metronome_volume")
        val DEFAULT_BPM = intPreferencesKey("default_bpm")
    }

    val volume: Flow<Int> = context.settingsDataStore.data.map { it[Keys.VOLUME] ?: 100 } // [0,150]
    val reverb: Flow<Int> = context.settingsDataStore.data.map { it[Keys.REVERB] ?: 0 } // [0,100]
    val pan: Flow<Int> = context.settingsDataStore.data.map { it[Keys.PAN] ?: 0 } // [-100,100]
    val keepScreenOn: Flow<Boolean> = context.settingsDataStore.data.map { it[Keys.KEEP_SCREEN_ON] ?: false }
    val hapticFeedback: Flow<Boolean> = context.settingsDataStore.data.map { it[Keys.HAPTIC_FEEDBACK] ?: true }
    val metronomeEnabled: Flow<Boolean> = context.settingsDataStore.data.map { it[Keys.METRONOME_ENABLED] ?: false }
    val metronomeVolume: Flow<Int> = context.settingsDataStore.data.map { it[Keys.METRONOME_VOLUME] ?: 60 } // [0,100]
    val defaultBpm: Flow<Int> = context.settingsDataStore.data.map { it[Keys.DEFAULT_BPM] ?: 120 } // [40,240]

    suspend fun setVolume(value: Int) {
        context.settingsDataStore.edit { it[Keys.VOLUME] = value.coerceIn(0, 150) }
    }

    suspend fun setReverb(value: Int) {
        context.settingsDataStore.edit { it[Keys.REVERB] = value.coerceIn(0, 100) }
    }

    suspend fun setPan(value: Int) {
        context.settingsDataStore.edit { it[Keys.PAN] = value.coerceIn(-100, 100) }
    }

    suspend fun setKeepScreenOn(value: Boolean) {
        context.settingsDataStore.edit { it[Keys.KEEP_SCREEN_ON] = value }
    }

    suspend fun setHapticFeedback(value: Boolean) {
        context.settingsDataStore.edit { it[Keys.HAPTIC_FEEDBACK] = value }
    }

    suspend fun setMetronomeEnabled(value: Boolean) {
        context.settingsDataStore.edit { it[Keys.METRONOME_ENABLED] = value }
    }

    suspend fun setMetronomeVolume(value: Int) {
        context.settingsDataStore.edit { it[Keys.METRONOME_VOLUME] = value.coerceIn(0, 100) }
    }

    suspend fun setDefaultBpm(value: Int) {
        context.settingsDataStore.edit { it[Keys.DEFAULT_BPM] = value.coerceIn(40, 240) }
    }

    suspend fun resetToDefaults() {
        context.settingsDataStore.edit {
            it[Keys.VOLUME] = 100
            it[Keys.REVERB] = 0
            it[Keys.PAN] = 0
        }
    }

    /** Size, in bytes, of the downloaded-preset audio and config ZIP extraction caches. */
    suspend fun getCacheSizeBytes(): Long = withContext(Dispatchers.IO) {
        cacheDirs().sumOf { dir -> dir.walkTopDown().filter { it.isFile }.sumOf { it.length() } }
    }

    suspend fun clearCache() = withContext(Dispatchers.IO) {
        cacheDirs().forEach { it.deleteRecursively() }
    }

    private fun cacheDirs(): List<File> = listOf(
        File(context.cacheDir, "audio"),
        File(context.cacheDir, "config")
    )
}
