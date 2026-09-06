package com.pinwave.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.pinwave.core.ui.theme.PulseBackground
import com.pinwave.domain.model.Trend

/**
 * The shareable trend card (spec §46). Rendered inside a graphics layer by
 * the share flow so it can be exported as a bitmap. Branding is Pinwave's
 * own — nothing here implies Pinterest endorsement (§25).
 */
@Composable
fun ShareTrendCard(trend: Trend, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.72f)
            .clip(RoundedCornerShape(28.dp))
            .background(PulseBackground),
    ) {
        AsyncImage(
            model = trend.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color(0xCC0E0E11),
                        0.45f to Color.Transparent,
                        1f to Color(0xF20E0E11),
                    ),
                ),
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(26.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                "PINWAVE",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.8f),
            )
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    trend.title.uppercase(),
                    style = MaterialTheme.typography.displayMedium,
                    color = Color.White,
                )
                Spacer(Modifier.height(10.dp))
                MomentumBadge(trend.growthPercent, trend.status, large = true)
                Spacer(Modifier.height(4.dp))
                Text(
                    trend.status.label.uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.75f),
                )
                Spacer(Modifier.height(18.dp))
                Text(
                    "See what's next.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.6f),
                    textAlign = TextAlign.Start,
                )
            }
        }
    }
}
