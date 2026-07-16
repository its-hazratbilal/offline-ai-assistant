package com.hazratbilal.offlineaiassistant.ui.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hazratbilal.offlineaiassistant.data.local.preferences.PreferencesManager
import com.hazratbilal.offlineaiassistant.data.model.AvailableModels
import com.hazratbilal.offlineaiassistant.ui.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = preferencesManager.themeMode

    private val _activeModelName = MutableStateFlow<String?>(null)
    val activeModelName: StateFlow<String?> = _activeModelName.asStateFlow()

    init {
        refreshActiveModelName()
    }

    fun onThemeModeSelected(mode: ThemeMode) {
        viewModelScope.launch {
            preferencesManager.setThemeMode(mode)
        }
    }

    fun refreshActiveModelName() {
        viewModelScope.launch {
            val id = preferencesManager.getActiveModelId()
            _activeModelName.value = AvailableModels.models.firstOrNull { it.id == id }?.name
        }
    }
}