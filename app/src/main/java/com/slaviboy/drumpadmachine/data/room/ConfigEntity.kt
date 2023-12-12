package com.slaviboy.drumpadmachine.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.slaviboy.drumpadmachine.data.entities.Category
import com.slaviboy.drumpadmachine.data.entities.Preset
import com.slaviboy.drumpadmachine.data.room.converters.CategoryConverter
import com.slaviboy.drumpadmachine.data.room.converters.PresetConverter

@Entity
data class ConfigEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0,

    @TypeConverters(CategoryConverter::class)
    val categories: List<Category>,

    @TypeConverters(PresetConverter::class)
    val presets: List<Preset>
)