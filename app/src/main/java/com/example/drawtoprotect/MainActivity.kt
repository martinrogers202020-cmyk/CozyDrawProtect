package com.cozyprotect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cozyprotect.ui.screens.MainMenuScreen
import com.cozyprotect.ui.screens.ResultsScreen
import com.cozyprotect.ui.screens.SettingsScreen
import com.cozyprotect.ui.screens.StageSelectScreen
import com.cozyprotect.ui.theme.CozyTheme
import com.cozyprotect.ui.util.GlobalErrorHandler
import com.cozyprotect.ui.util.safeNavigate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Logcat stacktrace (when pressing Play before fix):
        // FATAL EXCEPTION: main
        // android.content.ActivityNotFoundException: Unable to find explicit activity class
        // {com.cozyprotect/com.cozyprotect.GameActivity}; have you declared this activity in
        // your AndroidManifest.xml?
        // 	at android.app.Instrumentation.checkStartActivityResult(Instrumentation.java:2236)
        // 	at android.app.Instrumentation.execStartActivity(Instrumentation.java:1912)
        // 	at android.app.Activity.startActivityForResult(Activity.java:5292)
        // 	at android.app.Activity.startActivityForResult(Activity.java:5250)
        // 	at android.app.Activity.startActivity(Activity.java:5656)
        // 	at com.cozyprotect.ui.screens.StageSelectScreenKt$PackCard$1.invoke(StageSelectScreen.kt:149)
        // 	at com.cozyprotect.ui.screens.StageSelectScreenKt$PackCard$1.invoke(StageSelectScreen.kt:146)
        // 	...

        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            GlobalErrorHandler.report(throwable)
        }

        val repository = LevelRepository(this)
        setContent {
            CozyTheme {
                val navController = rememberNavController()
                val showDialog = remember { mutableStateOf(true) }
                val errorState by GlobalErrorHandler.errors.collectAsState()

                LaunchedEffect(errorState) {
                    if (errorState != null) {
                        showDialog.value = true
                    }
                }

                if (errorState != null && showDialog.value) {
                    GlobalErrorDialog(
                        throwable = errorState,
                        onDismiss = {
                            showDialog.value = false
                            GlobalErrorHandler.clear()
                        }
                    )
                }

                NavHost(navController = navController, startDestination = "splash") {
                    composable("splash") {
                        SplashScreen {
                            navController.safeNavigate("menu")
                        }
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
                                        IllegalStateException(\"GameActivity missing from Manifest\")
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
            TextButton(onClick = onDismiss) {
                Text(text = "Okay")
            }
        },
        title = { Text(text = "Something went wrong") },
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
        kotlinx.coroutines.delay(900)
        onFinish()
    }
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
            .background(
                androidx.compose.ui.graphics.Brush.verticalGradient(
                    listOf(
                        com.cozyprotect.ui.theme.CozyCream,
                        com.cozyprotect.ui.theme.CozyLavender
                    )
                )
            ),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Text(
            text = "Cozy Draw Protect",
            style = MaterialTheme.typography.headlineMedium,
            color = com.cozyprotect.ui.theme.CozyBrown
        )
    }
}
