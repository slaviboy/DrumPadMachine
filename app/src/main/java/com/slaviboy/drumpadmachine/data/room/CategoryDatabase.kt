package com.slaviboy.drumpadmachine.data.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CategoryEntity::class],
    version = 1
)
abstract class CategoryDatabase : RoomDatabase() {
    abstract val dao: CategoryDao
}