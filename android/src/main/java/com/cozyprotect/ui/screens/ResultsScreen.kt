package com.cozyprotect.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cozyprotect.ui.components.AppBackground
import com.cozyprotect.ui.components.AppButtonPrimary
import com.cozyprotect.ui.components.ScreenScaffoldWithBannerAd
import com.cozyprotect.ui.theme.CozyBrown

@Composable
fun ResultsScreen(onNext: () -> Unit) {
    ScreenScaffoldWithBannerAd { padding ->
        AppBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Stage Clear!", style = MaterialTheme.typography.headlineLarge, color = CozyBrown)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "★★★", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(20.dp))
                AppButtonPrimary(text = "Next", onClick = onNext)
            }
        }
    }
}
