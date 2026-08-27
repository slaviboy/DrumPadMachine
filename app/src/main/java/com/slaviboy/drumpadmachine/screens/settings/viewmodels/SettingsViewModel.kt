package com.slaviboy.drumpadmachine.screens.settings.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slaviboy.drumpadmachine.data.datastore.SettingsRepository
import com.slaviboy.drumpadmachine.enums.MetronomeSound
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _volume: MutableState<Int> = mutableIntStateOf(100) // [0,150]
    val volume: State<Int> = _volume

    private val _reverb: MutableState<Int> = mutableIntStateOf(0) // [0,100]
    val reverb: State<Int> = _reverb

    private val _pan: MutableState<Int> = mutableIntStateOf(0) // [-100,100]
    val pan: State<Int> = _pan

    private val _keepScreenOn: MutableState<Boolean> = mutableStateOf(false)
    val keepScreenOn: State<Boolean> = _keepScreenOn

    private val _hapticFeedback: MutableState<Boolean> = mutableStateOf(true)
    val hapticFeedback: State<Boolean> = _hapticFeedback

    private val _metronomeEnabled: MutableState<Boolean> = mutableStateOf(false)
    val metronomeEnabled: State<Boolean> = _metronomeEnabled

    private val _metronomeVolume: MutableState<Int> = mutableIntStateOf(60) // [0,100]
    val metronomeVolume: State<Int> = _metronomeVolume

    private val _defaultBpm: MutableState<Int> = mutableIntStateOf(120) // [40,240]
    val defaultBpm: State<Int> = _defaultBpm

    private val _metronomeSound: MutableState<MetronomeSound> = mutableStateOf(MetronomeSound.Default)
    val metronomeSound: State<MetronomeSound> = _metronomeSound

    private val _cacheSizeBytes: MutableState<Long> = mutableLongStateOf(0L)
    val cacheSizeBytes: State<Long> = _cacheSizeBytes

    init {
        viewModelScope.launch { settingsRepository.volume.collect { _volume.value = it } }
        viewModelScope.launch { settingsRepository.reverb.collect { _reverb.value = it } }
        viewModelScope.launch { settingsRepository.pan.collect { _pan.value = it } }
        viewModelScope.launch { settingsRepository.keepScreenOn.collect { _keepScreenOn.value = it } }
        viewModelScope.launch { settingsRepository.hapticFeedback.collect { _hapticFeedback.value = it } }
        viewModelScope.launch { settingsRepository.metronomeEnabled.collect { _metronomeEnabled.value = it } }
        viewModelScope.launch { settingsRepository.metronomeVolume.collect { _metronomeVolume.value = it } }
        viewModelScope.launch { settingsRepository.defaultBpm.collect { _defaultBpm.value = it } }
        viewModelScope.launch { settingsRepository.metronomeSound.collect { _metronomeSound.value = it } }
        refreshCacheSize()
    }

    fun setVolume(value: Int) = viewModelScope.launch {
        settingsRepository.setVolume(value)
    }

    fun setReverb(value: Int) = viewModelScope.launch {
        settingsRepository.setReverb(value)
    }

    fun setPan(value: Int) = viewModelScope.launch {
        settingsRepository.setPan(value)
    }

    fun setKeepScreenOn(value: Boolean) = viewModelScope.launch {
        settingsRepository.setKeepScreenOn(value)
    }

    fun setHapticFeedback(value: Boolean) = viewModelScope.launch {
        settingsRepository.setHapticFeedback(value)
    }

    fun resetToDefaults() = viewModelScope.launch {
        settingsRepository.resetToDefaults()
    }

    fun setMetronomeEnabled(value: Boolean) = viewModelScope.launch {
        settingsRepository.setMetronomeEnabled(value)
    }

    fun setMetronomeVolume(value: Int) = viewModelScope.launch {
        settingsRepository.setMetronomeVolume(value)
    }

    fun setDefaultBpm(value: Int) = viewModelScope.launch {
        settingsRepository.setDefaultBpm(value)
    }

    fun setMetronomeSound(sound: MetronomeSound) = viewModelScope.launch {
        settingsRepository.setMetronomeSound(sound)
    }

    fun clearCache() = viewModelScope.launch {
        settingsRepository.clearCache()
        refreshCacheSize()
    }

    private fun refreshCacheSize() = viewModelScope.launch {
        _cacheSizeBytes.value = settingsRepository.getCacheSizeBytes()
    }
}
