package com.slaviboy.drumpadmachine.data.room.preset

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.slaviboy.drumpadmachine.data.room.relations.PresetWithRelations

@Dao
interface PresetDao {

    @Upsert
    suspend fun upsertPreset(preset: PresetEntity)

    @Delete
    suspend fun deletePreset(preset: PresetEntity)

    @Transaction
    @Query("SELECT * FROM  preset WHERE presetId = :presetId")
    fun getPreset(presetId: Long): PresetWithRelations?
}