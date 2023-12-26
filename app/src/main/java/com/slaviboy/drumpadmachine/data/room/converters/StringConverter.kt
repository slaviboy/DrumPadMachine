package com.slaviboy.drumpadmachine.data.room.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object StringConverter {

    @TypeConverter
    fun stringToStringList(value: String?): List<String> {
        if (value == null) {
            return listOf()
        }
        val listType = object : TypeToken<List<String?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun stringListToString(list: List<String?>?): String? {
        if (list == null) {
            return null
        }
        return Gson().toJson(list)
    }
}