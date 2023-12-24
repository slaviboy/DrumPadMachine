package com.slaviboy.drumpadmachine.screens.lessonslist.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slaviboy.drumpadmachine.data.entities.Preset
import com.slaviboy.drumpadmachine.events.ErrorEvent
import com.slaviboy.drumpadmachine.extensions.containsString
import com.slaviboy.drumpadmachine.screens.home.viewmodels.BaseItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LessonsListViewModel @Inject constructor(
) : ViewModel() {

    private val _lessonsState: MutableState<HashMap<String, MutableList<Preset>>> = mutableStateOf(hashMapOf())
    private val _filteredLessonsState: MutableState<HashMap<String, MutableList<Preset>>> = mutableStateOf(hashMapOf())
    val filteredLessonsState: State<HashMap<String, MutableList<Preset>>> = _filteredLessonsState

    private val errorEventChannel = Channel<ErrorEvent>()
    val errorEventFlow = errorEventChannel.receiveAsFlow()

    private val _noItemsState: MutableState<BaseItem?> = mutableStateOf(null)
    val noItemsState: State<BaseItem?> = _noItemsState

    private val _searchTextState: MutableState<String> = mutableStateOf("")
    val searchTextState: State<String> = _searchTextState

    fun changeText(text: String) {
        _searchTextState.value = text
        search()
    }

    private fun search() = viewModelScope.launch {
        if (_searchTextState.value.isEmpty()) {
            _filteredLessonsState.value = _lessonsState.value
            setNoItemEvent()
            return@launch
        }
        val hashMap = HashMap<String, MutableList<Preset>>()
        _lessonsState.value.forEach {
            val (key, value) = it
            value.forEach {
                if (it.name.containsString(_searchTextState.value)) {
                    hashMap.getOrPut(key) {
                        mutableListOf()
                    }.add(it)
                }
            }
        }
        _filteredLessonsState.value = hashMap
        setNoItemEvent()
    }

    private fun setNoItemEvent() {
        val isEmpty = _filteredLessonsState.value.isEmpty()
        /*_noItemsState.value = if (_audioConfigState.value is Result.Fail && isEmpty) {
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
        }*/
    }
}