package com.slaviboy.drumpadmachine.modules

import android.content.Context
import androidx.room.Room
import com.slaviboy.drumpadmachine.data.room.ConfigDao
import com.slaviboy.drumpadmachine.data.room.ConfigDatabase
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
}