package com.hazratbilal.offlineaiassistant.data.model

data class LlmModel(
    val id: String,
    val name: String,
    val description: String,
    val downloadSize: String,
    val ramRequirement: String,
    val downloadUrl: String,
    val fileName: String,
    val recommended: Boolean = false,
    val isLightweight: Boolean = false
)