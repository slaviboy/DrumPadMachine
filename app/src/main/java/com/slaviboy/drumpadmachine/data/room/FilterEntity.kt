package com.slaviboy.drumpadmachine.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.slaviboy.drumpadmachine.api.entities.CategoryApi
import com.slaviboy.drumpadmachine.api.entities.PresetApi

@Entity
data class FilterEntity(
    val tags: List<String>
)