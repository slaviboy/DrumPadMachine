package com.slaviboy.drumpadmachine.screens.presets.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slaviboy.drumpadmachine.api.results.Result
import com.slaviboy.drumpadmachine.data.entities.Preset
import com.slaviboy.drumpadmachine.events.ErrorEvent
import com.slaviboy.drumpadmachine.events.NavigationEvent
import com.slaviboy.drumpadmachine.extensions.containsString
import com.slaviboy.drumpadmachine.screens.home.usecases.DownloadAudioZipUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PresetsViewModel @Inject constructor(
    private val downloadAudioZipUseCase: DownloadAudioZipUseCase
) : ViewModel() {

    private val _presetsState: MutableState<MutableList<Preset>> = mutableStateOf(mutableListOf())
    private val _filteredPresetsState: MutableState<MutableList<Preset>> = mutableStateOf(mutableListOf())
    val filteredPresetsState: State<MutableList<Preset>> = _filteredPresetsState

    private val _searchTextState: MutableState<String> = mutableStateOf("")
    val searchTextState: State<String> = _searchTextState

    private val navigationEventChannel = Channel<NavigationEvent>()
    val navigationEventFlow = navigationEventChannel.receiveAsFlow()

    private val errorEventChannel = Channel<ErrorEvent>()
    val errorEventFlow = errorEventChannel.receiveAsFlow()

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
            if (it.name.containsString(_searchTextState.value)) {
                list.add(it)
            }
        }
        _filteredPresetsState.value = list
    }

    fun initPresets(presets: Array<Preset>) {
        _presetsState.value = presets.toMutableList()
        search()
    }

    fun getSoundForFree(presetId: Int?) {
        presetId ?: return
        viewModelScope.launch {
            downloadAudioZipUseCase.execute(presetId).collect {
                if (it is Result.Success) {
                    val preset = getPresetById(presetId)
                    if (preset != null) {
                        navigationEventChannel.send(
                            NavigationEvent.NavigateToDrumPadScreen(preset = preset)
                        )
                    } else {
                        errorEventChannel.send(
                            ErrorEvent.ErrorWithMessage("Unable to find Preset!")
                        )
                    }
                }
                if (it is Result.Error) {
                    errorEventChannel.send(
                        ErrorEvent.ErrorWithMessage(it.errorMessage)
                    )
                }
            }
        }
    }

    fun unlockAllSounds() {
    }

    private fun getPresetById(presetId: Int): Preset? {
        return _presetsState.value.firstOrNull { it.id == presetId }
    }
}