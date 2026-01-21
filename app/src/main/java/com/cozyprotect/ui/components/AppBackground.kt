package com.cozyprotect.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.cozyprotect.ui.theme.CozyCream
import com.cozyprotect.ui.theme.CozyLavender
import com.cozyprotect.ui.theme.CozyMint
import com.cozyprotect.ui.theme.CozyPeach
import kotlin.random.Random

@Composable
fun AppBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val transition = rememberInfiniteTransition(label = "background")
    val floatOffset by transition.animateFloat(
        initialValue = -20f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    val speckPositions = remember {
        List(36) { Offset(Random.nextFloat(), Random.nextFloat()) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(CozyCream, CozyMint)
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    listOf(CozyCream, CozyLavender)
                )
            )
            drawCircle(
                color = CozyPeach.copy(alpha = 0.4f),
                radius = size.minDimension * 0.35f,
                center = Offset(size.width * 0.15f, size.height * 0.2f + floatOffset)
            )
            drawCircle(
                color = CozyLavender.copy(alpha = 0.35f),
                radius = size.minDimension * 0.45f,
                center = Offset(size.width * 0.85f, size.height * 0.15f - floatOffset)
            )
            drawCircle(
                color = CozyMint.copy(alpha = 0.3f),
                radius = size.minDimension * 0.5f,
                center = Offset(size.width * 0.5f, size.height * 0.85f + floatOffset)
            )

            speckPositions.forEach { unit ->
                val x = unit.x * size.width
                val y = unit.y * size.height
                drawCircle(
                    color = Color.White.copy(alpha = 0.08f),
                    radius = 2f,
                    center = Offset(x, y)
                )
            }

            drawRect(
                color = Color.White.copy(alpha = 0.05f),
                topLeft = Offset(0f, 0f),
                size = Size(size.width, size.height)
            )
        }
        content()
    }
}
