package com.hazratbilal.offlineaiassistant.ui.features.model_selection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hazratbilal.offlineaiassistant.data.model.LlmModel
import com.hazratbilal.offlineaiassistant.ui.common.CustomToolbar
import com.hazratbilal.offlineaiassistant.ui.common.fadeTopEdge

@Composable
fun ModelSelectionScreen(
    onBackClick: (() -> Unit)? = null,
    onSwitchComplete: () -> Unit,
    viewModel: ModelSelectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var helpModel by remember { mutableStateOf<LlmModel?>(null) }
    var lowRamWarningModel by remember { mutableStateOf<LlmModel?>(null) }

    LaunchedEffect(uiState.switchComplete) {
        if (uiState.switchComplete) {
            onSwitchComplete()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CustomToolbar(
            title = "Choose AI Model",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fadeTopEdge(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.padding(top = 12.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Performance",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Response speed depends on your device's hardware. Older phones may require smaller models like SmolLM2, Qwen, or TinyLlama for the best experience. Devices without GPU acceleration will also generate responses more slowly than GPU-capable phones.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                items(uiState.models) { model ->
                    val cardState = uiState.cardStates[model.id] ?: ModelCardState()

                    ModelItem(
                        model = model,
                        cardState = cardState,
                        isSwitching = uiState.switchingModelId == model.id,
                        isSelected = uiState.selectedModelId == model.id,
                        isAnyDownloadActive = uiState.activeDownloadModelId != null,
                        onDownloadClick = {
                            viewModel.checkRamAndDownload(model) {
                                lowRamWarningModel = model
                            }
                        },
                        onRetryClick = { viewModel.retryDownload(model) },
                        onUseModelClick = { viewModel.useModel(model) },
                        onHelp = { helpModel = model }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                }

            }
        }
    }

    helpModel?.let { model ->
        AlertDialog(
            onDismissRequest = { helpModel = null },
            confirmButton = {
                TextButton(onClick = { helpModel = null }) { Text("OK") } },
            title = { Text(text = model.name, style = MaterialTheme.typography.titleMedium) },
            text = {
                Column {
                    Text(model.description)
                    Spacer(Modifier.height(12.dp))
                    Text("Download: ${model.downloadSize}")
                    Text("RAM Required: ${model.ramRequirement}")
                }
            }
        )
    }

    lowRamWarningModel?.let { model ->
        val dismiss = { lowRamWarningModel = null }
        AlertDialog(
            onDismissRequest = dismiss,
            title = { Text(text = "Limited Device Memory", style = MaterialTheme.typography.titleMedium) },
            text = {
                Text(
                    "This model recommends ${model.ramRequirement} RAM. Your device may not " +
                            "have enough memory, which could cause slow responses or crashes. " +
                            "Do you want to continue anyway?"
                )
            },
            confirmButton = {
                TextButton(onClick = dismiss) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.startDownload(model)
                        dismiss()
                    }
                ) {
                    Text("Continue anyway")
                }
            }
        )
    }
}