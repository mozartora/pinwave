package com.pinwave.core.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pinwave.core.ui.theme.PulseFalling
import com.pinwave.core.ui.theme.PulseRising
import com.pinwave.core.ui.theme.PulseStable
import com.pinwave.domain.model.TrendStatus
import kotlin.math.roundToInt

fun statusColor(status: TrendStatus): Color = when (status) {
    TrendStatus.RISING, TrendStatus.NEW -> PulseRising
    TrendStatus.STABLE -> PulseStable
    TrendStatus.FALLING -> PulseFalling
}

fun statusArrow(status: TrendStatus): String = when (status) {
    TrendStatus.RISING, TrendStatus.NEW -> "↗"
    TrendStatus.STABLE -> "→"
    TrendStatus.FALLING -> "↘"
}

/**
 * Momentum badge with a count-up animation (spec §30).
 * Direction is always conveyed by glyph + label, never color alone (§40).
 */
@Composable
fun MomentumBadge(
    growthPercent: Int,
    status: TrendStatus,
    modifier: Modifier = Modifier,
    showLabel: Boolean = false,
    large: Boolean = false,
) {
    val animated = remember(growthPercent) { Animatable(0f) }
    LaunchedEffect(growthPercent) {
        animated.animateTo(growthPercent.toFloat(), spring(dampingRatio = 0.8f, stiffness = 60f))
    }
    val color = statusColor(status)
    val style = if (large) MaterialTheme.typography.displayMedium else MaterialTheme.typography.titleMedium

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "${statusArrow(status)} ${animated.value.roundToInt()}%",
            style = style,
            fontWeight = FontWeight.Bold,
            color = color,
        )
        if (showLabel) {
            Spacer(Modifier.width(8.dp))
            Text(
                text = status.label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
