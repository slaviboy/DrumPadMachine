package com.slaviboy.drumpadmachine.screens.lessonslist.viewmodels

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.core.entities.BaseItem
import com.slaviboy.drumpadmachine.data.entities.Lesson
import com.slaviboy.drumpadmachine.data.entities.Preset
import com.slaviboy.drumpadmachine.events.ErrorEvent
import com.slaviboy.drumpadmachine.extensions.containsString
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LessonsListViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _lessonsState: MutableState<List<Lesson>> = mutableStateOf(listOf())
    private val _filteredLessonsState: MutableState<List<Lesson>> = mutableStateOf(listOf())
    val filteredLessonsState: State<List<Lesson>> = _filteredLessonsState

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
        val list = mutableListOf<Lesson>()
        _lessonsState.value.forEach {
            val name = context.getString(R.string.lessons_number).format(it.id + 1)
            if (name.containsString(_searchTextState.value)) {
                list.add(it)
            }
        }
        _filteredLessonsState.value = list
        setNoItemEvent()
    }

    private fun setNoItemEvent() {
        val isEmpty = _filteredLessonsState.value.isEmpty()
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

    fun init(preset: Preset) {
        val lesson = preset.lessons ?: return
        _lessonsState.value = lesson
        search()
    }
}