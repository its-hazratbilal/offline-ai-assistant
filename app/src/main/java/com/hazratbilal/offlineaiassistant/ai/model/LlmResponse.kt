package com.hazratbilal.offlineaiassistant.ai.model

data class LlmResponse(
    val generatedText: String,
    val promptTokens: Int? = null,
    val generatedTokens: Int? = null,
    val finished: Boolean = true
)