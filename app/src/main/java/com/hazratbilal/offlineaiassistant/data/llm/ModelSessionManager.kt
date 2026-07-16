package com.hazratbilal.offlineaiassistant.data.llm

import com.hazratbilal.offlineaiassistant.ai.manager.ModelManager
import com.hazratbilal.offlineaiassistant.data.local.preferences.PreferencesManager
import com.hazratbilal.offlineaiassistant.data.model.AvailableModels
import com.hazratbilal.offlineaiassistant.data.model.LlmModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModelSessionManager @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val modelManager: ModelManager
) {
    private val _sessionState = MutableStateFlow<ModelSessionState>(ModelSessionState.NotLoaded)
    val sessionState: StateFlow<ModelSessionState> = _sessionState.asStateFlow()

    private val loadMutex = Mutex()

    suspend fun ensureModelLoaded() {
        if (_sessionState.value is ModelSessionState.Ready) return

        val activeId = preferencesManager.getActiveModelId() ?: return
        val activePath = preferencesManager.getActiveModelPath() ?: return
        val model = AvailableModels.models.firstOrNull { it.id == activeId } ?: return

        loadModelInternal(model, activePath)
    }

    suspend fun switchModel(model: LlmModel, filePath: String) {
        loadModelInternal(model, filePath)
    }

    suspend fun resetConversation() {
        loadMutex.withLock {
            val current = _sessionState.value
            if (current !is ModelSessionState.Ready) return@withLock

            _sessionState.value = ModelSessionState.Loading
            try {
                modelManager.resetConversation()
                _sessionState.value = ModelSessionState.Ready(current.model)
            } catch (e: Exception) {
                _sessionState.value = ModelSessionState.Error(
                    e.message ?: "Failed to reset conversation"
                )
            }
        }
    }

    private suspend fun loadModelInternal(model: LlmModel, filePath: String) {
        loadMutex.withLock {
            _sessionState.value = ModelSessionState.Loading

            try {
                if (!File(filePath).exists()) {
                    throw IllegalStateException("Model file missing at $filePath")
                }

                modelManager.loadModel(filePath)

                preferencesManager.saveSelectedModel(model.id, filePath)
                _sessionState.value = ModelSessionState.Ready(model)

            } catch (e: Exception) {
                _sessionState.value = ModelSessionState.Error(
                    e.message ?: "Failed to load model"
                )
            }
        }
    }

    fun unloadCurrentModel() {
        modelManager.unload()
        _sessionState.value = ModelSessionState.NotLoaded
    }
}