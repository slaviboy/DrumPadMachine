package com.slaviboy.drumpadmachine.modules

import android.content.Context
import com.slaviboy.drumpadmachine.api.repositories.ApiRepository
import com.slaviboy.drumpadmachine.screens.home.usecases.DownloadAudioZipUseCase
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
        repository: ApiRepository
    ): DownloadAudioZipUseCase {
        return DownloadAudioZipUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideApplicationContext(@ApplicationContext context: Context): Context {
        return context
    }
}