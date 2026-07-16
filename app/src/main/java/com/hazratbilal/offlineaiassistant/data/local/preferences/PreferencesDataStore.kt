package com.hazratbilal.offlineaiassistant.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore(name = PreferencesKeys.PREFS_NAME)