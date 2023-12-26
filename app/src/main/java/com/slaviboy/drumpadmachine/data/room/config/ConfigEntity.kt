package com.slaviboy.drumpadmachine.data.room.config

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.slaviboy.drumpadmachine.data.helpers.DatabaseHelper
import java.util.UUID

@Entity
data class ConfigEntity(

    @PrimaryKey
    val id: UUID = DatabaseHelper.defaultUUID
)