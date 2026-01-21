package com.cozyprotect

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = LevelRepository(this)
        setContent {
            CozyTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "splash") {
                    composable("splash") {
                        SplashScreen {
                            navController.navigate("menu") {
                                popUpTo("splash") { inclusive = true }
                            }
                        }
                    }
                    composable("menu") {
                        MainMenuScreen(
                            onPlay = { navController.navigate("select") },
                            onSettings = { navController.navigate("settings") }
                        )
                    }
                    composable("select") {
                        StageSelectScreen(repository = repository, navController = navController)
                    }
                    composable("settings") {
                        SettingsScreen(onBack = { navController.popBackStack() })
                    }
                    composable("results") {
                        ResultsScreen(onNext = { navController.navigate("select") })
                    }
                }
            }
        }
    }
}

@Composable
fun SplashScreen(onFinish: () -> Unit) {
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(900)
        onFinish()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFFFE8D6), Color(0xFFE6D9FF))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Cozy Draw Protect",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF6E4E3A)
        )
    }
}

@Composable
fun MainMenuScreen(onPlay: () -> Unit, onSettings: () -> Unit) {
    Scaffold(bottomBar = { AdBanner() }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFFFF4E8), Color(0xFFD2F5E8))
                    )
                )
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Meet Mochi",
                style = MaterialTheme.typography.headlineLarge,
                color = Color(0xFF6E4E3A)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Draw cozy shields to keep Mochi safe from gentle hazards.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF6E4E3A)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onPlay) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Start")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onSettings, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE6D9FF))) {
                Text("Settings")
            }
        }
    }
}

@Composable
fun StageSelectScreen(repository: LevelRepository, navController: NavHostController) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val packsState = remember { mutableStateOf<List<LevelPack>>(emptyList()) }
    val loadingState = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        packsState.value = repository.loadAllPacks()
        loadingState.value = false
    }

    Scaffold(bottomBar = { AdBanner() }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFFFF4E8), Color(0xFFE6D9FF))
                    )
                )
                .padding(16.dp)
        ) {
            Text(
                text = "Stage Select",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF6E4E3A)
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (loadingState.value) {
                Text(text = "Loading packs...", color = Color(0xFF6E4E3A))
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(packsState.value) { pack ->
                        PackCard(pack = pack, context = context, onPlay = { levelId ->
                            navController.navigate("menu")
                            context.startActivity(GameActivity.intentFor(context, pack.packId, levelId))
                        })
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = {
                loadingState.value = true
                repository.downloadRemotePack(
                    url = "https://example.com/pack_002.json",
                    onComplete = {
                        scope.launch {
                            packsState.value = repository.loadAllPacks()
                            loadingState.value = false
                        }
                    }
                )
            }) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Download New Pack")
            }
        }
    }
}

@Composable
fun PackCard(pack: LevelPack, context: Context, onPlay: (String) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = pack.name, style = MaterialTheme.typography.titleMedium)
            Text(text = "${pack.levels.size} levels", style = MaterialTheme.typography.bodyMedium)
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            val levelId = pack.levels.firstOrNull()?.id ?: return@Column
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Progress: ${pack.progress.completed}/${pack.levels.size}")
                Button(onClick = { onPlay(levelId) }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Play")
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val music = remember { mutableStateOf(true) }
    val sfx = remember { mutableStateOf(true) }
    val vibration = remember { mutableStateOf(true) }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Brush.verticalGradient(listOf(Color(0xFFFFF4E8), Color(0xFFD2F5E8))))
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(text = "Settings", style = MaterialTheme.typography.headlineMedium)
            ToggleRow(label = "Music", state = music)
            ToggleRow(label = "SFX", state = sfx)
            ToggleRow(label = "Vibration", state = vibration)
            Button(onClick = onBack) {
                Text("Back")
            }
        }
    }
}

@Composable
fun ResultsScreen(onNext: () -> Unit) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Brush.verticalGradient(listOf(Color(0xFFFFF4E8), Color(0xFFE6D9FF))))
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Stage Clear!", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "★★★", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = onNext) {
                Text("Next")
            }
        }
    }
}

@Composable
fun ToggleRow(label: String, state: MutableState<Boolean>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = state.value, onCheckedChange = { state.value = it })
    }
}

@Composable
fun AdBanner() {
    val context = LocalContext.current
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        factory = {
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = "ca-app-pub-3940256099942544/6300978111" // TODO: Replace with production AdMob banner ID.
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun MainMenuPreview() {
    CozyTheme {
        MainMenuScreen(onPlay = {}, onSettings = {})
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun StageSelectPreview() {
    CozyTheme {
        val samplePack = LevelPack(
            packId = "pack_001",
            name = "Cozy Meadows",
            levels = listOf(LevelDefinition(id = "001", timeToSurvive = 12, drawLimit = 1)),
            progress = PackProgress(completed = 12, stars = 30)
        )
        StageSelectContent(listOf(samplePack))
    }
}

@Composable
fun StageSelectContent(packs: List<LevelPack>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(packs) { pack ->
            PackCard(pack = pack, context = LocalContext.current, onPlay = {})
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun SettingsPreview() {
    CozyTheme {
        SettingsScreen(onBack = {})
    }
}

@Composable
fun CozyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = Color(0xFFE6D9FF),
            secondary = Color(0xFFFFD9C5),
            tertiary = Color(0xFFD2F5E8)
        ),
        typography = MaterialTheme.typography,
        content = content
    )
}
