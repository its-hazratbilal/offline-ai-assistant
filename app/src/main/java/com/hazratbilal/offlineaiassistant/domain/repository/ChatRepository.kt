package com.hazratbilal.offlineaiassistant.domain.repository

import com.hazratbilal.offlineaiassistant.domain.model.ChatMessage
import com.hazratbilal.offlineaiassistant.domain.model.ChatSession
import com.hazratbilal.offlineaiassistant.utils.Result
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    suspend fun sendMessageStreaming(
        sessionId: Long,
        displayMessage: String,
        promptForModel: String,
        systemPrompt: String?,
        onToken: (String) -> Unit
    ): Result<ChatMessage>

    suspend fun sendMessageEphemeralStreaming(
        displayMessage: String,
        promptForModel: String,
        systemPrompt: String?,
        onToken: (String) -> Unit
    ): Result<ChatMessage>

    fun getMessages(sessionId: Long): Flow<List<ChatMessage>>
    fun getSessions(): Flow<List<ChatSession>>

    suspend fun createSession(label: String, title: String): Long
    suspend fun deleteSession(sessionId: Long)
    suspend fun updateSessionLabel(sessionId: Long, label: String)
}