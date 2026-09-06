package com.pinwave.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val PulseDarkScheme = darkColorScheme(
    primary = PulseAccent,
    onPrimary = PulseBackground,
    primaryContainer = PulseAccentSoft,
    onPrimaryContainer = PulseAccent,
    background = PulseBackground,
    onBackground = PulseText,
    surface = PulseSurface,
    onSurface = PulseText,
    surfaceVariant = PulseSurfaceHigh,
    onSurfaceVariant = PulseTextMuted,
    surfaceContainerHighest = PulseSurfaceHigh,
    outline = PulseOutline,
    outlineVariant = PulseOutline,
    secondary = PulseTextMuted,
    onSecondary = PulseText,
)

object PulseDimens {
    val CardRadius = 24.dp
    val CardRadiusSmall = 16.dp
    val ScreenPadding = 20.dp
    val CardSpacing = 14.dp
}

/**
 * Pinwave is dark-first (spec §4). A light scheme is intentionally not
 * shipped yet; the system setting is respected structurally but renders the
 * same curated dark palette until a designed light theme exists.
 */
@Composable
fun PinwaveTheme(content: @Composable () -> Unit) {
    isSystemInDarkTheme() // reserved: light theme lands with the Appearance setting
    MaterialTheme(
        colorScheme = PulseDarkScheme,
        typography = PulseTypography,
        content = content,
    )
}
