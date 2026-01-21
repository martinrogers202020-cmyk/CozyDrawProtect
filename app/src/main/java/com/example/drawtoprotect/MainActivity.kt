// MainActivity.kt
package com.cozyprotect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cozyprotect.ui.screens.MainMenuScreen
import com.cozyprotect.ui.screens.ResultsScreen
import com.cozyprotect.ui.screens.SettingsScreen
import com.cozyprotect.ui.screens.StageSelectScreen
import com.cozyprotect.ui.theme.CozyBrown
import com.cozyprotect.ui.theme.CozyCream
import com.cozyprotect.ui.theme.CozyLavender
import com.cozyprotect.ui.theme.CozyTheme
import com.cozyprotect.ui.util.GlobalErrorHandler
import com.cozyprotect.ui.util.safeNavigate
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            GlobalErrorHandler.report(throwable)
        }

        val repository = LevelRepository(this)

        setContent {
            CozyTheme {
                val navController = rememberNavController()
                val errorState by GlobalErrorHandler.errors.collectAsState(initial = null)
                var showDialog by remember { mutableStateOf(false) }

                LaunchedEffect(errorState) {
                    showDialog = errorState != null
                }

                if (errorState != null && showDialog) {
                    GlobalErrorDialog(
                        throwable = errorState,
                        onDismiss = {
                            showDialog = false
                            GlobalErrorHandler.clear()
                        }
                    )
                }

                NavHost(navController = navController, startDestination = "splash") {
                    composable("splash") {
                        SplashScreen(onFinish = { navController.safeNavigate("menu") })
                    }
                    composable("menu") {
                        MainMenuScreen(
                            onStartAdventure = { navController.safeNavigate("select") },
                            onStages = { navController.safeNavigate("select") },
                            onSettings = { navController.safeNavigate("settings") }
                        )
                    }
                    composable("select") {
                        StageSelectScreen(
                            repository = repository,
                            onPlay = { packId, levelId ->
                                val intent = GameActivity.intentFor(this@MainActivity, packId, levelId)
                                if (intent.resolveActivity(packageManager) != null) {
                                    startActivity(intent)
                                } else {
                                    GlobalErrorHandler.report(
                                        IllegalStateException("GameActivity missing from Manifest")
                                    )
                                }
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("settings") {
                        SettingsScreen(onBack = { navController.popBackStack() })
                    }
                    composable("results") {
                        ResultsScreen(onNext = { navController.safeNavigate("select") })
                    }
                }
            }
        }
    }
}

@Composable
fun GlobalErrorDialog(throwable: Throwable?, onDismiss: () -> Unit) {
    if (throwable == null) return
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Okay") }
        },
        title = { Text("Something went wrong") },
        text = {
            Text(
                text = throwable.message ?: "We hit a cozy hiccup. Please try again.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    )
}

@Composable
fun SplashScreen(onFinish: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(900)
        onFinish()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(CozyCream, CozyLavender))),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Cozy Draw Protect",
            style = MaterialTheme.typography.headlineMedium,
            color = CozyBrown
        )
    }
}
