package com.slaviboy.drumpadmachine.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.slaviboy.drumpadmachine.data.room.category.CategoryDao
import com.slaviboy.drumpadmachine.data.room.category.CategoryEntity
import com.slaviboy.drumpadmachine.data.room.config.ConfigDao
import com.slaviboy.drumpadmachine.data.room.config.ConfigEntity
import com.slaviboy.drumpadmachine.data.room.converters.LessonStateConverter
import com.slaviboy.drumpadmachine.data.room.converters.StringConverter
import com.slaviboy.drumpadmachine.data.room.converters.UUIDConverter
import com.slaviboy.drumpadmachine.data.room.file.FileDao
import com.slaviboy.drumpadmachine.data.room.file.FileEntity
import com.slaviboy.drumpadmachine.data.room.filter.FilterDao
import com.slaviboy.drumpadmachine.data.room.filter.FilterEntity
import com.slaviboy.drumpadmachine.data.room.lesson.LessonDao
import com.slaviboy.drumpadmachine.data.room.lesson.LessonEntity
import com.slaviboy.drumpadmachine.data.room.pad.PadDao
import com.slaviboy.drumpadmachine.data.room.pad.PadEntity
import com.slaviboy.drumpadmachine.data.room.preset.PresetDao
import com.slaviboy.drumpadmachine.data.room.preset.PresetEntity

@Database(
    entities = [
        CategoryEntity::class,
        ConfigEntity::class,
        PresetEntity::class,
        FilterEntity::class,
        FileEntity::class,
        LessonEntity::class,
        PadEntity::class
    ],
    version = 1
)
@TypeConverters(
    StringConverter::class,
    UUIDConverter::class,
    LessonStateConverter::class
)
abstract class Database : RoomDatabase() {
    abstract val categoryDao: CategoryDao
    abstract val configDao: ConfigDao
    abstract val presetDao: PresetDao
    abstract val filterDao: FilterDao
    abstract val fileDao: FileDao
    abstract val lessonDao: LessonDao
    abstract val padDao: PadDao
}