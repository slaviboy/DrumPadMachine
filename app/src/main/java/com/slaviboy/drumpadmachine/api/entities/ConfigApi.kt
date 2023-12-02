package com.slaviboy.drumpadmachine.api.entities

import com.google.gson.annotations.SerializedName

data class ConfigApi(
    @SerializedName("categories") val categoriesApi: List<CategoryApi>,
    @SerializedName("presets") val presetsApi: List<PresetApi>
)