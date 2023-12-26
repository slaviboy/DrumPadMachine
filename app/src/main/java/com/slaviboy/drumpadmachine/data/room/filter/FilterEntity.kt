package com.slaviboy.drumpadmachine.data.room.filter

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FilterEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long,

    val configId: Long,
    val tags: List<String>
)