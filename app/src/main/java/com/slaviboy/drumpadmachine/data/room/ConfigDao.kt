package com.slaviboy.drumpadmachine.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ConfigDao {

    @Upsert
    suspend fun upsertConfig(config: ConfigEntity)

    @Delete
    suspend fun deleteConfig(config: ConfigEntity)

    @Query("SELECT * FROM ConfigEntity WHERE id = :id")
    fun getConfigById(id: Int = 0): Flow<ConfigEntity>
}