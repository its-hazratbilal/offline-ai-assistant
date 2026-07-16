package com.hazratbilal.offlineaiassistant.data.llm

import com.hazratbilal.offlineaiassistant.data.model.LlmModel

sealed class ModelSessionState {
    data object NotLoaded : ModelSessionState()
    data object Loading : ModelSessionState()
    data class Ready(val model: LlmModel) : ModelSessionState()
    data class Error(val message: String) : ModelSessionState()
}

