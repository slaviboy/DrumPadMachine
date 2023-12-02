package com.slaviboy.drumpadmachine.api.entities

data class ConfigApi(
    val categoriesApi: List<CategoryApi>,
    val presetsApi: List<PresetApi>
)