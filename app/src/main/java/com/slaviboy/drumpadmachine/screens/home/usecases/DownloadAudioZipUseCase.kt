package com.slaviboy.drumpadmachine.screens.home.usecases

import android.content.Context
import com.slaviboy.drumpadmachine.api.repositories.ApiRepository
import com.slaviboy.drumpadmachine.api.results.Result
import com.slaviboy.drumpadmachine.dispatchers.Dispatchers
import com.slaviboy.drumpadmachine.screens.home.helpers.ZipHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

interface DownloadAudioZipUseCase {
    suspend fun execute(presetId: Int): Flow<Result<Int>>
}

@Singleton
class DownloadAudioZipUseCaseImpl @Inject constructor(
    private val repository: ApiRepository,
    private val context: Context,
    private val dispatchers: Dispatchers
) : DownloadAudioZipUseCase {

    override suspend fun execute(presetId: Int): Flow<Result<Int>> = flow {
        try {
            val response = repository.getAudioZipById(presetId)
            if (!response.isSuccessful) {
                emit(Result.Error("Failed to download ZIP file"))
                return@flow
            }
            val path = File(context.cacheDir, "audio/$presetId/")
            val tempFile = File(path, "temp_audio.zip")
            tempFile.getParentFile()?.mkdirs()
            tempFile.createNewFile()
            response.body()?.byteStream()?.use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                    ZipHelper.extractZip(tempFile, path)
                    tempFile.delete()
                    emit(Result.Success(presetId))
                }
            } ?: run {
                emit(Result.Error("Empty response body"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error!"))
        }
    }.flowOn(dispatchers.io)
}