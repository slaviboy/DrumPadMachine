package com.slaviboy.drumpadmachine.data.room.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.slaviboy.drumpadmachine.data.entities.Preset

object PresetConverter {

    @TypeConverter
    fun stringToPresetList(value: String?): List<Preset> {
        if (value == null) {
            return arrayListOf()
        }
        val listType = object : TypeToken<List<Preset?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun presetListToString(list: List<Preset>): String {
        return Gson().toJson(list)
    }
}