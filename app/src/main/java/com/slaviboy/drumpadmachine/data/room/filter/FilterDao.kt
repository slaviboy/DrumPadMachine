package com.slaviboy.drumpadmachine.data.room.filter

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Upsert

@Dao
interface FilterDao {

    @Upsert
    suspend fun upsertFilters(filter: List<FilterEntity>)

    @Delete
    suspend fun deleteFilter(filter: FilterEntity)
}