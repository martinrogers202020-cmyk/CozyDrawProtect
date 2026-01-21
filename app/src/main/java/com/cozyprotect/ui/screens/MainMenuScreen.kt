package com.cozyprotect.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.cozyprotect.ui.components.AppBackground
import com.cozyprotect.ui.components.AppButtonPrimary
import com.cozyprotect.ui.components.AppButtonSecondary
import com.cozyprotect.ui.components.FloatingSparkles
import com.cozyprotect.ui.components.MochiMascot
import com.cozyprotect.ui.components.ScreenScaffoldWithBannerAd
import com.cozyprotect.ui.theme.CozyBrown

@Composable
fun MainMenuScreen(
    onStartAdventure: () -> Unit,
    onStages: () -> Unit,
    onSettings: () -> Unit
) {
    ScreenScaffoldWithBannerAd { padding ->
        AppBackground {
            FloatingSparkles(modifier = Modifier.matchParentSize())
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxWidth()
            ) {
                LogoLockup(modifier = Modifier.align(Alignment.TopCenter))
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    MochiMascot(
                        modifier = Modifier
                            .size(260.dp)
                            .shadow(12.dp, MaterialTheme.shapes.large)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Meet Mochi",
                        style = MaterialTheme.typography.headlineLarge,
                        color = CozyBrown
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Draw cozy shields to keep Mochi safe from gentle hazards.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = CozyBrown,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    AppButtonPrimary(
                        text = "Start Adventure",
                        icon = {
                            androidx.compose.material3.Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        },
                        onClick = onStartAdventure,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AppButtonSecondary(text = "Stages", onClick = onStages)
                        AppButtonSecondary(text = "Settings", onClick = onSettings)
                    }
                }
            }
        }
    }
}

@Composable
private fun LogoLockup(modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        androidx.compose.foundation.Canvas(modifier = Modifier.size(26.dp)) {
            drawCircle(color = CozyBrown)
            drawCircle(color = androidx.compose.ui.graphics.Color.White, radius = size.minDimension * 0.22f)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Cozy Draw Protect", style = MaterialTheme.typography.titleMedium, color = CozyBrown)
    }
}
