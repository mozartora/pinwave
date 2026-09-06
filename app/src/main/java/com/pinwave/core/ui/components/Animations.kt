package com.pinwave.core.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset

/**
 * Gentle entrance used by cards across the app: fade + slight rise + scale.
 * [index] staggers items in lists. Respects the animator-duration scale the
 * system applies for reduced-motion users automatically via Compose.
 */
@Composable
fun AppearAnimation(
    index: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay((index * 60L).coerceAtMost(480L))
        progress.animateTo(1f, spring(dampingRatio = 0.82f, stiffness = 260f))
    }
    val p = progress.value
    androidx.compose.foundation.layout.Box(
        modifier = modifier
            .alpha(p)
            .graphicsLayer {
                translationY = (1f - p) * 36f
                scaleX = 0.96f + 0.04f * p
                scaleY = 0.96f + 0.04f * p
            },
    ) {
        content()
    }
}

/** Soft looping pulse, e.g. for the radar center or a "live" indicator. */
@Composable
fun pulseAlpha(): Float {
    val transition = rememberInfiniteTransition(label = "pulse")
    val value by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulseAlpha",
    )
    return value
}
