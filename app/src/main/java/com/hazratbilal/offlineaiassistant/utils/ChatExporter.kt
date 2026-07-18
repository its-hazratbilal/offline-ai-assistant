package com.hazratbilal.offlineaiassistant.utils

import android.content.Context
import com.hazratbilal.offlineaiassistant.domain.model.ChatMessage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatExporter @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun exportToTextFile(title: String, messages: List<ChatMessage>): File =
        withContext(Dispatchers.IO) {
            val dir = File(context.cacheDir, "exports").apply { mkdirs() }
            val safeName = title.take(30).replace(Regex("[^A-Za-z0-9]"), "_")
            val file = File(dir, "${safeName}_${System.currentTimeMillis()}.txt")

            file.bufferedWriter().use { writer ->
                writer.write("$title\n")
                writer.write("=".repeat(title.length) + "\n\n")
                messages.forEach { msg ->
                    writer.write("You: ${msg.request}\n\n")
                    writer.write("AI: ${MarkdownStripper.toPlainText(msg.response)}\n\n")
                    writer.write("-".repeat(40) + "\n\n")
                }
            }
            file
        }

    suspend fun exportMessageToTextFile(message: ChatMessage): File =
        withContext(Dispatchers.IO) {
            val dir = File(context.cacheDir, "exports").apply { mkdirs() }
            val file = File(dir, "message_${message.timestamp}.txt")

            file.bufferedWriter().use { writer ->
                writer.write("You: ${message.request}\n\n")
                writer.write("AI: ${MarkdownStripper.toPlainText(message.response)}\n")
            }
            file
        }
}