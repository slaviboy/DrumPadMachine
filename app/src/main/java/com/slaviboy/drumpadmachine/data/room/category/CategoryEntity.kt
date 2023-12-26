package com.slaviboy.drumpadmachine.data.room.category

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.slaviboy.drumpadmachine.data.entities.Filter

@Entity
data class CategoryEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long,

    val configId: Long,
    val title: String,
    //val filter: Filter
)