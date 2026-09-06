package com.pinwave.ui.saved

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pinwave.core.ui.components.AppearAnimation
import com.pinwave.core.ui.components.EmptyState
import com.pinwave.core.ui.components.OverlineLabel
import com.pinwave.core.ui.components.TrendRow
import com.pinwave.core.ui.theme.PulseAccent
import com.pinwave.core.ui.theme.PulseDimens
import com.pinwave.core.ui.theme.PulseSurface
import com.pinwave.core.ui.theme.PulseTextMuted
import com.pinwave.domain.model.Trend
import com.pinwave.domain.model.TrendCollection
import kotlinx.coroutines.launch

private val collectionEmojis = listOf("🔥", "👜", "🏠", "✈️", "💡", "⭐️")

@Composable
fun SavedScreen(
    onTrendClick: (String) -> Unit,
    onExploreClick: () -> Unit,
    viewModel: SavedViewModel = hiltViewModel(),
) {
    val savedTrends by viewModel.savedTrends.collectAsStateWithLifecycle()
    val collections by viewModel.collections.collectAsStateWithLifecycle()
    var showNewCollection by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(PulseDimens.ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(PulseDimens.CardSpacing),
    ) {
        item {
            Text(
                text = "Saved",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        item { OverlineLabel("My collections") }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                itemsIndexed(collections, key = { _, pair -> pair.first.id }) { _, (collection, count) ->
                    CollectionCard(collection = collection, count = count)
                }
                item {
                    NewCollectionCard(onClick = { showNewCollection = true })
                }
            }
        }

        item { OverlineLabel("Saved trends") }

        if (savedTrends.isEmpty()) {
            item {
                EmptyState(
                    glyph = "♡",
                    title = "Nothing saved yet.",
                    body = "Tap the heart on any trend and it lives here.",
                    ctaLabel = "Explore trends",
                    onCta = onExploreClick,
                )
            }
        } else {
            itemsIndexed(savedTrends, key = { _, trend -> trend.id }) { index, trend ->
                AppearAnimation(index = index) {
                    SavedTrendRow(
                        trend = trend,
                        onClick = { onTrendClick(trend.id) },
                        onUnsave = { viewModel.toggleSave(trend.id) },
                    )
                }
            }
        }
    }

    if (showNewCollection) {
        NewCollectionDialog(
            onDismiss = { showNewCollection = false },
            onCreate = { name, emoji ->
                viewModel.createCollection(name, emoji)
                showNewCollection = false
            },
        )
    }
}

@Composable
private fun CollectionCard(
    collection: TrendCollection,
    count: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(140.dp)
            .height(110.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(PulseSurface)
            .padding(14.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(collection.emoji, style = MaterialTheme.typography.headlineMedium)
        Column {
            Text(
                collection.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
            )
            Text(
                "$count trends",
                style = MaterialTheme.typography.bodyMedium,
                color = PulseTextMuted,
            )
        }
    }
}

@Composable
private fun NewCollectionCard(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(140.dp)
            .height(110.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(PulseSurface)
            .clickable(onClick = onClick)
            .padding(14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            "+ New",
            style = MaterialTheme.typography.titleMedium,
            color = PulseAccent,
        )
    }
}

@Composable
private fun SavedTrendRow(
    trend: Trend,
    onClick: () -> Unit,
    onUnsave: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }
    TrendRow(
        trend = trend,
        onClick = onClick,
        trailing = {
            IconButton(
                onClick = {
                    scope.launch {
                        scale.animateTo(1.35f, spring(dampingRatio = 0.4f, stiffness = 600f))
                        scale.animateTo(1f, spring(dampingRatio = 0.6f, stiffness = 400f))
                        onUnsave()
                    }
                },
                modifier = Modifier.graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                },
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Remove from saved",
                    tint = PulseAccent,
                )
            }
        },
    )
}

@Composable
private fun NewCollectionDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf(collectionEmojis.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New collection", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    collectionEmojis.forEach { option ->
                        val selected = option == emoji
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selected) PulseAccent.copy(alpha = 0.2f) else PulseSurface)
                                .clickable { emoji = option },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(option, style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onCreate(name, emoji) },
                enabled = name.isNotBlank(),
            ) {
                Text("Create", color = PulseAccent)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = PulseTextMuted)
            }
        },
    )
}
