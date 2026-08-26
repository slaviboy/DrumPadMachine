package com.slaviboy.drumpadmachine.screens.settings.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slaviboy.drumpadmachine.data.datastore.SettingsRepository
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

    init {
        viewModelScope.launch { settingsRepository.volume.collect { _volume.value = it } }
        viewModelScope.launch { settingsRepository.reverb.collect { _reverb.value = it } }
        viewModelScope.launch { settingsRepository.pan.collect { _pan.value = it } }
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
}
