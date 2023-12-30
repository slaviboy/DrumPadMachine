package com.slaviboy.drumpadmachine.data.room.preset

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.slaviboy.drumpadmachine.data.room.relations.PresetWithRelations

@Dao
interface PresetDao {

    @Transaction
    @Upsert
    suspend fun upsertPresets(preset: List<PresetEntity>)

    @Transaction
    @Query("SELECT * FROM  preset WHERE presetId = :presetId")
    fun getPreset(presetId: Long): PresetWithRelations?

    @Query("DELETE FROM preset")
    fun deleteAll()
}