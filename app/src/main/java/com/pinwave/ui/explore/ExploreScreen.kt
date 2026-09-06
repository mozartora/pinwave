package com.pinwave.ui.explore

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pinwave.core.ui.UiState
import com.pinwave.core.ui.components.AppearAnimation
import com.pinwave.core.ui.components.CategoryChipRow
import com.pinwave.core.ui.components.FriendlyError
import com.pinwave.core.ui.components.LoadingPulse
import com.pinwave.core.ui.components.TrendCard
import com.pinwave.core.ui.theme.PulseSurfaceHigh
import com.pinwave.core.ui.theme.PulseTextFaint
import com.pinwave.core.ui.theme.PulseTextMuted

@Composable
fun ExploreScreen(
    onTrendClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    viewModel: ExploreViewModel = hiltViewModel(),
) {
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val demo by viewModel.demo.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 20.dp),
    ) {
        Text(
            text = "Discover",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        Spacer(Modifier.height(16.dp))

        // Non-editable search affordance — taps through to the real search screen.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(50))
                .background(PulseSurfaceHigh)
                .clickable(onClick = onSearchClick)
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = PulseTextMuted,
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = "Search trends, niches, ideas…",
                style = MaterialTheme.typography.bodyLarge,
                color = PulseTextMuted,
            )
        }
        Spacer(Modifier.height(16.dp))

        CategoryChipRow(
            selected = selectedCategory,
            onSelect = viewModel::setCategory,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        if (demo) {
            Text(
                text = "Demo data",
                style = MaterialTheme.typography.labelSmall,
                color = PulseTextFaint,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            )
        } else {
            Spacer(Modifier.height(12.dp))
        }

        Box(Modifier.weight(1f)) {
            when (val current = state) {
                is UiState.Loading -> LoadingPulse()
                is UiState.Error -> FriendlyError(current.message)
                is UiState.Success -> LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalItemSpacing = 10.dp,
                ) {
                    itemsIndexed(current.data, key = { _, trend -> trend.id }) { index, trend ->
                        AppearAnimation(index % 8) {
                            TrendCard(
                                trend = trend,
                                onClick = { onTrendClick(trend.id) },
                                aspect = if (index % 2 == 0) 0.72f else 0.86f,
                                compact = true,
                            )
                        }
                    }
                }
            }
        }
    }
}
