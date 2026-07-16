package com.hazratbilal.offlineaiassistant.ui.features.model_selection

import com.hazratbilal.offlineaiassistant.data.model.AvailableModels
import com.hazratbilal.offlineaiassistant.data.model.LlmModel

data class ModelSelectionUiState(
    val models: List<LlmModel> = AvailableModels.models,
    val cardStates: Map<String, ModelCardState> = emptyMap(),
    val activeDownloadModelId: String? = null,
    val switchingModelId: String? = null,
    val switchComplete: Boolean = false,
    val selectedModelId: String? = null
)