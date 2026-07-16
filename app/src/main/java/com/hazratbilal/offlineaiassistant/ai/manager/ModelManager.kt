package com.hazratbilal.offlineaiassistant.ai.manager

import com.hazratbilal.offlineaiassistant.ai.engine.LlmEngine
import com.hazratbilal.offlineaiassistant.ai.engine.isModelLoaded
import com.hazratbilal.offlineaiassistant.ai.model.LlmRequest
import com.hazratbilal.offlineaiassistant.ai.model.LlmResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.fold
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModelManager @Inject constructor(
    private val llmEngine: LlmEngine
) {

    private var loadedModelPath: String? = null
    private var systemPromptAppliedForCurrentLoad: String? = null

    private var hasGeneratedSinceLoad = false

    suspend fun loadModel(modelFilePath: String) {
        if (loadedModelPath == modelFilePath && llmEngine.state.value.isModelLoaded) return

        val currentState = llmEngine.state.value

        if (currentState is LlmEngine.State.Uninitialized || currentState is LlmEngine.State.Initializing) {
            val readyState = llmEngine.state.first {
                it is LlmEngine.State.Initialized || it is LlmEngine.State.Error
            }
            if (readyState is LlmEngine.State.Error) {
                throw readyState.exception
            }
        }

        if (llmEngine.state.value.isModelLoaded) {
            llmEngine.cleanUp()
        }

        llmEngine.loadModel(modelFilePath)
        loadedModelPath = modelFilePath
        systemPromptAppliedForCurrentLoad = null
        hasGeneratedSinceLoad = false
    }

    suspend fun resetConversation() {
        if (!hasGeneratedSinceLoad) return
        val path = loadedModelPath ?: return

        if (llmEngine.state.value.isModelLoaded) {
            llmEngine.cleanUp()
        }

        val readyState = llmEngine.state.first {
            it is LlmEngine.State.Initialized || it is LlmEngine.State.Error
        }
        if (readyState is LlmEngine.State.Error) {
            throw readyState.exception
        }

        llmEngine.loadModel(path)
        systemPromptAppliedForCurrentLoad = null
        hasGeneratedSinceLoad = false
    }

    suspend fun generate(request: LlmRequest): LlmResponse {
        require(llmEngine.state.value.isModelLoaded) {
            "No model loaded — call loadModel() before generate()"
        }

        if (request.systemPrompt != null && systemPromptAppliedForCurrentLoad == null) {
            llmEngine.setSystemPrompt(request.systemPrompt)
            systemPromptAppliedForCurrentLoad = request.systemPrompt
        }

        val result = try {
            llmEngine.sendUserPrompt(
                request.prompt,
                request.maxTokens
            ).fold(StringBuilder()) { acc, token ->
                acc.append(token)
            }.toString()
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        }

        hasGeneratedSinceLoad = true
        return LlmResponse(generatedText = result)
    }

    fun unload() {
        llmEngine.cleanUp()
        loadedModelPath = null
        systemPromptAppliedForCurrentLoad = null
        hasGeneratedSinceLoad = false
    }

    fun isLoaded(): Boolean = llmEngine.state.value.isModelLoaded

    fun currentModelPath(): String? = loadedModelPath
}