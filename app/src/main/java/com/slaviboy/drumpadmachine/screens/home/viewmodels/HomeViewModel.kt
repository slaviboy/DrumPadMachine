package com.slaviboy.drumpadmachine.screens.home.viewmodels

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.api.results.Result
import com.slaviboy.drumpadmachine.data.entities.Preset
import com.slaviboy.drumpadmachine.screens.home.usecases.DownloadAudioZipUseCase
import com.slaviboy.drumpadmachine.screens.home.usecases.GetAudioConfigUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MenuItem(
    val isSelected: Boolean = false,
    @DrawableRes val iconResId: Int,
    @StringRes val titleResId: Int
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val downloadAudioZipUseCase: DownloadAudioZipUseCase,
    private val getAudioConfigUseCase: GetAudioConfigUseCase,
    private val context: Context
) : ViewModel() {

    private val _categoriesMapState: MutableState<HashMap<String, MutableList<Preset>>> = mutableStateOf(hashMapOf())
    val categoriesMapState: State<HashMap<String, MutableList<Preset>>> = _categoriesMapState

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

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getAudioConfigUseCase.execute().collect {
                when (it) {
                    is Result.Loading -> {

                    }

                    is Result.Success -> {
                        val categories = it.data.categories
                        val presets = it.data.presets
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

                        viewModelScope.launch(Dispatchers.Main) {
                            // _categoriesMapState.value = hashMap
                            _categoriesMapState.value = hashMapOf(
                                "Test" to mutableListOf(presets!![0]),
                                "WA" to mutableListOf(presets!![1], presets!![2], presets!![3], presets!![4], presets!![5])
                            )
                        }
                    }

                    is Result.Error -> {

                    }
                }
            }
        }
    }

    fun downloadAudioZip() {
        viewModelScope.launch(Dispatchers.IO) {
            downloadAudioZipUseCase.execute(context.cacheDir, 11).collect {
                val b = 3
            }
            getAudioConfigUseCase.execute().collect {
                val b = 3
            }
        }
    }
}