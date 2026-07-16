package com.hazratbilal.offlineaiassistant.data.local.datasource

import com.hazratbilal.offlineaiassistant.ai.manager.ModelManager
import com.hazratbilal.offlineaiassistant.ai.model.LlmRequest
import com.hazratbilal.offlineaiassistant.ai.model.LlmResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LlmLocalDataSourceImpl @Inject constructor(
    private val modelManager: ModelManager
) : LlmLocalDataSource {

    override suspend fun generateResponse(
        request: LlmRequest
    ): LlmResponse {
        return modelManager.generate(request)
    }
}