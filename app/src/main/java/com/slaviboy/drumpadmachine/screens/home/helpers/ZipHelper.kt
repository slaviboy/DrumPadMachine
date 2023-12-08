package com.slaviboy.drumpadmachine.screens.home.helpers

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipInputStream

object ZipHelper {
    fun extractZip(zipFile: File, outputDir: File): Boolean {
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