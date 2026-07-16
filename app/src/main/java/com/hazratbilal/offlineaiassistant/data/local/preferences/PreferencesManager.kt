package com.hazratbilal.offlineaiassistant.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.hazratbilal.offlineaiassistant.ui.theme.ThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val themeMode: StateFlow<ThemeMode> = context.dataStore.data
        .map { prefs ->
            val stored = prefs[PreferencesKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name
            try {
                ThemeMode.valueOf(stored)
            } catch (e: IllegalArgumentException) {
                ThemeMode.SYSTEM
            }
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = runBlocking { readThemeModeOnce() }
        )

    private suspend fun readThemeModeOnce(): ThemeMode {
        val stored = context.dataStore.data.first()[PreferencesKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name
        return try {
            ThemeMode.valueOf(stored)
        } catch (e: IllegalArgumentException) {
            ThemeMode.SYSTEM
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[PreferencesKeys.THEME_MODE] = mode.name }
    }

    suspend fun saveSelectedModel(modelId: String, filePath: String) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.ACTIVE_MODEL_ID] = modelId
            prefs[PreferencesKeys.ACTIVE_MODEL_PATH] = filePath
            prefs[PreferencesKeys.MODEL_DOWNLOADED] = true
            prefs[PreferencesKeys.downloadedPath(modelId)] = filePath
        }
    }

    suspend fun getActiveModelId(): String? =
        context.dataStore.data.first()[PreferencesKeys.ACTIVE_MODEL_ID]

    suspend fun getActiveModelPath(): String? =
        context.dataStore.data.first()[PreferencesKeys.ACTIVE_MODEL_PATH]

    suspend fun isModelDownloaded(): Boolean =
        context.dataStore.data.first()[PreferencesKeys.MODEL_DOWNLOADED] ?: false

    suspend fun markModelAsDownloaded(modelId: String, filePath: String) {
        context.dataStore.edit { it[PreferencesKeys.downloadedPath(modelId)] = filePath }
    }

    suspend fun getDownloadedPath(modelId: String): String? =
        context.dataStore.data.first()[PreferencesKeys.downloadedPath(modelId)]

    suspend fun isModelFileDownloaded(modelId: String): Boolean {
        val path = getDownloadedPath(modelId) ?: return false
        return File(path).exists()
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}