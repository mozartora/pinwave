package com.pinwave.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.pinwave.core.ui.theme.PulseDimens
import com.pinwave.core.ui.theme.PulseSurface
import com.pinwave.core.ui.theme.PulseSurfaceHigh
import com.pinwave.domain.model.Trend

private val scrim = Brush.verticalGradient(
    0f to Color.Transparent,
    0.45f to Color.Transparent,
    1f to Color(0xE60A0A0D),
)

/** Large, image-dominant trend card — the hero of Home and Discovery. */
@Composable
fun TrendCard(
    trend: Trend,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    aspect: Float = 0.86f,
    compact: Boolean = false,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspect)
            .clip(RoundedCornerShape(PulseDimens.CardRadius))
            .background(PulseSurfaceHigh)
            .clickable(onClick = onClick)
            .semantics {
                contentDescription =
                    "${trend.title}, ${trend.status.label}, ${trend.growthPercent} percent"
            },
    ) {
        AsyncImage(
            model = trend.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(Modifier.fillMaxSize().background(scrim))
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(if (compact) 14.dp else 20.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CategoryDot(trend.category)
                Spacer(Modifier.width(8.dp))
                OverlineLabel(trend.category.label, color = Color(0xCCFFFFFF))
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = trend.title.uppercase(),
                style = if (compact) {
                    MaterialTheme.typography.titleLarge.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Serif)
                } else {
                    MaterialTheme.typography.headlineLarge
                },
                color = Color.White,
            )
            Spacer(Modifier.height(6.dp))
            MomentumBadge(
                growthPercent = trend.growthPercent,
                status = trend.status,
                showLabel = !compact,
            )
        }
    }
}

/** Compact list-row variant used in radar zoom, search results, saved lists. */
@Composable
fun TrendRow(
    trend: Trend,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(PulseDimens.CardRadiusSmall))
            .background(PulseSurface)
            .clickable(onClick = onClick)
            .padding(12.dp)
            .semantics {
                contentDescription =
                    "${trend.title}, ${trend.status.label}, ${trend.growthPercent} percent"
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = "https://picsum.photos/seed/${trend.imageSeed}/200/240",
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(52.dp)
                .height(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(PulseSurfaceHigh),
        )
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(trend.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Row(verticalAlignment = Alignment.CenterVertically) {
                CategoryDot(trend.category)
                Spacer(Modifier.width(6.dp))
                Text(
                    trend.category.label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (trailing != null) {
            trailing()
        } else {
            MomentumBadge(growthPercent = trend.growthPercent, status = trend.status)
        }
    }
}
