package com.slaviboy.drumpadmachine.data.room.filter

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Transaction
import androidx.room.Upsert

@Dao
interface FilterDao {

    @Transaction
    @Upsert
    suspend fun upsertFilters(filter: List<FilterEntity>)

    @Delete
    suspend fun deleteFilter(filter: FilterEntity)
}