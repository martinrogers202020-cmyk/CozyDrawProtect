package com.cozyprotect.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.cozyprotect.ui.theme.CozyBrown
import com.cozyprotect.ui.theme.CozyLavender
import com.cozyprotect.ui.theme.CozyMint
import com.cozyprotect.ui.theme.CozyPeach
import com.cozyprotect.ui.theme.CozyRose

@Composable
fun MochiMascot(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "mochi")
    val floatOffset by transition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )
    val blink by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(200, delayMillis = 2600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blink"
    )

    Canvas(modifier = modifier) {
        val center = center.copy(y = center.y + floatOffset)
        val bodyRadius = size.minDimension * 0.32f
        drawCircle(
            color = CozyPeach,
            radius = bodyRadius,
            center = center
        )
        drawCircle(
            color = CozyRose.copy(alpha = 0.6f),
            radius = bodyRadius * 0.12f,
            center = center + Offset(-bodyRadius * 0.35f, bodyRadius * 0.12f)
        )
        drawCircle(
            color = CozyRose.copy(alpha = 0.6f),
            radius = bodyRadius * 0.12f,
            center = center + Offset(bodyRadius * 0.35f, bodyRadius * 0.12f)
        )

        val earSize = Size(bodyRadius * 0.6f, bodyRadius * 0.7f)
        drawRoundRect(
            color = CozyPeach,
            topLeft = Offset(center.x - bodyRadius * 0.9f, center.y - bodyRadius * 1.2f),
            size = earSize,
            cornerRadius = CornerRadius(earSize.width * 0.4f, earSize.height * 0.4f)
        )
        drawRoundRect(
            color = CozyPeach,
            topLeft = Offset(center.x + bodyRadius * 0.3f, center.y - bodyRadius * 1.2f),
            size = earSize,
            cornerRadius = CornerRadius(earSize.width * 0.4f, earSize.height * 0.4f)
        )

        drawOval(
            color = CozyBrown,
            topLeft = Offset(center.x - bodyRadius * 0.35f, center.y - bodyRadius * 0.1f),
            size = Size(bodyRadius * 0.2f, bodyRadius * 0.12f * blink)
        )
        drawOval(
            color = CozyBrown,
            topLeft = Offset(center.x + bodyRadius * 0.15f, center.y - bodyRadius * 0.1f),
            size = Size(bodyRadius * 0.2f, bodyRadius * 0.12f * blink)
        )
        drawCircle(
            color = CozyBrown,
            radius = bodyRadius * 0.06f,
            center = center + Offset(0f, bodyRadius * 0.08f)
        )

        drawLine(
            color = CozyBrown,
            start = center + Offset(-bodyRadius * 0.05f, bodyRadius * 0.15f),
            end = center + Offset(bodyRadius * 0.05f, bodyRadius * 0.15f),
            strokeWidth = 6f,
            cap = StrokeCap.Round
        )

        val scarfPath = Path().apply {
            moveTo(center.x - bodyRadius * 0.4f, center.y + bodyRadius * 0.3f)
            lineTo(center.x + bodyRadius * 0.4f, center.y + bodyRadius * 0.2f)
            lineTo(center.x + bodyRadius * 0.2f, center.y + bodyRadius * 0.45f)
            lineTo(center.x - bodyRadius * 0.5f, center.y + bodyRadius * 0.45f)
            close()
        }
        drawPath(scarfPath, color = CozyLavender)
    }
}

@Composable
fun FloatingSparkles(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "sparkles")
    val shift by transition.animateFloat(
        initialValue = -20f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkle-shift"
    )
    Canvas(modifier = modifier) {
        repeat(12) { index ->
            val x = (index % 4) * size.width / 4 + 20f
            val y = (index / 4) * size.height / 3 + 20f
            drawCircle(
                color = Color.White.copy(alpha = 0.6f),
                radius = 6f,
                center = Offset(x + shift * (index % 2), y - shift)
            )
        }
    }
}

@Composable
fun MeadowScene(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val hillPath = Path().apply {
            moveTo(0f, size.height * 0.75f)
            quadraticBezierTo(size.width * 0.25f, size.height * 0.6f, size.width * 0.5f, size.height * 0.72f)
            quadraticBezierTo(size.width * 0.75f, size.height * 0.84f, size.width, size.height * 0.7f)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        drawPath(hillPath, color = CozyMint.copy(alpha = 0.6f))

        repeat(6) { index ->
            val flowerX = size.width * (0.1f + index * 0.13f)
            val stemTop = Offset(flowerX, size.height * 0.75f - 10f)
            drawLine(
                color = CozyMint.copy(alpha = 0.8f),
                start = Offset(flowerX, size.height * 0.9f),
                end = stemTop,
                strokeWidth = 6f
            )
            drawCircle(
                color = CozyPeach,
                radius = 12f,
                center = stemTop
            )
            drawCircle(
                color = CozyRose,
                radius = 6f,
                center = stemTop + Offset(0f, -8f)
            )
        }
    }
}

@Composable
fun BeePuffIcon(modifier: Modifier = Modifier, color: Color = CozyLavender) {
    Canvas(modifier = modifier) {
        drawCircle(color = color, radius = size.minDimension * 0.45f)
        drawCircle(
            color = CozyBrown,
            radius = size.minDimension * 0.08f,
            center = center + Offset(-size.minDimension * 0.1f, 0f)
        )
        drawCircle(
            color = CozyBrown,
            radius = size.minDimension * 0.08f,
            center = center + Offset(size.minDimension * 0.1f, 0f)
        )
        drawCircle(
            color = CozyRose,
            radius = size.minDimension * 0.06f,
            center = center + Offset(0f, size.minDimension * 0.15f)
        )
    }
}

@Composable
fun HazardRow(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val spacing = size.width / 4
            repeat(3) { index ->
                drawBeePuff(
                    center = Offset(spacing * (index + 1), size.height / 2),
                    size = size.minDimension * 0.25f
                )
            }
        }
    }
}

private fun DrawScope.drawBeePuff(center: Offset, size: Float) {
    drawCircle(color = CozyLavender, radius = size, center = center)
    drawCircle(color = CozyMint, radius = size * 0.3f, center = center + Offset(-size * 0.3f, -size * 0.2f))
    drawCircle(color = CozyMint, radius = size * 0.2f, center = center + Offset(size * 0.3f, -size * 0.2f))
    drawCircle(color = CozyBrown, radius = size * 0.12f, center = center + Offset(-size * 0.2f, 0f))
    drawCircle(color = CozyBrown, radius = size * 0.12f, center = center + Offset(size * 0.2f, 0f))
}
