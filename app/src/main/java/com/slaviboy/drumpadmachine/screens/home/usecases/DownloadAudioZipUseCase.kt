package com.slaviboy.drumpadmachine.screens.home.usecases

import com.slaviboy.drumpadmachine.api.repositories.ApiRepository
import com.slaviboy.drumpadmachine.api.results.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadAudioZipUseCase @Inject constructor(
    private val repository: ApiRepository
) {
    suspend fun execute(cacheDir: File, id: Int): Flow<Result<Unit>> = flow {
        val response = repository.getAudioZipById(id)
        if (response.isSuccessful) {
            val responseBody = response.body
            if (responseBody != null) {
                val tempFile = File(cacheDir, "audio/$id/temp.zip")
                responseBody.byteStream().use { inputStream ->
                    FileOutputStream(tempFile).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                extractZip(tempFile, cacheDir)
                tempFile.delete()
                emit(Result.Success(Unit))
            } else {
                emit(Result.Error("Empty response body"))
            }
        } else {
            emit(Result.Error("Failed to download ZIP file"))
        }
    }

    private fun extractZip(zipFile: File, outputDir: File) {
        ZipInputStream(FileInputStream(zipFile)).use { zipInputStream ->
            var entry: ZipEntry? = zipInputStream.nextEntry
            while (entry != null) {
                val entryFile = File(outputDir, entry.name)
                if (entry.isDirectory) {
                    entryFile.mkdirs()
                } else {
                    FileOutputStream(entryFile).use { fileOutputStream ->
                        zipInputStream.copyTo(fileOutputStream)
                    }
                }
                zipInputStream.closeEntry()
                entry = zipInputStream.nextEntry
            }
        }
    }
}