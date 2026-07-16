package com.hazratbilal.offlineaiassistant.data.mapper

import com.hazratbilal.offlineaiassistant.data.local.entity.ChatEntity
import com.hazratbilal.offlineaiassistant.data.local.entity.ChatSessionEntity
import com.hazratbilal.offlineaiassistant.domain.model.ChatMessage
import com.hazratbilal.offlineaiassistant.domain.model.ChatSession
import javax.inject.Inject

class ChatMapper @Inject constructor() {

    fun toDomain(entity: ChatEntity): ChatMessage {
        return ChatMessage(
            id = entity.id,
            request = entity.request,
            response = entity.response,
            timestamp = entity.timestamp
        )
    }

    fun toEntity(message: ChatMessage, sessionId: Long): ChatEntity {
        return ChatEntity(
            id = message.id,
            sessionId = sessionId,
            request = message.request,
            response = message.response,
            timestamp = message.timestamp
        )
    }

    fun toDomainList(entities: List<ChatEntity>): List<ChatMessage> {
        return entities.map { toDomain(it) }
    }

    fun toDomainSession(entity: ChatSessionEntity): ChatSession {
        return ChatSession(
            id = entity.id,
            title = entity.title,
            label = entity.label,
            updatedAt = entity.updatedAt
        )
    }
}