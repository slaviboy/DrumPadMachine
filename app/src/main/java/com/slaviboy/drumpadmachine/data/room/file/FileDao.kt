package com.slaviboy.drumpadmachine.data.room.file

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert

@Dao
interface FileDao {

    @Transaction
    @Upsert
    suspend fun upsertFiles(category: List<FileEntity>)

    @Query("DELETE FROM file")
    fun deleteAll()
}