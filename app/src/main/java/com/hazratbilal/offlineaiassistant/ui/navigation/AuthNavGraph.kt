package com.hazratbilal.offlineaiassistant.ui.navigation

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hazratbilal.offlineaiassistant.ui.features.about.AboutScreen
import com.hazratbilal.offlineaiassistant.ui.features.about.OpenSourceLicensesScreen
import com.hazratbilal.offlineaiassistant.ui.features.chat.ChatScreen
import com.hazratbilal.offlineaiassistant.ui.features.model_selection.ModelSelectionScreen
import com.hazratbilal.offlineaiassistant.ui.features.settings.SettingsScreen
import com.hazratbilal.offlineaiassistant.ui.features.welcome.WelcomeScreen
import androidx.core.net.toUri

@Composable
fun AuthNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.WELCOME) {
            WelcomeScreen(
                onChooseClick = {
                    navController.navigate(Routes.MODEL_SELECTION)
                }
            )
        }

        composable(Routes.MODEL_SELECTION) {
            ModelSelectionScreen(
                onSwitchComplete = {
                    navController.navigate(Routes.CHAT) {
                        popUpTo(Routes.WELCOME) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.CHANGE_MODEL) {
            ModelSelectionScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onSwitchComplete = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.CHAT) {
            ChatScreen(
                onSettingsClick = {
                    navController.navigate(Routes.SETTINGS)
                }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onChangeModelClick = {
                    navController.navigate(Routes.CHANGE_MODEL)
                },
                onAboutClick = {
                    navController.navigate(Routes.ABOUT)
                },
                onOpenSourceLicensesClick = {
                    navController.navigate(Routes.OPEN_SOURCE_LICENSES)
                }
            )
        }

        composable(Routes.ABOUT) {
            val context = LocalContext.current

            AboutScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onPortfolioClick = {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            "https://hazratbilal.com".toUri()
                        )
                    )
                },
                onGithubClick = {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            "https://github.com/its-hazratbilal".toUri()
                        )
                    )
                },
                onLinkedInClick = {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            "https://linkedin.com/in/its-hazratbilal".toUri()
                        )
                    )
                }
            )
        }

        composable(Routes.OPEN_SOURCE_LICENSES) {
            val context = LocalContext.current

            OpenSourceLicensesScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onLibraryClick = { url ->
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW, url.toUri())
                    )
                }
            )
        }

    }
}