package com.slaviboy.drumpadmachine.data.room.pad

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Transaction
import androidx.room.Upsert

@Dao
interface PadDao {

    @Transaction
    @Upsert
    suspend fun upsertPads(pads: List<PadEntity>)

    @Delete
    suspend fun deletePad(pad: PadEntity)
}