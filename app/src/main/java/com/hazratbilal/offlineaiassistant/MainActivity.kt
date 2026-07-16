package com.hazratbilal.offlineaiassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.hazratbilal.offlineaiassistant.data.local.preferences.PreferencesManager
import com.hazratbilal.offlineaiassistant.ui.navigation.AuthNavGraph
import com.hazratbilal.offlineaiassistant.ui.navigation.Routes
import com.hazratbilal.offlineaiassistant.ui.theme.AppTheme
import com.hazratbilal.offlineaiassistant.ui.theme.ThemeMode
import com.hazratbilal.offlineaiassistant.ui.theme.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        val startDestinationState = mutableStateOf<String?>(null)
        splashScreen.setKeepOnScreenCondition { startDestinationState.value == null }

        lifecycleScope.launch {
            startDestinationState.value = if (preferencesManager.isModelDownloaded()) {
                Routes.CHAT
            } else {
                Routes.WELCOME
            }
        }

        enableEdgeToEdge()

        setContent {
            val destination by startDestinationState
            destination?.let { startDestination ->
                val themeMode by themeViewModel.themeMode.collectAsStateWithLifecycle()
                val systemDark = isSystemInDarkTheme()
                val useDarkTheme = when (themeMode) {
                    ThemeMode.LIGHT -> false
                    ThemeMode.DARK -> true
                    ThemeMode.SYSTEM -> systemDark
                }

                AppTheme(isDarkTheme = useDarkTheme) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = MaterialTheme.colorScheme.background
                    ) { innerPadding ->
                        AuthNavGraph(
                            modifier = Modifier
                                .padding(innerPadding)
                                .consumeWindowInsets(innerPadding),
                            startDestination = startDestination
                        )
                    }
                }
            }
        }
    }
}