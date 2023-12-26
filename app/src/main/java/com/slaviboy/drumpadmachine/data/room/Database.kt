package com.slaviboy.drumpadmachine.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.slaviboy.drumpadmachine.data.room.category.CategoryDao
import com.slaviboy.drumpadmachine.data.room.category.CategoryEntity
import com.slaviboy.drumpadmachine.data.room.config.ConfigDao
import com.slaviboy.drumpadmachine.data.room.config.ConfigEntity
import com.slaviboy.drumpadmachine.data.room.converters.StringConverter
import com.slaviboy.drumpadmachine.data.room.preset.PresetDao
import com.slaviboy.drumpadmachine.data.room.preset.PresetEntity

@Database(
    entities = [
        CategoryEntity::class,
        ConfigEntity::class,
        PresetEntity::class
    ],
    version = 1
)

@TypeConverters(
    StringConverter::class
)
abstract class Database : RoomDatabase() {
    abstract val categoryDao: CategoryDao
    abstract val configDao: ConfigDao
    abstract val presetDao: PresetDao
}