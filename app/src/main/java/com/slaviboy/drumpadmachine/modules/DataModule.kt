package com.slaviboy.drumpadmachine.modules

import android.content.Context
import androidx.room.Room
import com.slaviboy.drumpadmachine.data.room.Database
import com.slaviboy.drumpadmachine.data.room.category.CategoryDao
import com.slaviboy.drumpadmachine.data.room.config.ConfigDao
import com.slaviboy.drumpadmachine.data.room.preset.PresetDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

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