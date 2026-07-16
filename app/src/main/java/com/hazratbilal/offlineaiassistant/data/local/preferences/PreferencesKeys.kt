package com.hazratbilal.offlineaiassistant.data.local.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    const val PREFS_NAME = "offline_ai_assistant_prefs"
    val THEME_MODE = stringPreferencesKey("key_theme_mode")
    val ACTIVE_MODEL_ID = stringPreferencesKey("key_active_model_id")
    val ACTIVE_MODEL_PATH = stringPreferencesKey("key_active_model_path")
    val MODEL_DOWNLOADED = booleanPreferencesKey("key_model_downloaded")
    fun downloadedPath(modelId: String) = stringPreferencesKey("downloaded_path_$modelId")
}