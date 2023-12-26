package com.slaviboy.drumpadmachine.data.room.file

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Upsert

@Dao
interface FileDao {

    @Upsert
    suspend fun upsertFile(category: FileEntity)

    @Delete
    suspend fun deleteFile(category: FileEntity)
}