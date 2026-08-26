package com.slaviboy.drumpadmachine.modules

import android.content.Context
import com.google.gson.Gson
import com.slaviboy.drumpadmachine.api.repositories.ApiRepository
import com.slaviboy.drumpadmachine.data.room.config.ConfigDao
import com.slaviboy.drumpadmachine.data.room.lesson.LessonDao
import com.slaviboy.drumpadmachine.data.room.preset.PresetDao
import com.slaviboy.drumpadmachine.dispatchers.Dispatchers
import com.slaviboy.drumpadmachine.screens.home.usecases.DownloadAudioZipUseCase
import com.slaviboy.drumpadmachine.screens.home.usecases.DownloadAudioZipUseCaseImpl
import com.slaviboy.drumpadmachine.screens.home.usecases.GetConfigUseCase
import com.slaviboy.drumpadmachine.screens.home.usecases.GetConfigUseCaseImpl
import com.slaviboy.drumpadmachine.screens.home.usecases.GetPresetUseCase
import com.slaviboy.drumpadmachine.screens.home.usecases.GetPresetUseCaseImpl
import com.slaviboy.drumpadmachine.screens.lessonplayer.usecases.SaveLessonResultUseCase
import com.slaviboy.drumpadmachine.screens.lessonplayer.usecases.SaveLessonResultUseCaseImpl
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
    fun provideGetPresetUseCase(
        presetDao: PresetDao,
        dispatchers: Dispatchers
    ): GetPresetUseCase {
        return GetPresetUseCaseImpl(presetDao, dispatchers)
    }

    @Provides
    @ViewModelScoped
    fun provideGetConfigUseCase(
        repository: ApiRepository,
        configDao: ConfigDao,
        gson: Gson,
        @ApplicationContext context: Context,
        dispatchers: Dispatchers
    ): GetConfigUseCase {
        return GetConfigUseCaseImpl(repository, configDao, gson, context, dispatchers)
    }

    @Provides
    @ViewModelScoped
    fun provideSaveLessonResultUseCase(
        presetDao: PresetDao,
        lessonDao: LessonDao,
        dispatchers: Dispatchers
    ): SaveLessonResultUseCase {
        return SaveLessonResultUseCaseImpl(presetDao, lessonDao, dispatchers)
    }
}