package com.pinwave.ui.home

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pinwave.core.ui.UiState
import com.pinwave.core.ui.components.AppearAnimation
import com.pinwave.core.ui.components.CategoryChipRow
import com.pinwave.core.ui.components.FriendlyError
import com.pinwave.core.ui.components.LoadingPulse
import com.pinwave.core.ui.components.OverlineLabel
import com.pinwave.core.ui.components.TrendCard
import com.pinwave.core.ui.components.TrendRow
import com.pinwave.core.ui.theme.PulseSurfaceHigh
import com.pinwave.core.ui.theme.PulseTextFaint
import com.pinwave.core.ui.theme.PulseTextMuted
import com.pinwave.domain.model.Trend
import com.pinwave.domain.model.TrendCategory
import java.time.LocalTime

@Composable
fun HomeScreen(
    onTrendClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onSeeDrop: () -> Unit,
    onCategoryClick: (TrendCategory) -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val demoData by viewModel.demoData.collectAsStateWithLifecycle()

    val greeting = remember {
        when (LocalTime.now().hour) {
            in 5..11 -> "Good morning"
            in 12..17 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item(key = "header") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    OverlineLabel(greeting)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "What's trending today?",
                        style = MaterialTheme.typography.displayMedium,
                    )
                }
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        tint = PulseTextMuted,
                    )
                }
            }
        }

        item(key = "search") {
            SearchBarRow(onClick = onSearchClick)
        }

        when (val s = state) {
            UiState.Loading -> item(key = "loading") {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(320.dp),
                ) {
                    LoadingPulse()
                }
            }
            is UiState.Error -> item(key = "error") {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(320.dp),
                ) {
                    FriendlyError(message = s.message, onRetry = viewModel::load)
                }
            }
            is UiState.Success -> {
                val trends = s.data
                if (trends.isNotEmpty()) {
                    val hero = trends.first()
                    item(key = "hero-label") {
                        OverlineLabel("Trending now")
                    }
                    item(key = "hero") {
                        Column {
                            AppearAnimation(0) {
                                TrendCard(
                                    trend = hero,
                                    onClick = { onTrendClick(hero.id) },
                                    aspect = 0.82f,
                                )
                            }
                            if (demoData) {
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    "Demo data",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PulseTextFaint,
                                )
                            }
                        }
                    }
                    item(key = "for-you-label") {
                        OverlineLabel("For you")
                    }
                    item(key = "for-you") {
                        CategoryChipRow(
                            selected = null,
                            onSelect = { category -> category?.let(onCategoryClick) },
                        )
                    }
                    item(key = "drop-label") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            OverlineLabel("Today's drop", modifier = Modifier.weight(1f))
                            TextButton(onClick = onSeeDrop) {
                                Text("See all", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                    itemsIndexed(
                        trends.sortedByDescending { it.growthPercent }.take(5),
                        key = { _, trend -> trend.id },
                    ) { index, trend ->
                        NumberedTrendRow(
                            number = index + 1,
                            trend = trend,
                            onClick = { onTrendClick(trend.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBarRow(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clip(RoundedCornerShape(50))
            .background(PulseSurfaceHigh)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 13.dp)
            .semantics { contentDescription = "Search trends" },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("🔍", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.width(10.dp))
        Text(
            "Search Pinterest trends",
            style = MaterialTheme.typography.bodyLarge,
            color = PulseTextMuted,
        )
    }
}

@Composable
private fun NumberedTrendRow(
    number: Int,
    trend: Trend,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "%02d".format(number),
            style = MaterialTheme.typography.headlineMedium,
            color = PulseTextFaint,
        )
        Spacer(Modifier.width(14.dp))
        TrendRow(
            trend = trend,
            onClick = onClick,
            modifier = Modifier.weight(1f),
        )
    }
}
