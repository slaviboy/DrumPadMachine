package com.slaviboy.drumpadmachine.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface ConfigDao {

    @Upsert
    suspend fun upsertConfig(config: ConfigEntity)

    @Delete
    suspend fun deleteConfig(config: ConfigEntity)

    @Query("SELECT * FROM ConfigEntity WHERE id = 0")
    fun getConfig(): ConfigEntity?
}