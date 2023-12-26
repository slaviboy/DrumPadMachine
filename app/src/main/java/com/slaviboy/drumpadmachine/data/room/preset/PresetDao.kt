package com.slaviboy.drumpadmachine.data.room.preset

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Upsert

@Dao
interface PresetDao {

    @Upsert
    suspend fun upsertPreset(preset: PresetEntity)

    @Delete
    suspend fun deletePreset(preset: PresetEntity)
}