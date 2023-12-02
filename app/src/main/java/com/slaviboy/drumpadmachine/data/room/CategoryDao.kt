package com.slaviboy.drumpadmachine.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Upsert
    suspend fun upsertCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    @Query("SELECT * FROM CategoryEntity WHERE title = :title")
    fun getCategoryByTitle(title: String): Flow<CategoryEntity>
}