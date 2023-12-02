package com.slaviboy.drumpadmachine.data.room.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.slaviboy.drumpadmachine.data.entities.Category

object CategoryConverter {

    @TypeConverter
    fun stringToCategoryList(value: String?): List<Category> {
        if (value == null) {
            return arrayListOf()
        }
        val listType = object : TypeToken<List<Category?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun categoryListToString(list: List<Category>): String {
        return Gson().toJson(list)
    }
}