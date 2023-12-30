package com.slaviboy.drumpadmachine.data.room.converters

import androidx.room.TypeConverter
import com.slaviboy.drumpadmachine.data.entities.LessonState

class LessonStateConverter {

    @TypeConverter
    fun fromPriority(priority: LessonState): String {
        return priority.name
    }

    @TypeConverter
    fun toPriority(priority: String): LessonState {
        return LessonState.valueOf(priority)
    }
}