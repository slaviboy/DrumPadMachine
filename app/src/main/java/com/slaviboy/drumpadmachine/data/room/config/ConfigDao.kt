package com.slaviboy.drumpadmachine.data.room.config

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.slaviboy.drumpadmachine.data.room.relations.ConfigWithRelations

@Dao
interface ConfigDao {

    @Transaction
    @Upsert
    suspend fun upsertConfig(config: ConfigEntity)

    @Delete
    suspend fun deleteConfig(config: ConfigEntity)

    @Transaction
    @Query("SELECT * FROM config LIMIT 1")
    fun getConfig(): ConfigWithRelations?
}