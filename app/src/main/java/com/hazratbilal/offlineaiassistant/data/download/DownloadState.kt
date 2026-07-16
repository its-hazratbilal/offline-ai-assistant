package com.hazratbilal.offlineaiassistant.data.download

sealed class DownloadState {
    data object Idle : DownloadState()
    data class InProgress(
        val progressPercent: Int,
        val downloadedMB: Float,
        val totalMB: Float
    ) : DownloadState()
    data class Success(val filePath: String) : DownloadState()
    data class Error(val message: String) : DownloadState()
}