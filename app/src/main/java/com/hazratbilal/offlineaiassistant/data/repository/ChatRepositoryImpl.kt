package com.hazratbilal.offlineaiassistant.data.repository

import com.hazratbilal.offlineaiassistant.ai.model.LlmRequest
import com.hazratbilal.offlineaiassistant.data.local.dao.ChatDao
import com.hazratbilal.offlineaiassistant.data.local.dao.ChatSessionDao
import com.hazratbilal.offlineaiassistant.data.local.datasource.LlmLocalDataSource
import com.hazratbilal.offlineaiassistant.data.local.entity.ChatEntity
import com.hazratbilal.offlineaiassistant.data.local.entity.ChatSessionEntity
import com.hazratbilal.offlineaiassistant.data.mapper.ChatMapper
import com.hazratbilal.offlineaiassistant.domain.model.ChatMessage
import com.hazratbilal.offlineaiassistant.domain.model.ChatSession
import com.hazratbilal.offlineaiassistant.domain.repository.ChatRepository
import com.hazratbilal.offlineaiassistant.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val llmDataSource: LlmLocalDataSource,
    private val chatDao: ChatDao,
    private val chatSessionDao: ChatSessionDao,
    private val mapper: ChatMapper
) : ChatRepository {

    override suspend fun sendMessage(
        sessionId: Long,
        message: String,
        promptForModel: String,
        systemPrompt: String?
    ): Result<ChatMessage> {
        return try {
            val llmRequest = LlmRequest(prompt = promptForModel, systemPrompt = systemPrompt)
            val llmResponse = llmDataSource.generateResponse(llmRequest)

            val entity = ChatEntity(sessionId = sessionId, request = message, response = llmResponse.generatedText)
            val insertedId = chatDao.insert(entity)

            val session = chatSessionDao.getSessionById(sessionId)
            chatSessionDao.updateTitleAndTimestamp(
                sessionId = sessionId,
                title = if (session?.title == "New Chat" || session == null) message.take(40) else session.title,
                updatedAt = System.currentTimeMillis()
            )

            Result.Success(mapper.toDomain(entity.copy(id = insertedId)))
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to generate response", e)
        }
    }

    override suspend fun sendMessageEphemeral(
        message: String,
        promptForModel: String,
        systemPrompt: String?
    ): Result<ChatMessage> {
        return try {
            val llmRequest = LlmRequest(prompt = promptForModel, systemPrompt = systemPrompt)
            val llmResponse = llmDataSource.generateResponse(llmRequest)

            Result.Success(
                ChatMessage(
                    id = 0,
                    request = message,
                    response = llmResponse.generatedText,
                    timestamp = System.currentTimeMillis()
                )
            )
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to generate response", e)
        }
    }

    override fun getMessages(sessionId: Long): Flow<List<ChatMessage>> =
        chatDao.getMessagesForSession(sessionId).map { mapper.toDomainList(it) }

    override fun getSessions(): Flow<List<ChatSession>> =
        chatSessionDao.getAllSessions().map { entities -> entities.map { mapper.toDomainSession(it) } }

    override suspend fun createSession(label: String, title: String): Long =
        chatSessionDao.insert(ChatSessionEntity(title = title, label = label))

    override suspend fun deleteSession(sessionId: Long) {
        chatDao.deleteMessagesForSession(sessionId)
        chatSessionDao.deleteSession(sessionId)
    }

    override suspend fun updateSessionLabel(sessionId: Long, label: String) {
        chatSessionDao.updateLabel(sessionId, label)
    }
}