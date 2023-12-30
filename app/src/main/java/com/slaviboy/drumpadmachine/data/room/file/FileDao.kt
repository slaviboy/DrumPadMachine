package com.slaviboy.drumpadmachine.data.room.file

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Transaction
import androidx.room.Upsert

@Dao
interface FileDao {

    @Transaction
    @Upsert
    suspend fun upsertFiles(category: List<FileEntity>)

    @Delete
    suspend fun deleteFile(category: FileEntity)
}