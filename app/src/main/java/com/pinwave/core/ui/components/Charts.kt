package com.pinwave.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.pinwave.core.ui.theme.PulseOutline
import com.pinwave.core.ui.theme.PulseTextFaint
import com.pinwave.domain.model.MomentumPoint
import com.pinwave.domain.model.TrendStatus

/**
 * Smooth momentum line chart. Renders whatever series the repository
 * provides — the spec forbids fabricating numbers, so this chart never
 * invents points; empty series render a friendly message instead.
 */
@Composable
fun MomentumLineChart(
    points: List<MomentumPoint>,
    status: TrendStatus,
    modifier: Modifier = Modifier,
) {
    if (points.size < 2) {
        Text(
            "Insufficient historical Pinterest data",
            style = MaterialTheme.typography.bodyMedium,
            color = PulseTextFaint,
            modifier = modifier.padding(16.dp),
        )
        return
    }
    val color = statusColor(status)
    var size by remember { mutableStateOf(IntSize.Zero) }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .onSizeChanged { size = it },
    ) {
        if (size.width == 0) return@Canvas
        val values = points.map { it.value }
        val min = values.min()
        val max = values.max()
        val range = (max - min).takeIf { it > 0 } ?: 1f
        val stepX = size.width.toFloat() / (values.size - 1)
        val usableH = size.height * 0.78f
        val topPad = size.height * 0.11f

        fun offset(i: Int): Offset = Offset(
            x = i * stepX,
            y = topPad + (1f - (values[i] - min) / range) * usableH,
        )

        val path = Path()
        val fillPath = Path()
        val pts = values.indices.map { offset(it) }

        path.moveTo(pts.first().x, pts.first().y)
        fillPath.moveTo(pts.first().x, size.height.toFloat())
        fillPath.lineTo(pts.first().x, pts.first().y)
        for (i in 1 until pts.size) {
            val prev = pts[i - 1]
            val curr = pts[i]
            val midX = (prev.x + curr.x) / 2f
            path.cubicTo(midX, prev.y, midX, curr.y, curr.x, curr.y)
            fillPath.cubicTo(midX, prev.y, midX, curr.y, curr.x, curr.y)
        }
        fillPath.lineTo(pts.last().x, size.height.toFloat())
        fillPath.close()

        drawPath(
            fillPath,
            Brush.verticalGradient(
                0f to color.copy(alpha = 0.25f),
                1f to color.copy(alpha = 0f),
            ),
        )
        drawPath(path, color, style = Stroke(width = 5f, cap = StrokeCap.Round))

        val last = pts.last()
        drawCircle(color.copy(alpha = 0.3f), radius = 16f, center = last)
        drawCircle(color, radius = 7f, center = last)
    }
}

/**
 * Weekly activity heatmap. When [intensities] is null the caller has no real
 * historical data, and we say so instead of inventing any (spec §15).
 */
@Composable
fun TrendHeatmap(
    days: List<String>,
    intensities: List<Float>?,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (intensities == null) {
            Text(
                "Insufficient historical Pinterest data",
                style = MaterialTheme.typography.bodyMedium,
                color = PulseTextFaint,
            )
            return@Column
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            days.forEachIndexed { i, day ->
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        day,
                        style = MaterialTheme.typography.labelSmall,
                        color = PulseTextFaint,
                    )
                    val v = intensities.getOrElse(i) { 0f }.coerceIn(0f, 1f)
                    androidx.compose.foundation.layout.Box(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.10f + 0.55f * v),
                            ),
                    )
                }
            }
        }
    }
}
