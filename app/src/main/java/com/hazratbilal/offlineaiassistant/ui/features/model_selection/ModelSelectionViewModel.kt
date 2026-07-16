package com.hazratbilal.offlineaiassistant.ui.features.model_selection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hazratbilal.offlineaiassistant.data.download.DownloadState
import com.hazratbilal.offlineaiassistant.data.download.ModelDownloadManager
import com.hazratbilal.offlineaiassistant.data.llm.ModelSessionManager
import com.hazratbilal.offlineaiassistant.data.local.preferences.PreferencesManager
import com.hazratbilal.offlineaiassistant.data.model.AvailableModels
import com.hazratbilal.offlineaiassistant.data.model.LlmModel
import com.hazratbilal.offlineaiassistant.utils.RamChecker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ModelSelectionViewModel @Inject constructor(
    private val downloadManager: ModelDownloadManager,
    private val preferencesManager: PreferencesManager,
    private val modelSessionManager: ModelSessionManager,
    private val ramChecker: RamChecker
) : ViewModel() {

    private val _uiState = MutableStateFlow(ModelSelectionUiState())
    val uiState: StateFlow<ModelSelectionUiState> = _uiState.asStateFlow()

    private var downloadJob: Job? = null

    init {
        loadInitialState()
    }

    private fun loadInitialState() {
        viewModelScope.launch {
            val cardStates = AvailableModels.models.associate { model ->
                model.id to ModelCardState(
                    isDownloaded = preferencesManager.isModelFileDownloaded(model.id)
                )
            }
            val selectedModelId = preferencesManager.getActiveModelId()
            _uiState.update { it.copy(cardStates = cardStates, selectedModelId = selectedModelId) }
        }
    }

    fun checkRamAndDownload(model: LlmModel, onLowRam: () -> Unit) {
        if (ramChecker.meetsRequirement(model.ramRequirement)) {
            startDownload(model)
        } else {
            onLowRam()
        }
    }

    fun startDownload(model: LlmModel) {
        if (_uiState.value.activeDownloadModelId != null) return

        _uiState.update { it.copy(activeDownloadModelId = model.id) }

        downloadJob = viewModelScope.launch {
            downloadManager.downloadModel(model).collect { state ->
                updateCardState(model.id) { it.copy(downloadState = state) }

                when (state) {
                    is DownloadState.Success -> {
                        preferencesManager.markModelAsDownloaded(model.id, state.filePath)
                        updateCardState(model.id) {
                            it.copy(isDownloaded = true, downloadState = DownloadState.Idle)
                        }
                        _uiState.update { it.copy(activeDownloadModelId = null) }
                    }
                    is DownloadState.Error -> {
                        _uiState.update { it.copy(activeDownloadModelId = null) }
                    }
                    else -> Unit
                }
            }
        }
    }

    fun retryDownload(model: LlmModel) {
        updateCardState(model.id) { it.copy(downloadState = DownloadState.Idle) }
        startDownload(model)
    }

    fun useModel(model: LlmModel) {
        _uiState.update { it.copy(switchingModelId = model.id) }

        viewModelScope.launch {
            val path = preferencesManager.getDownloadedPath(model.id) ?: run {
                _uiState.update { it.copy(switchingModelId = null) }
                return@launch
            }
            modelSessionManager.switchModel(model, path)
            _uiState.update { it.copy(switchingModelId = null, switchComplete = true, selectedModelId = model.id) }
        }
    }

    private fun updateCardState(modelId: String, transform: (ModelCardState) -> ModelCardState) {
        _uiState.update { state ->
            val current = state.cardStates[modelId] ?: ModelCardState()
            state.copy(cardStates = state.cardStates + (modelId to transform(current)))
        }
    }

    override fun onCleared() {
        super.onCleared()
        downloadJob?.cancel()
    }
}