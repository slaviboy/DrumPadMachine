package com.slaviboy.drumpadmachine.data.room.category

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert

@Dao
interface CategoryDao {

    @Transaction
    @Upsert
    suspend fun upsertCategories(category: List<CategoryEntity>)

    @Query("DELETE FROM category")
    fun deleteAll()
}