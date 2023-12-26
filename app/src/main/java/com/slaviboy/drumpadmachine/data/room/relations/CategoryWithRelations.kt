package com.slaviboy.drumpadmachine.data.room.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.slaviboy.drumpadmachine.data.room.category.CategoryEntity
import com.slaviboy.drumpadmachine.data.room.filter.FilterEntity

data class CategoryWithRelations(
    @Embedded
    val owner: CategoryEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    val filter: FilterEntity
)