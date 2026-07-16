package com.hazratbilal.offlineaiassistant.domain.model

data class ChatMessage(
    val id: Long = 0,
    val request: String,
    val response: String,
    val timestamp: Long = System.currentTimeMillis()


)