package com.slaviboy.drumpadmachine.screens.home.viewmodels

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.api.results.Result
import com.slaviboy.drumpadmachine.core.entities.BaseItem
import com.slaviboy.drumpadmachine.data.MenuItem
import com.slaviboy.drumpadmachine.data.entities.Config
import com.slaviboy.drumpadmachine.data.entities.Preset
import com.slaviboy.drumpadmachine.data.workers.StoreDatabaseWorker
import com.slaviboy.drumpadmachine.events.ErrorEvent
import com.slaviboy.drumpadmachine.events.NavigationEvent
import com.slaviboy.drumpadmachine.extensions.containsString
import com.slaviboy.drumpadmachine.global.allTrue
import com.slaviboy.drumpadmachine.network.ConnectivityObserver
import com.slaviboy.drumpadmachine.network.NetworkConnectivityObserver
import com.slaviboy.drumpadmachine.screens.home.usecases.DownloadAudioZipUseCase
import com.slaviboy.drumpadmachine.screens.home.usecases.GetConfigUseCase
import com.slaviboy.drumpadmachine.screens.home.usecases.GetPresetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val downloadAudioZipUseCase: DownloadAudioZipUseCase,
    private val getConfigUseCase: GetConfigUseCase,
    private val getPresetUseCase: GetPresetUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _categoriesMapState: MutableState<HashMap<String, MutableList<Preset>>> = mutableStateOf(hashMapOf())
    private val _filteredCategoriesMapState: MutableState<HashMap<String, MutableList<Preset>>> = mutableStateOf(hashMapOf())
    val filteredCategoriesMapState: State<HashMap<String, MutableList<Preset>>> = _filteredCategoriesMapState

    private val _audioConfigState: MutableState<Result<Config>> = mutableStateOf(Result.Initial)
    val audioConfigState: State<Result<Config>> = _audioConfigState

    private val _presetIdState: MutableState<Result<Long>> = mutableStateOf(Result.Initial)
    val presetIdState: State<Result<Long>> = _presetIdState

    private val navigationEventChannel = Channel<NavigationEvent>()
    val navigationEventFlow = navigationEventChannel.receiveAsFlow()

    private val errorEventChannel = Channel<ErrorEvent>()
    val errorEventFlow = errorEventChannel.receiveAsFlow()

    private val _noItemsState: MutableState<BaseItem?> = mutableStateOf(null)
    val noItemsState: State<BaseItem?> = _noItemsState

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
        getConfig()
        initNetworkListener()
    }

    private var downloadJob: Job? = null
    private var getPreset: Job? = null

    fun getSoundForFree(presetId: Long?) {
        presetId ?: return
        if (_presetIdState.value.isLoadingOrSuccess()) return
        downloadJob?.cancel()
        downloadJob = viewModelScope.launch {
            downloadAudioZipUseCase.execute(presetId).collect {
                if (it is Result.Success) {
                    getPresetById(presetId)
                }
                if (it is Result.Loading) {
                    _presetIdState.value = it
                }
                if (it is Result.Fail) {
                    errorEventChannel.send(
                        ErrorEvent.ErrorWithMessage(it.errorMessage)
                    )
                    _presetIdState.value = it
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

    private fun initNetworkListener() = viewModelScope.launch {
        NetworkConnectivityObserver(context).observe().collectLatest {
            if (it == ConnectivityObserver.Status.Available && hasNetworkError()) {
                getConfig()
            }
        }
    }

    private fun search() = viewModelScope.launch {
        if (_searchTextState.value.isEmpty()) {
            _filteredCategoriesMapState.value = _categoriesMapState.value
            setNoItemEvent()
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
        setNoItemEvent()
    }

    private fun hasNetworkError(): Boolean {
        val isEmpty = _filteredCategoriesMapState.value.isEmpty()
        return (_audioConfigState.value is Result.Fail && isEmpty)
    }

    private fun setNoItemEvent() {
        val isEmpty = _filteredCategoriesMapState.value.isEmpty()
        _noItemsState.value = if (hasNetworkError()) {
            BaseItem(
                iconResId = R.drawable.ic_no_internet,
                titleResId = R.string.no_items,
                subtitleResId = R.string.please_check_your_network
            )
        } else if (audioConfigState.value is Result.Success && isEmpty) {
            BaseItem(
                iconResId = R.drawable.ic_audio,
                titleResId = R.string.no_items,
                subtitleResId = R.string.there_are_no_items_found
            )
        } else {
            null
        }
    }

    private fun getConfig() = viewModelScope.launch {
        getConfigUseCase.execute(CONFIG_VERSION).collect {
            _audioConfigState.value = it
            setNoItemEvent()
            if (it is Result.Success) {
                val config = it.data
                if (config.presets?.firstOrNull()?.files?.isNotEmpty() == true) {
                    val data = Data.Builder().apply {
                        putInt("CONFIG_VERSION", CONFIG_VERSION)
                    }
                    val storeDatabaseWorker = OneTimeWorkRequestBuilder<StoreDatabaseWorker>()
                        .setInputData(data.build())
                        .build()
                    WorkManager.getInstance(context)
                        .enqueueUniqueWork("store_database_worker", ExistingWorkPolicy.KEEP, storeDatabaseWorker)
                }
                setConfig(config)
            }
            if (it is Result.Fail) {
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

    private fun getPresetById(presetId: Long) {
        getPreset?.cancel()
        getPreset = viewModelScope.launch {

            // on first launch use full Preset object
            val preset = _categoriesMapState.value.firstNotNullOfOrNull {
                it.value.firstOrNull {
                    allTrue(
                        it.id == presetId,
                        it.files != null,
                        it.lessons != null
                    )
                }
            }
            if (preset != null) {
                navigationEventChannel.send(
                    NavigationEvent.NavigateToDrumPadScreen(preset)
                )
                _presetIdState.value = Result.Success(presetId)
                return@launch
            }

            // on any upcoming launches, use short Preset - without *{files} *{lessons} fields
            getPresetUseCase.execute(presetId).collect {
                if (it is Result.Success) {
                    navigationEventChannel.send(
                        NavigationEvent.NavigateToDrumPadScreen(it.data)
                    )
                    _presetIdState.value = Result.Success(presetId)
                }
                if (it is Result.Fail) {
                    errorEventChannel.send(
                        ErrorEvent.ErrorWithMessage(it.errorMessage)
                    )
                    _presetIdState.value = Result.Fail(it.errorMessage)
                }
            }
        }
    }

    fun resetPresetIdState() {
        _presetIdState.value = Result.Initial
    }

    fun cancelDownload() = viewModelScope.launch {
        downloadJob?.cancel()
        getPreset?.cancel()
    }

    fun setInitPresetStatus() {
        _presetIdState.value = Result.Initial
    }

    companion object {
        const val CONFIG_VERSION = 12
    }
}