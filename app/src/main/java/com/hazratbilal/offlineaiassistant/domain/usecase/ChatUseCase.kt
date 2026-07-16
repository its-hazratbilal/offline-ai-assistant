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
        message: String,
        promptForModel: String,
        systemPrompt: String?
    ): Result<ChatMessage> {
        if (message.isBlank()) {
            return Result.Error("Please enter a message")
        }

        return if (sessionId != null) {
            repository.sendMessage(sessionId, message, promptForModel, systemPrompt)
        } else {
            repository.sendMessageEphemeral(message, promptForModel, systemPrompt)
        }
    }
}