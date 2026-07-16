package com.hazratbilal.offlineaiassistant.ui.features.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hazratbilal.offlineaiassistant.data.llm.ModelSessionManager
import com.hazratbilal.offlineaiassistant.data.llm.ModelSessionState
import com.hazratbilal.offlineaiassistant.domain.model.ChatMessage
import com.hazratbilal.offlineaiassistant.domain.model.ChatSession
import com.hazratbilal.offlineaiassistant.domain.repository.ChatRepository
import com.hazratbilal.offlineaiassistant.domain.usecase.ChatUseCase
import com.hazratbilal.offlineaiassistant.domain.usecase.ConversationContextBuilder
import com.hazratbilal.offlineaiassistant.utils.ChatExporter
import com.hazratbilal.offlineaiassistant.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatUseCase: ChatUseCase,
    private val chatRepository: ChatRepository,
    private val modelSessionManager: ModelSessionManager,
    private val chatExporter: ChatExporter
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var messagesJob: Job? = null
    private var generationJob: Job? = null

    init {
        observeModelSession()
        loadActiveModel()
        observeSessions()
    }

    private fun loadActiveModel() {
        viewModelScope.launch { modelSessionManager.ensureModelLoaded() }
    }

    private fun observeModelSession() {
        viewModelScope.launch {
            modelSessionManager.sessionState.collect { sessionState ->
                when (sessionState) {
                    is ModelSessionState.Loading, ModelSessionState.NotLoaded -> {
                        _uiState.update { it.copy(isModelLoading = true) }
                    }

                    is ModelSessionState.Ready -> {
                        _uiState.update {
                            it.copy(
                                isModelLoading = false,
                                activeModelName = sessionState.model.name,
                                isActiveModelLightweight = sessionState.model.isLightweight,
                                error = null
                            )
                        }
                    }

                    is ModelSessionState.Error -> {
                        _uiState.update {
                            it.copy(
                                isModelLoading = false,
                                error = "Model failed to load: ${sessionState.message}"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun observeSessions() {
        viewModelScope.launch {
            chatRepository.getSessions().collect { sessions ->
                _uiState.update { it.copy(sessions = sessions) }
            }
        }
    }

    fun onNewChatClick() {
        stopGeneration()
        messagesJob?.cancel()

        viewModelScope.launch {
            modelSessionManager.resetConversation()

            _uiState.update {
                it.copy(
                    currentSessionId = null,
                    isPrivateChat = false,
                    messages = emptyList(),
                    message = "",
                    pendingUserMessage = null,
                    pendingAiText = null,
                    assistantGreeting = if (it.selectedLabel == ChatLabel.GENERAL) null else it.selectedLabel.greetingMessage,
                    error = null
                )
            }
        }
    }

    fun onPrivateChatClick() {
        stopGeneration()
        messagesJob?.cancel()

        viewModelScope.launch {
            modelSessionManager.resetConversation()

            _uiState.update {
                it.copy(
                    currentSessionId = null,
                    isPrivateChat = true,
                    messages = emptyList(),
                    message = "",
                    pendingUserMessage = null,
                    pendingAiText = null,
                    assistantGreeting = if (it.selectedLabel == ChatLabel.GENERAL) null else it.selectedLabel.greetingMessage,
                    error = null
                )
            }
        }
    }

    fun onSessionSelected(session: ChatSession) {
        stopGeneration()
        messagesJob?.cancel()
        viewModelScope.launch { modelSessionManager.resetConversation() }
        _uiState.update {
            it.copy(
                currentSessionId = session.id,
                isPrivateChat = false,
                selectedLabel = ChatLabel.fromName(session.label),
                message = "",
                pendingUserMessage = null,
                pendingAiText = null,
                assistantGreeting = null,
                needsHistoryReplay = true
            )
        }
        messagesJob = viewModelScope.launch {
            chatRepository.getMessages(session.id).collect { messages ->
                _uiState.update { it.copy(messages = messages) }
            }
        }
    }

    fun onShowLabelDialog() = _uiState.update { it.copy(showLabelDialog = true) }
    fun onDismissLabelDialog() = _uiState.update { it.copy(showLabelDialog = false) }

    fun onLabelSelected(label: ChatLabel) {
        stopGeneration()
        messagesJob?.cancel()

        _uiState.update {
            it.copy(
                currentSessionId = null,
                isPrivateChat = false,
                messages = emptyList(),
                message = "",
                pendingUserMessage = null,
                pendingAiText = null,
                selectedLabel = label,
                assistantGreeting = if (label == ChatLabel.GENERAL) null else label.greetingMessage,
                showLabelDialog = false,
                error = null
            )
        }

        viewModelScope.launch {
            modelSessionManager.resetConversation()
        }
    }

    fun onMessageChanged(message: String) {
        _uiState.update { it.copy(message = message, error = null) }
    }

    fun onErrorShown() {
        _uiState.update { it.copy(error = null) }
    }

    fun sendMessage() {
        val state = _uiState.value
        val currentMessage = normalizeMessage(text = state.message)
        if (currentMessage.isBlank()) return
        if (state.isModelLoading) return
        if (state.isLoading) return

        _uiState.update {
            it.copy(
                message = "",
                pendingUserMessage = currentMessage,
                pendingAiText = null,
                isLoading = true,
                isThinking = true,
                error = null,
                assistantGreeting = null
            )
        }

        generationJob = viewModelScope.launch {
            try {
                var sessionId = state.currentSessionId

                if (sessionId == null && !state.isPrivateChat) {
                    sessionId = chatRepository.createSession(
                        label = state.selectedLabel.name,
                        title = "New Chat"
                    )
                    _uiState.update { it.copy(currentSessionId = sessionId) }

                    messagesJob?.cancel()
                    messagesJob = viewModelScope.launch {
                        chatRepository.getMessages(sessionId).collect { messages ->
                            _uiState.update { it.copy(messages = messages) }
                        }
                    }
                }

                val systemPrompt = state.selectedLabel.systemPrompt

                val historyForReplay: List<ChatMessage> =
                    if (state.needsHistoryReplay && sessionId != null) {
                        chatRepository.getMessages(sessionId).first()
                    } else {
                        emptyList()
                    }

                val effectiveMessage = if (historyForReplay.isNotEmpty()) {
                    ConversationContextBuilder.buildPrompt(
                        history = historyForReplay,
                        newMessage = currentMessage,
                        isLightweightModel = state.isActiveModelLightweight
                    )
                } else {
                    currentMessage
                }

                if (state.needsHistoryReplay) {
                    _uiState.update { it.copy(needsHistoryReplay = false) }
                }

                when (val result = chatUseCase(sessionId, currentMessage, effectiveMessage, systemPrompt)) {
                    is Result.Success -> {
                        _uiState.update { current ->
                            current.copy(
                                isLoading = false,
                                isThinking = false,
                                pendingUserMessage = null,
                                pendingAiText = null,
                                messages = if (current.isPrivateChat) current.messages + result.data else current.messages
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isThinking = false,
                                pendingUserMessage = null,
                                pendingAiText = null,
                                error = result.message
                            )
                        }
                    }
                }
            } catch (e: CancellationException) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isThinking = false,
                        pendingAiText = "Response stopped."
                    )
                }
                throw e
            }
        }
    }

    fun normalizeMessage(text: String): String {
        return text
            .replace(Regex("[ \t]+"), " ")
            .replace(Regex("\n[ \t]+"), "\n")
            .replace(Regex("\n{3,}"), "\n\n")
            .trim()
    }

    fun stopGeneration() {
        generationJob?.cancel()
    }

    fun onDeleteChatClick() {
        val sessionId = _uiState.value.currentSessionId
        if (sessionId == null) {
            onNewChatClick()
            return
        }
        viewModelScope.launch {
            chatRepository.deleteSession(sessionId)
            onNewChatClick()
        }
    }

    fun downloadChat(onFileReady: (File) -> Unit) {
        val state = _uiState.value
        if (state.messages.isEmpty()) return

        val title = state.sessions.firstOrNull { it.id == state.currentSessionId }?.title ?: "Chat"

        viewModelScope.launch {
            val file = chatExporter.exportToTextFile(title, state.messages)
            onFileReady(file)
        }
    }

    fun downloadMessage(message: ChatMessage, onFileReady: (File) -> Unit) {
        viewModelScope.launch {
            val file = chatExporter.exportMessageToTextFile(message)
            onFileReady(file)
        }
    }

    override fun onCleared() {
        super.onCleared()
        messagesJob?.cancel()
        generationJob?.cancel()
    }
}