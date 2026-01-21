package com.cozyprotect.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = CozyLavender,
    onPrimary = CozyBrownDark,
    secondary = CozyMint,
    onSecondary = CozyBrownDark,
    tertiary = CozyPeach,
    onTertiary = CozyBrownDark,
    background = CozyCream,
    onBackground = CozyBrownDark,
    surface = CozyCream,
    onSurface = CozyBrownDark
)

private val DarkColors = darkColorScheme(
    primary = CozyLavender,
    onPrimary = CozyNight,
    secondary = CozyMint,
    onSecondary = CozyNight,
    tertiary = CozyPeach,
    onTertiary = CozyNight,
    background = CozyNight,
    onBackground = CozyCream,
    surface = CozyNight,
    onSurface = CozyCream
)

@Composable
fun CozyTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = CozyTypography,
        shapes = CozyShapes,
        content = content
    )
}
