package com.slaviboy.drumpadmachine.data.room.config

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ConfigEntity(

    @PrimaryKey(autoGenerate = false)
    val id: Long = 0
)