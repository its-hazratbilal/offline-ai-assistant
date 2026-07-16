package com.hazratbilal.offlineaiassistant.data.local.datasource

import com.hazratbilal.offlineaiassistant.ai.model.LlmRequest
import com.hazratbilal.offlineaiassistant.ai.model.LlmResponse

interface LlmLocalDataSource {
    suspend fun generateResponse(request: LlmRequest): LlmResponse
}