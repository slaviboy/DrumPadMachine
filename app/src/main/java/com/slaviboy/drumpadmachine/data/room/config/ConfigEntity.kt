package com.slaviboy.drumpadmachine.data.room.config

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "config")
data class ConfigEntity(

    @PrimaryKey
    val id: UUID = UUID.randomUUID()
)