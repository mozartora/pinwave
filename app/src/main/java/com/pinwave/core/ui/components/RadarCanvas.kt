package com.pinwave.core.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import com.pinwave.core.ui.theme.PulseAccent
import com.pinwave.core.ui.theme.PulseOutline
import com.pinwave.domain.model.TrendCategory
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

data class RadarCategoryEntry(
    val category: TrendCategory,
    val trendCount: Int,
    /** 0..1 — how "hot" the category is, controls distance from center. */
    val intensity: Float,
)

/**
 * The signature radar interface (spec §14). A slow sweep rotates over
 * concentric rings; each category sits on the field with a pulsing node whose
 * size reflects its live trend count. Tapping a node zooms into that
 * category's trends.
 */
@Composable
fun RadarCanvas(
    entries: List<RadarCategoryEntry>,
    onCategoryTap: (TrendCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "radar")
    val sweep by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing)),
        label = "sweep",
    )
    val pulse by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2200, easing = LinearEasing)),
        label = "nodePulse",
    )

    var size by remember { mutableStateOf(IntSize.Zero) }

    // Node positions derive purely from size + entries, so they can be shared
    // between the tap handler and the draw pass without writing state mid-draw.
    val nodePositions: Map<TrendCategory, Pair<Offset, Float>> = remember(size, entries) {
        if (size == IntSize.Zero || entries.isEmpty()) {
            emptyMap()
        } else {
            val w = size.width.toFloat()
            val h = size.height.toFloat()
            val center = Offset(w / 2f, h / 2f)
            val radius = min(w, h) / 2f * 0.92f
            entries.mapIndexed { i, entry ->
                val angle = Math.toRadians((i * (360.0 / entries.size)) - 90.0)
                val dist = radius * (0.35f + 0.55f * (1f - entry.intensity.coerceIn(0f, 1f)))
                val nodeCenter = Offset(
                    x = center.x + (cos(angle) * dist).toFloat(),
                    y = center.y + (sin(angle) * dist).toFloat(),
                )
                val nodeRadius = 10f + entry.trendCount.coerceAtMost(10) * 3f
                entry.category to (nodeCenter to nodeRadius)
            }.toMap()
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .onSizeChanged { size = it }
            .pointerInput(entries) {
                detectTapGestures { tap ->
                    val hit = nodePositions.entries.firstOrNull { (_, value) ->
                        val (center, radius) = value
                        val dx = tap.x - center.x
                        val dy = tap.y - center.y
                        dx * dx + dy * dy <= (radius * 2.6f) * (radius * 2.6f)
                    }
                    hit?.let { onCategoryTap(it.key) }
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxWidth().aspectRatio(1f)) {
            val w = this.size.width
            val h = this.size.height
            val center = Offset(w / 2f, h / 2f)
            val radius = min(w, h) / 2f * 0.92f

            // Rings
            listOf(0.33f, 0.66f, 1f).forEach { f ->
                drawCircle(
                    color = PulseOutline,
                    radius = radius * f,
                    center = center,
                    style = Stroke(width = 1.5f),
                )
            }
            // Cross lines
            drawLine(PulseOutline, center - Offset(radius, 0f), center + Offset(radius, 0f), strokeWidth = 1f)
            drawLine(PulseOutline, center - Offset(0f, radius), center + Offset(0f, radius), strokeWidth = 1f)

            // Rotating sweep wedge + leading edge
            drawArc(
                color = PulseAccent.copy(alpha = 0.16f),
                startAngle = sweep - 45f,
                sweepAngle = 45f,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2f, radius * 2f),
            )
            val edgeRad = Math.toRadians(sweep.toDouble())
            drawLine(
                color = PulseAccent.copy(alpha = 0.6f),
                start = center,
                end = Offset(
                    center.x + (cos(edgeRad) * radius).toFloat(),
                    center.y + (sin(edgeRad) * radius).toFloat(),
                ),
                strokeWidth = 3f,
            )

            // Category nodes placed deterministically around the field.
            entries.forEach { entry ->
                val (nodeCenter, nodeRadius) = nodePositions[entry.category] ?: return@forEach
                val accent = entry.category.accent()
                drawCircle(accent.copy(alpha = 0.16f + 0.10f * pulse), radius = nodeRadius * 2.1f, center = nodeCenter)
                drawCircle(accent.copy(alpha = 0.35f), radius = nodeRadius * (1.2f + 0.25f * pulse), center = nodeCenter)
                drawCircle(accent, radius = nodeRadius * 0.72f, center = nodeCenter)
            }

            // Center pulse
            drawCircle(PulseAccent.copy(alpha = 0.25f * (1f - pulse)), radius = 26f + 30f * pulse, center = center)
            drawCircle(PulseAccent, radius = 8f, center = center)
        }
    }
}
