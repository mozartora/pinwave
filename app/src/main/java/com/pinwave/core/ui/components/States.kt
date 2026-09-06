package com.pinwave.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pinwave.core.ui.theme.PulseTextFaint
import com.pinwave.core.ui.theme.PulseTextMuted

/** Friendly empty state (spec §31) — never a blank screen. */
@Composable
fun EmptyState(
    glyph: String,
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    ctaLabel: String? = null,
    onCta: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(glyph, style = MaterialTheme.typography.displayMedium, modifier = Modifier.alpha(pulseAlpha()))
        Spacer(Modifier.height(18.dp))
        Text(title, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(
            body,
            style = MaterialTheme.typography.bodyLarge,
            color = PulseTextMuted,
            textAlign = TextAlign.Center,
        )
        if (ctaLabel != null && onCta != null) {
            Spacer(Modifier.height(20.dp))
            TextButton(onClick = onCta) {
                Text(ctaLabel, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

/** Friendly error state (spec §32) — technical detail stays in logs. */
@Composable
fun FriendlyError(
    message: String,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("◎", style = MaterialTheme.typography.displayMedium, color = PulseTextFaint)
        Spacer(Modifier.height(18.dp))
        Text(
            message,
            style = MaterialTheme.typography.bodyLarge,
            color = PulseTextMuted,
            textAlign = TextAlign.Center,
        )
        if (onRetry != null) {
            Spacer(Modifier.height(20.dp))
            TextButton(onClick = onRetry) {
                Text("Try again", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

/** Minimal loading state: three softly pulsing dots, no spinner (§30). */
@Composable
fun LoadingPulse(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            "• • •",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.alpha(pulseAlpha()),
        )
    }
}
