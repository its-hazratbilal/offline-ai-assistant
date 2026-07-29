package com.hazratbilal.offlineaiassistant.data.local.datasource

import com.hazratbilal.offlineaiassistant.ai.manager.ModelManager
import com.hazratbilal.offlineaiassistant.ai.model.LlmRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LlmLocalDataSourceImpl @Inject constructor(
    private val modelManager: ModelManager
) : LlmLocalDataSource {

    override suspend fun generateResponseStream(request: LlmRequest): Flow<String> {
        return modelManager.generateStream(request)
    }
}