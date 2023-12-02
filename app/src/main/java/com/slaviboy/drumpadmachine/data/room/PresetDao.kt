package com.slaviboy.drumpadmachine.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PresetDao {

    @Upsert
    suspend fun upsertPreset(preset: PresetEntity)

    @Delete
    suspend fun deletePreset(preset: PresetEntity)

    @Query("SELECT * FROM PresetEntity WHERE id = :id")
    fun getPresetById(id: Int): Flow<PresetEntity>
}