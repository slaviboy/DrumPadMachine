package com.slaviboy.drumpadmachine.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CategoryEntity(
    @PrimaryKey(autoGenerate = false)
    val title: String,
    val filterApi: FilterEntity
)