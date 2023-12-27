package com.slaviboy.drumpadmachine.data.room.pad

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Upsert

@Dao
interface PadDao {

    @Upsert
    suspend fun upsertPad(pad: PadEntity)

    @Delete
    suspend fun deletePad(pad: PadEntity)
}