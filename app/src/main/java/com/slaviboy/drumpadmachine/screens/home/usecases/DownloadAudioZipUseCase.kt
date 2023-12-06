package com.slaviboy.drumpadmachine.screens.home.usecases

import com.slaviboy.drumpadmachine.api.repositories.ApiRepository
import com.slaviboy.drumpadmachine.api.results.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipInputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadAudioZipUseCase @Inject constructor(
    private val repository: ApiRepository
) {
    suspend fun execute(cacheDir: File, presetId: Int): Flow<Result<Int>> = flow {
        val response = repository.getAudioZipById(presetId)
        if (!response.isSuccessful) {
            emit(Result.Error("Failed to download ZIP file"))
        }
        val path = File(cacheDir, "audio/$presetId/")
        val tempFile = File(path, "temp.zip")
        try {
            tempFile.getParentFile()?.mkdirs()
            tempFile.createNewFile()
            response.body()?.byteStream()?.use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                    extractZip(tempFile, path)
                    tempFile.delete()
                    emit(Result.Success(presetId))
                }
            } ?: run {
                emit(Result.Error("Empty response body"))
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }.flowOn(Dispatchers.IO)

    private fun extractZip(zipFile: File, outputDir: File): Boolean {
        return try {
            ZipInputStream(FileInputStream(zipFile)).use { zipInputStream ->
                var zipEntry = zipInputStream.nextEntry
                while (zipEntry != null) {
                    val entryFile = File(outputDir, zipEntry.name)
                    if (zipEntry.isDirectory) {
                        entryFile.mkdirs()
                    } else {
                        FileOutputStream(entryFile).use { fileOutputStream ->
                            zipInputStream.copyTo(fileOutputStream)
                        }
                    }
                    zipInputStream.closeEntry()
                    zipEntry = zipInputStream.nextEntry
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}