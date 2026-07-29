package com.hazratbilal.offlineaiassistant.domain.usecase

import com.hazratbilal.offlineaiassistant.domain.model.ChatMessage
import com.hazratbilal.offlineaiassistant.domain.repository.ChatRepository
import com.hazratbilal.offlineaiassistant.utils.Result
import javax.inject.Inject

class ChatUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(
        sessionId: Long?,
        displayMessage: String,
        promptForModel: String,
        systemPrompt: String?,
        onToken: (String) -> Unit
    ): Result<ChatMessage> {
        if (displayMessage.isBlank()) {
            return Result.Error("Please enter a message")
        }

        return if (sessionId != null) {
            repository.sendMessageStreaming(sessionId, displayMessage, promptForModel, systemPrompt, onToken)
        } else {
            repository.sendMessageEphemeralStreaming(displayMessage, promptForModel, systemPrompt, onToken)
        }
    }
}