package com.slaviboy.drumpadmachine.screens.presets.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.api.results.Result
import com.slaviboy.drumpadmachine.data.entities.Preset
import com.slaviboy.drumpadmachine.events.ErrorEvent
import com.slaviboy.drumpadmachine.events.NavigationEvent
import com.slaviboy.drumpadmachine.extensions.containsString
import com.slaviboy.drumpadmachine.screens.home.usecases.DownloadAudioZipUseCase
import com.slaviboy.drumpadmachine.screens.home.viewmodels.BaseItem
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

    private val _noItemsState: MutableState<BaseItem?> = mutableStateOf(null)
    val noItemsState: State<BaseItem?> = _noItemsState

    private val _presetIdState: MutableState<Result<Int>> = mutableStateOf(Result.Initial)
    val presetIdState: State<Result<Int>> = _presetIdState

    init {
        _presetIdState.value = Result.Initial
    }

    fun changeText(text: String) {
        _searchTextState.value = text
        search()
    }

    fun initPresets(presets: Array<Preset>) {
        _presetsState.value = presets.toMutableList()
        search()
    }

    fun getSoundForFree(presetId: Int?) {
        presetId ?: return
        viewModelScope.launch {
            downloadAudioZipUseCase.execute(presetId).collect {
                _presetIdState.value = it
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
                if (it is Result.Fail) {
                    errorEventChannel.send(
                        ErrorEvent.ErrorWithMessage(it.errorMessage)
                    )
                }
            }
        }
    }

    fun unlockAllSounds() {
    }

    private fun search() = viewModelScope.launch {
        if (_searchTextState.value.isEmpty()) {
            _filteredPresetsState.value = _presetsState.value
            setNoItemEvent()
            return@launch
        }
        val list = mutableListOf<Preset>()
        _presetsState.value.forEach {
            if (it.name.containsString(_searchTextState.value)) {
                list.add(it)
            }
        }
        _filteredPresetsState.value = list
        setNoItemEvent()
    }

    private fun setNoItemEvent() {
        val isEmpty = _filteredPresetsState.value.isEmpty()
        _noItemsState.value = if (isEmpty) {
            BaseItem(
                iconResId = R.drawable.ic_audio,
                titleResId = R.string.no_items,
                subtitleResId = R.string.there_are_no_items_found
            )
        } else {
            null
        }
    }

    private fun getPresetById(presetId: Int): Preset? {
        return _presetsState.value.firstOrNull { it.id == presetId }
    }
}