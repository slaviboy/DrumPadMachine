package com.slaviboy.drumpadmachine.modules

import android.content.Context
import com.google.gson.Gson
import com.slaviboy.drumpadmachine.api.repositories.ApiRepository
import com.slaviboy.drumpadmachine.data.room.ConfigDao
import com.slaviboy.drumpadmachine.screens.home.usecases.DownloadAudioZipUseCase
import com.slaviboy.drumpadmachine.screens.home.usecases.GetPresetsConfigUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {

    @Provides
    @ViewModelScoped
    fun provideDownloadAudioZipUseCase(
        repository: ApiRepository,
        @ApplicationContext context: Context
    ): DownloadAudioZipUseCase {
        return DownloadAudioZipUseCase(repository, context)
    }

    @Provides
    @ViewModelScoped
    fun provideGetPresetsConfigUseCase(
        repository: ApiRepository,
        dao: ConfigDao,
        gson: Gson,
        @ApplicationContext context: Context
    ): GetPresetsConfigUseCase {
        return GetPresetsConfigUseCase(repository, dao, gson, context)
    }

    @Provides
    @ViewModelScoped
    fun provideApplicationContext(@ApplicationContext context: Context): Context {
        return context
    }
}