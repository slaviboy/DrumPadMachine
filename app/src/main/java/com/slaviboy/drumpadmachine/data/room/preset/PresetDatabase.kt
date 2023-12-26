package com.slaviboy.drumpadmachine.data.room.preset

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PresetEntity::class],
    version = 1
)
abstract class PresetDatabase : RoomDatabase() {
    abstract val dao: PresetDao
}