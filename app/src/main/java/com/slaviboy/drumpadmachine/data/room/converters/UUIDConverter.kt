package com.slaviboy.drumpadmachine.data.room.converters

import androidx.room.TypeConverter
import com.slaviboy.drumpadmachine.data.helpers.DatabaseHelper.DEFAULT_UUID
import java.util.UUID

class UUIDConverter {

    @TypeConverter
    fun fromUUID(uuid: UUID): String {
        if (uuid.toString() == DEFAULT_UUID) {
            return UUID.randomUUID().toString()
        }
        return uuid.toString()
    }

    @TypeConverter
    fun uuidFromString(string: String?): UUID {
        string?.let {
            return UUID.fromString(string)
        }
        return UUID.fromString(DEFAULT_UUID)
    }
}