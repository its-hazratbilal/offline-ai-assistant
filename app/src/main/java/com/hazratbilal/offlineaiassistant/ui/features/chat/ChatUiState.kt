package com.hazratbilal.offlineaiassistant.ui.features.chat

import com.hazratbilal.offlineaiassistant.domain.model.ChatMessage
import com.hazratbilal.offlineaiassistant.domain.model.ChatSession

data class ChatUiState(
    val message: String = "",
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val isThinking: Boolean = false,
    val isModelLoading: Boolean = true,
    val activeModelName: String? = null,
    val isActiveModelLightweight: Boolean = false,
    val error: String? = null,
    val currentSessionId: Long? = null,
    val isPrivateChat: Boolean = false,
    val selectedLabel: ChatLabel = ChatLabel.GENERAL,
    val showLabelDialog: Boolean = false,
    val sessions: List<ChatSession> = emptyList(),
    val assistantGreeting: String? = null,
    val needsHistoryReplay: Boolean = false,
    val pendingUserMessage: String? = null,
    val pendingAiText: String? = null
)