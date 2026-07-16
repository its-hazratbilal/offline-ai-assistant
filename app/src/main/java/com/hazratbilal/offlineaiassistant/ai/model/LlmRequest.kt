package com.hazratbilal.offlineaiassistant.ai.model

data class LlmRequest(
    val prompt: String,
    val systemPrompt: String? = null,
    val maxTokens: Int = 512,
    val temperature: Float = 0.7f
)