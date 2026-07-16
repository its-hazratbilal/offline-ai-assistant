package com.hazratbilal.offlineaiassistant.domain.usecase

import com.hazratbilal.offlineaiassistant.domain.model.ChatMessage

object ConversationContextBuilder {

    private const val MAX_HISTORY_CHARS_LIGHTWEIGHT = 2000
    private const val MAX_HISTORY_CHARS_STANDARD = 4000

    fun buildPrompt(
        history: List<ChatMessage>,
        newMessage: String,
        isLightweightModel: Boolean
    ): String {
        if (history.isEmpty()) return newMessage

        val maxHistoryChars = if (isLightweightModel) {
            MAX_HISTORY_CHARS_LIGHTWEIGHT
        } else {
            MAX_HISTORY_CHARS_STANDARD
        }

        val included = mutableListOf<ChatMessage>()
        var runningLength = newMessage.length

        for (msg in history.asReversed()) {
            val turnLength = msg.request.length + msg.response.length + 20
            if (runningLength + turnLength > maxHistoryChars) break
            included.add(0, msg)
            runningLength += turnLength
        }

        if (included.isEmpty()) return newMessage

        val sb = StringBuilder()
        included.forEach { msg ->
            sb.append("User: ${msg.request}\n")
            sb.append("Assistant: ${msg.response}\n\n")
        }
        sb.append("User: $newMessage")
        return sb.toString()
    }
}