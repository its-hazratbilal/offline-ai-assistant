package com.hazratbilal.offlineaiassistant.data.download

import android.content.Context
import com.hazratbilal.offlineaiassistant.data.model.LlmModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModelDownloadManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val okHttpClient: OkHttpClient
) {

    fun downloadModel(model: LlmModel): Flow<DownloadState> = callbackFlow {
        trySend(DownloadState.InProgress(0, 0f, 0f))

        val modelsDir = File(context.filesDir, "models").apply { mkdirs() }
        val outputFile = File(modelsDir, model.fileName)
        val tempFile = File(modelsDir, "${model.fileName}.tmp")

        if (tempFile.exists()) {
            tempFile.delete()
        }

        val request = Request.Builder().url(model.downloadUrl).build()
        val call = okHttpClient.newCall(request)

        try {
            val response = call.execute()

            if (!response.isSuccessful) {
                trySend(DownloadState.Error("Download failed: HTTP ${response.code}"))
                close()
                return@callbackFlow
            }

            val body = response.body ?: run {
                trySend(DownloadState.Error("Empty response body"))
                close()
                return@callbackFlow
            }

            val totalBytes = body.contentLength()
            var downloadedBytes = 0L

            body.byteStream().use { input ->
                FileOutputStream(tempFile).use { output ->
                    val buffer = ByteArray(8 * 1024)
                    var bytesRead: Int

                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        downloadedBytes += bytesRead

                        val percent = if (totalBytes > 0) {
                            ((downloadedBytes * 100) / totalBytes).toInt()
                        } else 0

                        trySend(
                            DownloadState.InProgress(
                                progressPercent = percent,
                                downloadedMB = downloadedBytes / (1024f * 1024f),
                                totalMB = totalBytes / (1024f * 1024f)
                            )
                        )
                    }
                }
            }

            if (outputFile.exists()) outputFile.delete()
            tempFile.renameTo(outputFile)

            trySend(DownloadState.Success(outputFile.absolutePath))
            close()

        } catch (e: IOException) {
            tempFile.delete()
            trySend(DownloadState.Error(e.message ?: "Network error during download"))
            close()
        }

        awaitClose {
            call.cancel()
            if (tempFile.exists()) {
                tempFile.delete()
            }
        }
    }.flowOn(Dispatchers.IO)
}