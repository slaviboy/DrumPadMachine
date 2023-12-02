package com.slaviboy.drumpadmachine.data.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ConfigEntity::class],
    version = 1
)
abstract class ConfigDatabase : RoomDatabase() {
    abstract val dao: ConfigDao
}