package com.bebralingo.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.bebralingo.ui.theme.*
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

/**
 * Анимация конфетти — падающие разноцветные частицы поверх контента.
 *
 * @param visible показывает/скрывает анимацию
 * @param particleCount количество частиц
 */
@Composable
fun ConfettiBurst(
    visible: Boolean,
    modifier: Modifier = Modifier,
    particleCount: Int = 70
) {
    if (!visible) return

    val particles = remember(particleCount) {
        List(particleCount) {
            ParticleParams(
                startX = Random.nextFloat(),
                phase = Random.nextFloat(),
                fallSpeed = 0.6f + Random.nextFloat() * 0.8f,
                swayAmp = 0.02f + Random.nextFloat() * 0.06f,
                swayFreq = 1.5f + Random.nextFloat() * 2.5f,
                color = CONFETTI_COLORS[Random.nextInt(CONFETTI_COLORS.size)],
                size = 6f + Random.nextFloat() * 10f
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "confetti")
    val time by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "confetti_time"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        particles.forEach { p ->
            val t = ((time * p.fallSpeed + p.phase) % 1f)
            val y = -30f + (h + 60f) * t
            val x = p.startX * w + sin(t * PI.toFloat() * p.swayFreq) * p.swayAmp * w
            val fade = if (t > 0.85f) (1f - (t - 0.85f) / 0.15f) else 1f

            drawCircle(
                color = p.color.copy(alpha = fade),
                radius = p.size / 2f,
                center = Offset(x, y)
            )
        }
    }
}

private val CONFETTI_COLORS = listOf(Green, Blue, Orange, Red, Gold, Purple)

private data class ParticleParams(
    val startX: Float,
    val phase: Float,
    val fallSpeed: Float,
    val swayAmp: Float,
    val swayFreq: Float,
    val color: Color,
    val size: Float
)
