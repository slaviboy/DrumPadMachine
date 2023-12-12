package com.slaviboy.drumpadmachine.api.entities

/**
 *  Preset from API response
 *  {
 *     "id": "253",
 *     "name": "Reaching The Skies",
 *     "author": "Synthferatu",
 *     "price": 10,
 *     "orderBy": "212",
 *     "timestamp": 1700085894
 *     "tags": [
 *       "#new",
 *       "#dubstep",
 *       "#easyplay"
 *      ]
 *   }
 */
data class PresetApi(
    val id: String,
    val name: String,
    val author: String,
    val price: Int,
    val orderBy: String,
    val timestamp: Int,
    val deleted: Boolean,
    val tags: List<String>
)