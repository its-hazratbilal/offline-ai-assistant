package com.hazratbilal.offlineaiassistant.ui.theme

import androidx.lifecycle.ViewModel
import com.hazratbilal.offlineaiassistant.data.local.preferences.PreferencesManager
import com.hazratbilal.offlineaiassistant.ui.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    preferencesManager: PreferencesManager
) : ViewModel() {
    val themeMode: StateFlow<ThemeMode> = preferencesManager.themeMode
}