package com.hazratbilal.offlineaiassistant.data.local.datasource

import com.hazratbilal.offlineaiassistant.ai.model.LlmRequest
import kotlinx.coroutines.flow.Flow

interface LlmLocalDataSource {
    suspend fun generateResponseStream(request: LlmRequest): Flow<String>
}