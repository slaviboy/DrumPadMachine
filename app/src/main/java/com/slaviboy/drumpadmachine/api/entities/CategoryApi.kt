package com.slaviboy.drumpadmachine.api.entities

import com.google.gson.annotations.SerializedName

data class CategoryApi(

    val title: String,

    @SerializedName("filter")
    val filterApi: FilterApi
)