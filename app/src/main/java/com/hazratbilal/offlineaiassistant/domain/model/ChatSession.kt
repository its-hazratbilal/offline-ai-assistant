package com.hazratbilal.offlineaiassistant.domain.model

data class ChatSession(
    val id: Long,
    val title: String,
    val label: String,
    val updatedAt: Long
)