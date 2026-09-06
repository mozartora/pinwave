package com.pinwave.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pinwave.core.ui.components.OverlineLabel
import com.pinwave.core.ui.theme.PulseAccent
import com.pinwave.core.ui.theme.PulseDimens
import com.pinwave.core.ui.theme.PulseFalling
import com.pinwave.core.ui.theme.PulseSurface
import com.pinwave.core.ui.theme.PulseTextMuted

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onPaywall: () -> Unit,
    onLegal: (String) -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val pinterestConnected by viewModel.pinterestConnected.collectAsStateWithLifecycle()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsStateWithLifecycle()
    val appearance by viewModel.appearance.collectAsStateWithLifecycle()
    val plan by viewModel.plan.collectAsStateWithLifecycle()

    var showDisconnectConfirm by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showAppearanceDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(PulseDimens.ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }

        item { OverlineLabel("Pinterest") }
        item {
            SettingsCard {
                SettingsRow(
                    title = "Pinterest",
                    subtitle = if (pinterestConnected) "Connected ✓" else "Not connected",
                ) {
                    TextButton(
                        onClick = {
                            if (pinterestConnected) showDisconnectConfirm = true
                            else viewModel.connectDemo()
                        },
                    ) {
                        Text(
                            if (pinterestConnected) "Disconnect" else "Connect",
                            color = PulseAccent,
                        )
                    }
                }
            }
        }

        item { OverlineLabel("AI") }
        item {
            SettingsCard {
                SettingsRow(
                    title = "AI preferences",
                    subtitle = "Analysis runs on trends you open",
                ) {
                    Text("›", style = MaterialTheme.typography.titleLarge, color = PulseTextMuted)
                }
            }
        }

        item { OverlineLabel("Notifications") }
        item {
            SettingsCard {
                SettingsRow(
                    title = "Trending alerts",
                    subtitle = "A gentle ping when your niches heat up",
                ) {
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = viewModel::setNotifications,
                        colors = SwitchDefaults.colors(checkedTrackColor = PulseAccent),
                    )
                }
            }
        }

        item { OverlineLabel("Appearance") }
        item {
            SettingsCard {
                SettingsRow(
                    title = "Appearance",
                    subtitle = appearanceLabel(appearance),
                    onClick = { showAppearanceDialog = true },
                ) {
                    Text("›", style = MaterialTheme.typography.titleLarge, color = PulseTextMuted)
                }
            }
        }

        item { OverlineLabel("Account") }
        item {
            SettingsCard {
                SettingsRow(
                    title = "Subscription",
                    subtitle = plan.label,
                    onClick = onPaywall,
                ) {
                    Text("›", style = MaterialTheme.typography.titleLarge, color = PulseTextMuted)
                }
                SettingsRow(
                    title = "Delete account",
                    titleColor = PulseFalling,
                    subtitle = "Clears local data and disconnects Pinterest",
                    onClick = { showDeleteConfirm = true },
                )
            }
        }

        item { OverlineLabel("Legal") }
        item {
            SettingsCard {
                SettingsRow(title = "Privacy Policy", onClick = { onLegal("privacy") }) {
                    Chevron()
                }
                SettingsRow(title = "Terms of Service", onClick = { onLegal("terms") }) {
                    Chevron()
                }
                SettingsRow(title = "Pinterest API Usage", onClick = { onLegal("pinterest_api") }) {
                    Chevron()
                }
                SettingsRow(title = "Data & Permissions", onClick = { onLegal("data") }) {
                    Chevron()
                }
            }
        }
    }

    if (showDisconnectConfirm) {
        ConfirmDialog(
            title = "Disconnect Pinterest?",
            body = "Pinwave will fall back to demo trends until you connect again.",
            confirmLabel = "Disconnect",
            onConfirm = {
                viewModel.disconnect()
                showDisconnectConfirm = false
            },
            onDismiss = { showDisconnectConfirm = false },
        )
    }

    if (showDeleteConfirm) {
        ConfirmDialog(
            title = "Delete account?",
            body = "This clears your local data and disconnects Pinterest. There is no cloud account in this demo build, so nothing else is stored.",
            confirmLabel = "Delete",
            onConfirm = {
                viewModel.deleteAccount()
                showDeleteConfirm = false
            },
            onDismiss = { showDeleteConfirm = false },
        )
    }

    if (showAppearanceDialog) {
        AppearanceDialog(
            current = appearance,
            onSelect = {
                viewModel.setAppearance(it)
                showAppearanceDialog = false
            },
            onDismiss = { showAppearanceDialog = false },
        )
    }
}

private fun appearanceLabel(value: String): String = when (value) {
    "light" -> "Light — coming soon"
    "system" -> "System"
    else -> "Dark"
}

@Composable
private fun Chevron() {
    Text("›", style = MaterialTheme.typography.titleLarge, color = PulseTextMuted)
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(PulseSurface),
    ) {
        content()
    }
}

@Composable
private fun SettingsRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = titleColor)
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = PulseTextMuted)
            }
        }
        if (trailing != null) {
            trailing()
        }
    }
}

@Composable
private fun ConfirmDialog(
    title: String,
    body: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, style = MaterialTheme.typography.titleLarge) },
        text = { Text(body, style = MaterialTheme.typography.bodyMedium, color = PulseTextMuted) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmLabel, color = PulseAccent)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = PulseTextMuted)
            }
        },
    )
}

@Composable
private fun AppearanceDialog(
    current: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val options = listOf(
        "dark" to "Dark",
        "light" to "Light — coming soon",
        "system" to "System",
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Appearance", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column {
                options.forEach { (value, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelect(value) },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = current == value,
                            onClick = { onSelect(value) },
                        )
                        Text(
                            label,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(start = 4.dp),
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done", color = PulseAccent)
            }
        },
    )
}
