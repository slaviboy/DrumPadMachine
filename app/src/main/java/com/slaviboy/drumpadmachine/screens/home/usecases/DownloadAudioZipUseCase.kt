package com.slaviboy.drumpadmachine.screens.home.usecases

import com.slaviboy.drumpadmachine.api.repositories.ApiRepository
import com.slaviboy.drumpadmachine.api.results.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
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
            val fullCacheDirPath = File(cacheDir, "audio/$id/")
            if (fullCacheDirPath.isDirectory()) {
                fullCacheDirPath.mkdirs()
            }
            val tempFile = File(fullCacheDirPath, "temp.zip")
            try {
                withContext(Dispatchers.IO) {
                    tempFile.getParentFile()?.mkdirs()
                    tempFile.createNewFile()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            response.body()?.byteStream()?.use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            } ?: run {
                emit(Result.Error("Empty response body"))
            }
            extractZip(tempFile, fullCacheDirPath)
            tempFile.delete()
            emit(Result.Success(Unit))

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