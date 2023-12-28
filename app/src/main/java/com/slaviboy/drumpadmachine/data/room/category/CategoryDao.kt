package com.slaviboy.drumpadmachine.data.room.category

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Upsert

@Dao
interface CategoryDao {

    @Upsert
    suspend fun upsertCategories(category: List<CategoryEntity>)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)
}