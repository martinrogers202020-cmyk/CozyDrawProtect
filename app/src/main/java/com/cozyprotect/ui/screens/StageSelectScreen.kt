package com.cozyprotect.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cozyprotect.LevelDefinition
import com.cozyprotect.LevelPack
import com.cozyprotect.LevelRepository
import com.cozyprotect.PackProgress
import com.cozyprotect.ui.components.AppBackground
import com.cozyprotect.ui.components.AppButtonPrimary
import com.cozyprotect.ui.components.AppButtonSecondary
import com.cozyprotect.ui.components.AppCard
import com.cozyprotect.ui.components.DownloadPackButton
import com.cozyprotect.ui.components.MeadowScene
import com.cozyprotect.ui.components.ScreenScaffoldWithBannerAd
import com.cozyprotect.ui.components.HazardRow
import com.cozyprotect.ui.state.StageSelectUiState
import com.cozyprotect.ui.state.StageSelectViewModel
import com.cozyprotect.ui.theme.CozyBrown
import com.cozyprotect.ui.theme.CozyLavender
import com.cozyprotect.ui.theme.CozyMint
import com.cozyprotect.ui.theme.CozyRose

@Composable
fun StageSelectScreen(
    repository: LevelRepository,
    onPlay: (String, String) -> Unit,
    onBack: () -> Unit
) {
    val factory = remember(repository) {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return StageSelectViewModel(repository) as T
            }
        }
    }
    val viewModel: StageSelectViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()
    val isGrid by viewModel.isGrid.collectAsState()

    ScreenScaffoldWithBannerAd {
        AppBackground {
            Column(
                modifier = Modifier
                    .padding(it)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Stage Select",
                            style = MaterialTheme.typography.headlineMedium,
                            color = CozyBrown
                        )
                        Text(
                            text = "Choose a cozy pack or daily challenge",
                            style = MaterialTheme.typography.bodyMedium,
                            color = CozyBrown
                        )
                    }
                    AppButtonSecondary(text = if (isGrid) "List" else "Grid") {
                        viewModel.toggleGrid()
                    }
                }

                HazardRow(modifier = Modifier.fillMaxWidth().height(32.dp))

                Crossfade(targetState = uiState, label = "stage-select") { state ->
                    when (state) {
                        StageSelectUiState.Loading -> {
                            LoadingSkeletons(isGrid = isGrid)
                        }
                        is StageSelectUiState.Error -> {
                            Text(text = state.message, color = CozyBrown)
                        }
                        is StageSelectUiState.Loaded -> {
                            StageSelectContent(
                                packs = state.packs.ifEmpty { listOf(samplePack()) },
                                isGrid = isGrid,
                                onPlay = onPlay
                            )
                        }
                    }
                }

                DailyChallengeCard(onPlay = { onPlay("daily", "001") })

                DownloadPackButton(
                    text = "Download New Pack",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        viewModel.downloadNewPack("https://example.com/pack_002.json")
                    }
                )

                AppButtonSecondary(text = "Back", onClick = onBack)
            }
        }
    }
}

@Composable
private fun StageSelectContent(
    packs: List<LevelPack>,
    isGrid: Boolean,
    onPlay: (String, String) -> Unit
) {
    if (isGrid) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.height(320.dp)
        ) {
            items(packs) { pack ->
                PackCard(pack = pack, onPlay = onPlay)
            }
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(packs) { pack ->
                PackCard(pack = pack, onPlay = onPlay)
            }
        }
    }
}

@Composable
private fun PackCard(pack: LevelPack, onPlay: (String, String) -> Unit) {
    val levelId = pack.levels.firstOrNull()?.id ?: return
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = CozyLavender.copy(alpha = 0.45f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            MeadowScene(modifier = Modifier.matchParentSize())
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(text = pack.name, style = MaterialTheme.typography.titleLarge, color = CozyBrown)
                        Text(text = "Cozy pack", style = MaterialTheme.typography.bodyMedium)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Badge(text = "${pack.levels.size} Levels")
                        Badge(text = "${pack.progress.completed} / ${pack.levels.size}")
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    repeat(3) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = CozyRose
                        )
                    }
                    Text(text = "${pack.progress.stars} Stars", style = MaterialTheme.typography.bodyMedium)
                }
                LinearProgressIndicator(
                    progress = (pack.progress.completed.toFloat() / pack.levels.size.coerceAtLeast(1)).coerceIn(0f, 1f),
                    color = CozyMint,
                    trackColor = CozyLavender.copy(alpha = 0.3f)
                )
                AppButtonPrimary(
                    text = "Continue",
                    onClick = { onPlay(pack.packId, levelId) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun DailyChallengeCard(onPlay: () -> Unit) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = CozyMint.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "Daily Cozy Challenge", style = MaterialTheme.typography.titleLarge, color = CozyBrown)
            Text(text = "1 gentle level per day · Reward extra stars", style = MaterialTheme.typography.bodyMedium)
            AppButtonPrimary(text = "Play Today's", onClick = onPlay, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun LoadingSkeletons(isGrid: Boolean) {
    val skeletonCount = 2
    if (isGrid) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.height(320.dp)
        ) {
            items(skeletonCount) {
                Box(modifier = Modifier.aspectRatio(1f)) {
                    SkeletonCard()
                }
            }
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(skeletonCount) {
                SkeletonCard()
            }
        }
    }
}

@Composable
private fun SkeletonCard() {
    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(MaterialTheme.shapes.large),
        containerColor = CozyLavender.copy(alpha = 0.3f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(CozyLavender.copy(alpha = 0.2f))
        )
    }
}

@Composable
private fun Badge(text: String) {
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(CozyRose.copy(alpha = 0.7f))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

private fun samplePack(): LevelPack {
    return LevelPack(
        packId = "pack_001",
        name = "Cozy Meadows",
        levels = listOf(LevelDefinition(id = "001", timeToSurvive = 12, drawLimit = 1)),
        progress = PackProgress(completed = 12, stars = 30)
    )
}
