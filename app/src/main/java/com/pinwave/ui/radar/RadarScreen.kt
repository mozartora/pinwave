package com.pinwave.ui.radar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pinwave.core.ui.UiState
import com.pinwave.core.ui.components.CategoryDot
import com.pinwave.core.ui.components.FriendlyError
import com.pinwave.core.ui.components.LoadingPulse
import com.pinwave.core.ui.components.OverlineLabel
import com.pinwave.core.ui.components.RadarCanvas
import com.pinwave.core.ui.components.TrendRow
import com.pinwave.core.ui.theme.PulseTextFaint
import com.pinwave.core.ui.theme.PulseTextMuted

@Composable
fun RadarScreen(
    onTrendClick: (String) -> Unit,
    viewModel: RadarViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
    ) {
        OverlineLabel("Trend Radar")
        Spacer(Modifier.height(6.dp))
        Text(
            text = "What's moving",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(12.dp))

        when (val current = state) {
            is UiState.Loading -> Box(Modifier.weight(1f)) { LoadingPulse() }
            is UiState.Error -> Box(Modifier.weight(1f)) {
                FriendlyError(current.message, onRetry = viewModel::load)
            }
            is UiState.Success -> {
                val data = current.data
                RadarCanvas(
                    entries = data.entries,
                    onCategoryTap = { viewModel.selectCategory(it) },
                    modifier = Modifier.weight(1f, fill = false),
                )
                Spacer(Modifier.height(12.dp))

                val detected by animateIntAsState(
                    targetValue = data.totalDetected,
                    label = "trendCount",
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$detected trends detected",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    if (data.demo) {
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "Demo data",
                            style = MaterialTheme.typography.labelSmall,
                            color = PulseTextFaint,
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Node distance and size are computed by Pinwave from trend activity.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PulseTextMuted,
                )

                AnimatedVisibility(visible = selectedCategory != null) {
                    selectedCategory?.let { category ->
                        val categoryTrends = data.trends
                            .filter { it.category == category }
                            .sortedByDescending { it.growthPercent }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 14.dp)
                                .heightIn(max = 300.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                CategoryDot(category)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = category.label,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.weight(1f),
                                )
                                TextButton(onClick = { viewModel.selectCategory(null) }) {
                                    Text("Clear", color = MaterialTheme.colorScheme.primary)
                                }
                            }
                            categoryTrends.forEach { trend ->
                                TrendRow(
                                    trend = trend,
                                    onClick = { onTrendClick(trend.id) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
