package com.cozyprotect.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cozyprotect.ui.components.AppBackground
import com.cozyprotect.ui.components.AppButtonSecondary
import com.cozyprotect.ui.components.ScreenScaffoldWithBannerAd
import com.cozyprotect.ui.theme.CozyBrown

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val music = remember { mutableStateOf(true) }
    val sfx = remember { mutableStateOf(true) }
    val vibration = remember { mutableStateOf(true) }

    ScreenScaffoldWithBannerAd { padding ->
        AppBackground {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(text = "Settings", style = MaterialTheme.typography.headlineMedium, color = CozyBrown)
                ToggleRow(label = "Music", checked = music.value) { music.value = it }
                ToggleRow(label = "SFX", checked = sfx.value) { sfx.value = it }
                ToggleRow(label = "Vibration", checked = vibration.value) { vibration.value = it }
                Spacer(modifier = Modifier.height(8.dp))
                AppButtonSecondary(text = "Back", onClick = onBack)
            }
        }
    }
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge, color = CozyBrown)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
