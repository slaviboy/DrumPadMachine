package com.slaviboy.drumpadmachine.modules

import android.content.Context
import androidx.room.Room
import com.slaviboy.drumpadmachine.data.room.category.CategoryDao
import com.slaviboy.drumpadmachine.data.room.category.CategoryDatabase
import com.slaviboy.drumpadmachine.data.room.config.ConfigDao
import com.slaviboy.drumpadmachine.data.room.config.ConfigDatabase
import com.slaviboy.drumpadmachine.data.room.preset.PresetDao
import com.slaviboy.drumpadmachine.data.room.preset.PresetDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    fun provideConfigDatabase(
        @ApplicationContext context: Context
    ): ConfigDatabase {
        return Room.databaseBuilder(
            context,
            ConfigDatabase::class.java,
            "config.db"
        ).build()
    }

    @Provides
    fun provideConfigDao(
        configDatabase: ConfigDatabase
    ): ConfigDao {
        return configDatabase.dao
    }

    @Provides
    fun provideCategoryDatabase(
        @ApplicationContext context: Context
    ): CategoryDatabase {
        return Room.databaseBuilder(
            context,
            CategoryDatabase::class.java,
            "category.db"
        ).build()
    }

    @Provides
    fun provideCategoryDao(
        categoryDatabase: CategoryDatabase
    ): CategoryDao {
        return categoryDatabase.dao
    }

    @Provides
    fun providePresetDatabase(
        @ApplicationContext context: Context
    ): PresetDatabase {
        return Room.databaseBuilder(
            context,
            PresetDatabase::class.java,
            "preset.db"
        ).build()
    }

    @Provides
    fun providePresetDao(
        presetDatabase: PresetDatabase
    ): PresetDao {
        return presetDatabase.dao
    }
}