package com.slaviboy.drumpadmachine.screens.presets.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slaviboy.drumpadmachine.data.entities.Preset
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PresetsViewModel @Inject constructor(
) : ViewModel() {

    private val _presetsState: MutableState<MutableList<Preset>> = mutableStateOf(mutableListOf())
    private val _filteredPresetsState: MutableState<MutableList<Preset>> = mutableStateOf(mutableListOf())
    val filteredPresetsState: State<MutableList<Preset>> = _filteredPresetsState

    private val _searchTextState: MutableState<String> = mutableStateOf("")
    val searchTextState: State<String> = _searchTextState

    fun changeText(text: String) {
        _searchTextState.value = text
        search()
    }

    fun search() = viewModelScope.launch {
        if (_searchTextState.value.isEmpty()) {
            _filteredPresetsState.value = _presetsState.value
            return@launch
        }
        val list = mutableListOf<Preset>()
        _presetsState.value.forEach {
            if (it.name.contains(_searchTextState.value)) {
                list.add(it)
            }
        }
        _filteredPresetsState.value = list
    }
}