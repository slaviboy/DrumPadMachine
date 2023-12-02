package com.slaviboy.drumpadmachine.api.entities

import com.google.gson.annotations.SerializedName

/**
 * Categories from API response
 * @example
 *   {
 *      "title": "House",
 *      "filter": {...}
 *   }
 */
data class CategoryApi(
    val title: String,
    @SerializedName("filter") val filterApi: FilterApi
)