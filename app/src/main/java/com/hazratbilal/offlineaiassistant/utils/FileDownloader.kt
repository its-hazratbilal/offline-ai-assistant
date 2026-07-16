package com.hazratbilal.offlineaiassistant.utils

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import java.io.File

class FileDownloader(
    private val context: Context
) {

    fun saveToDownloads(file: File): Boolean {
        return try {
            val resolver = context.contentResolver

            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, file.name)
                put(MediaStore.Downloads.MIME_TYPE, "text/plain")

                put(
                    MediaStore.Downloads.RELATIVE_PATH,
                    Environment.DIRECTORY_DOWNLOADS
                )
                put(MediaStore.Downloads.IS_PENDING, 1)
            }

            val uri = resolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                values
            ) ?: return false

            resolver.openOutputStream(uri)?.use { output ->
                file.inputStream().use { input ->
                    input.copyTo(output)
                }
            }

            values.clear()
            values.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, values, null, null)

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}