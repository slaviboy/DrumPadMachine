package com.slaviboy.drumpadmachine.api.entities

/**
 * Categories from API response
 * @example
 *   {
 *      "title": "House",
 *      "filter": {}
 *   }
 */
data class CategoryApi(
    val title: String,
    val filterApi: FilterApi
)