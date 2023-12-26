package com.slaviboy.drumpadmachine.data.room.category

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.slaviboy.drumpadmachine.data.helpers.DatabaseHelper
import java.util.UUID

@Entity
data class CategoryEntity(

    @PrimaryKey
    val id: UUID = DatabaseHelper.defaultUUID,

    val configId: UUID = DatabaseHelper.defaultUUID,
    val title: String
)