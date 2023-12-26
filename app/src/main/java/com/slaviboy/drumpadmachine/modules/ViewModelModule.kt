package com.slaviboy.drumpadmachine.modules

import android.content.Context
import com.google.gson.Gson
import com.slaviboy.drumpadmachine.api.repositories.ApiRepository
import com.slaviboy.drumpadmachine.data.room.config.ConfigDao
import com.slaviboy.drumpadmachine.dispatchers.DefaultDispatchers
import com.slaviboy.drumpadmachine.dispatchers.Dispatchers
import com.slaviboy.drumpadmachine.screens.home.usecases.DownloadAudioZipUseCase
import com.slaviboy.drumpadmachine.screens.home.usecases.DownloadAudioZipUseCaseImpl
import com.slaviboy.drumpadmachine.screens.home.usecases.GetPresetsConfigUseCase
import com.slaviboy.drumpadmachine.screens.home.usecases.GetPresetsConfigUseCaseImpl
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
    fun provideApplicationContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    fun provideDispatchers(): Dispatchers {
        return DefaultDispatchers()
    }

    @Provides
    @ViewModelScoped
    fun provideDownloadAudioZipUseCase(
        repository: ApiRepository,
        @ApplicationContext context: Context,
        dispatchers: Dispatchers
    ): DownloadAudioZipUseCase {
        return DownloadAudioZipUseCaseImpl(repository, context, dispatchers)
    }

    @Provides
    @ViewModelScoped
    fun provideGetPresetsConfigUseCase(
        repository: ApiRepository,
        dao: ConfigDao,
        gson: Gson,
        @ApplicationContext context: Context,
        dispatchers: Dispatchers
    ): GetPresetsConfigUseCase {
        return GetPresetsConfigUseCaseImpl(repository, dao, gson, context, dispatchers)
    }
}