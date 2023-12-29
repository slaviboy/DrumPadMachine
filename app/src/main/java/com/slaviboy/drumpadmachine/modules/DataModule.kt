package com.slaviboy.drumpadmachine.modules

import android.content.Context
import androidx.room.Room
import com.slaviboy.drumpadmachine.data.room.Database
import com.slaviboy.drumpadmachine.data.room.category.CategoryDao
import com.slaviboy.drumpadmachine.data.room.config.ConfigDao
import com.slaviboy.drumpadmachine.data.room.file.FileDao
import com.slaviboy.drumpadmachine.data.room.filter.FilterDao
import com.slaviboy.drumpadmachine.data.room.lesson.LessonDao
import com.slaviboy.drumpadmachine.data.room.pad.PadDao
import com.slaviboy.drumpadmachine.data.room.preset.PresetDao
import com.slaviboy.drumpadmachine.dispatchers.DefaultDispatchers
import com.slaviboy.drumpadmachine.dispatchers.Dispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    fun provideDispatchers(): Dispatchers {
        return DefaultDispatchers()
    }

    @Provides
    fun provideConfigDao(
        database: Database
    ): ConfigDao {
        return database.configDao
    }

    @Provides
    fun provideCategoryDao(
        database: Database
    ): CategoryDao {
        return database.categoryDao
    }

    @Provides
    fun providePresetDao(
        database: Database
    ): PresetDao {
        return database.presetDao
    }

    @Provides
    fun provideFilterDao(
        database: Database
    ): FilterDao {
        return database.filterDao
    }

    @Provides
    fun provideFileDao(
        database: Database
    ): FileDao {
        return database.fileDao
    }

    @Provides
    fun provideLessonDao(
        database: Database
    ): LessonDao {
        return database.lessonDao
    }

    @Provides
    fun providePadDao(
        database: Database
    ): PadDao {
        return database.padDao
    }

    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ): Database {
        return Room.databaseBuilder(
            context,
            Database::class.java,
            "config.db"
        ).build()
    }
}