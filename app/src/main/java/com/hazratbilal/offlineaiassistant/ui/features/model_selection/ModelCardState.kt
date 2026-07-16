package com.hazratbilal.offlineaiassistant.ui.features.model_selection

import com.hazratbilal.offlineaiassistant.data.download.DownloadState

data class ModelCardState(
    val isDownloaded: Boolean = false,
    val downloadState: DownloadState = DownloadState.Idle
)