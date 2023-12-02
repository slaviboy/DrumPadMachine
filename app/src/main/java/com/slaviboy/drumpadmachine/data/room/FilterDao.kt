package com.slaviboy.drumpadmachine.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Upsert

@Dao
interface FilterDao {

    @Upsert
    suspend fun upsertFilter(filter: FilterEntity)

    @Delete
    suspend fun deleteFilter(filter: FilterEntity)

}