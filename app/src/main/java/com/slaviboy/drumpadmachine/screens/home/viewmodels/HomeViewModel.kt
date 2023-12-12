package com.slaviboy.drumpadmachine.screens.home.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.api.results.Result
import com.slaviboy.drumpadmachine.data.MenuItem
import com.slaviboy.drumpadmachine.data.entities.Config
import com.slaviboy.drumpadmachine.data.entities.Preset
import com.slaviboy.drumpadmachine.events.ErrorEvent
import com.slaviboy.drumpadmachine.events.NavigationEvent
import com.slaviboy.drumpadmachine.extensions.containsString
import com.slaviboy.drumpadmachine.screens.home.usecases.DownloadAudioZipUseCase
import com.slaviboy.drumpadmachine.screens.home.usecases.GetPresetsConfigUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val downloadAudioZipUseCase: DownloadAudioZipUseCase,
    private val getPresetsConfigUseCase: GetPresetsConfigUseCase
) : ViewModel() {

    private val _categoriesMapState: MutableState<HashMap<String, MutableList<Preset>>> = mutableStateOf(hashMapOf())
    private val _filteredCategoriesMapState: MutableState<HashMap<String, MutableList<Preset>>> = mutableStateOf(hashMapOf())
    val filteredCategoriesMapState: State<HashMap<String, MutableList<Preset>>> = _filteredCategoriesMapState

    private val _audioConfigState: MutableState<Result<Config>> = mutableStateOf(Result.Initial)
    val audioConfigState: State<Result<Config>> = _audioConfigState

    private val navigationEventChannel = Channel<NavigationEvent>()
    val navigationEventFlow = navigationEventChannel.receiveAsFlow()

    private val errorEventChannel = Channel<ErrorEvent>()
    val errorEventFlow = errorEventChannel.receiveAsFlow()

    private val _menuItemsState: MutableState<List<MenuItem>> = mutableStateOf(
        listOf(
            MenuItem(
                isSelected = true,
                iconResId = R.drawable.ic_home,
                titleResId = R.string.main
            ),
            MenuItem(
                isSelected = false,
                iconResId = R.drawable.ic_vinyl,
                titleResId = R.string.my_music
            ),
            MenuItem(
                isSelected = false,
                iconResId = R.drawable.ic_more,
                titleResId = R.string.more
            )
        )
    )
    val menuItemsState: State<List<MenuItem>> = _menuItemsState

    private val _searchTextState: MutableState<String> = mutableStateOf("")
    val searchTextState: State<String> = _searchTextState

    init {
        init()
    }

    fun getSoundForFree(presetId: Int?) {
        presetId ?: return
        viewModelScope.launch {
            downloadAudioZipUseCase.execute(presetId).collect {
                if (it is Result.Success) {
                    navigationEventChannel.send(
                        NavigationEvent.NavigateToDrumPadScreen(presetId)
                    )
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

    fun setMenuItem(menuItem: MenuItem) {
        val menuItemsList = _menuItemsState.value.toMutableList()
        menuItemsList.forEachIndexed { i, item ->
            if (item == menuItem) {
                menuItemsList[i] = item.copy(isSelected = true)
            } else {
                menuItemsList[i] = item.copy(isSelected = false)
            }
        }
        _menuItemsState.value = menuItemsList
    }

    fun changeText(text: String) {
        _searchTextState.value = text
        search()
    }

    fun search() = viewModelScope.launch {
        if (_searchTextState.value.isEmpty()) {
            _filteredCategoriesMapState.value = _categoriesMapState.value
            return@launch
        }
        val hashMap = HashMap<String, MutableList<Preset>>()
        _categoriesMapState.value.forEach {
            val (key, value) = it
            value.forEach {
                if (it.name.containsString(_searchTextState.value)) {
                    hashMap.getOrPut(key) {
                        mutableListOf()
                    }.add(it)
                }
            }
        }
        _filteredCategoriesMapState.value = hashMap
    }

    private fun init() = viewModelScope.launch {
        getPresetsConfigUseCase.execute(PRESETS_VERSION).collect {
            _audioConfigState.value = it
            if (it is Result.Success) {
                setConfig(it.data)
            }
            if (it is Result.Error) {
                errorEventChannel.send(
                    ErrorEvent.ErrorWithMessage(it.errorMessage)
                )
            }
        }
    }

    private fun setConfig(config: Config) = viewModelScope.launch {
        val categories = config.categories
        val presets = config.presets
        val hashMap = HashMap<String, MutableList<Preset>>()
        categories?.forEach {
            val categoryName = it.title
            val categoryTags = it.filter.tags
            presets?.forEach {
                val presetTags = it.tags
                val containsAnyTag = presetTags?.any {
                    it in categoryTags
                } ?: false
                if (containsAnyTag) {
                    hashMap.getOrPut(categoryName) {
                        mutableListOf()
                    }.add(it)
                }
            }
        }
        _categoriesMapState.value = hashMap
        search()
    }

    companion object {
        const val PRESETS_VERSION = 12
    }
}