package com.slaviboy.drumpadmachine.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.slaviboy.drumpadmachine.data.room.converters.CategoryConverter
import com.slaviboy.drumpadmachine.data.room.converters.PresetConverter

@Database(
    entities = [ConfigEntity::class],
    version = 1
)
@TypeConverters(
    CategoryConverter::class,
    PresetConverter::class
)
abstract class ConfigDatabase : RoomDatabase() {
    abstract val dao: ConfigDao
}