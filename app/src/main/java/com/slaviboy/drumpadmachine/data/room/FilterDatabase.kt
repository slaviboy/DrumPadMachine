package com.slaviboy.drumpadmachine.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.slaviboy.drumpadmachine.data.room.converters.StringArrayConverter

@Database(
    entities = [FilterEntity::class],
    version = 1
)
@TypeConverters(StringArrayConverter::class)
abstract class FilterDatabase : RoomDatabase() {
    abstract val dao: FilterDao
}