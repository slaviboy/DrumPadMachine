package com.slaviboy.drumpadmachine.data.room.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.slaviboy.drumpadmachine.data.room.category.CategoryEntity
import com.slaviboy.drumpadmachine.data.room.config.ConfigEntity
import com.slaviboy.drumpadmachine.data.room.preset.PresetEntity

data class ConfigWithRelation(
    @Embedded
    val owner: ConfigEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "configId"
    )
    val categories: List<CategoryEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "configId"
    )
    val presets: List<PresetEntity>
)