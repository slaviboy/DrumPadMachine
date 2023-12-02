package com.slaviboy.drumpadmachine.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ConfigEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val categoriesApi: List<CategoryEntity>,
    val presetsApi: List<PresetEntity>
)