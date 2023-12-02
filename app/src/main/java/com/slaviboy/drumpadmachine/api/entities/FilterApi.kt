package com.slaviboy.drumpadmachine.api.entities

/**
 * Filter from API response
 *  "tags": [
 *     "#house",
 *     "#basshouse"
 *  ]
 */
data class FilterApi(
    val tags: List<String>
)