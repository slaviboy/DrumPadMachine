package com.slaviboy.drumpadmachine.data.room.filter

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.slaviboy.drumpadmachine.data.helpers.DatabaseHelper
import java.util.UUID

@Entity
data class FilterEntity(

    @PrimaryKey
    val id: UUID = UUID.randomUUID(),

    val categoryId: UUID = DatabaseHelper.defaultUUID,
    val tags: List<String>
)