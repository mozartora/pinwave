package com.pinwave.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pinwave.core.ui.UiState
import com.pinwave.core.ui.components.CategoryChip
import com.pinwave.core.ui.components.EmptyState
import com.pinwave.core.ui.components.FriendlyError
import com.pinwave.core.ui.components.LoadingPulse
import com.pinwave.core.ui.components.OverlineLabel
import com.pinwave.core.ui.components.TrendRow
import com.pinwave.core.ui.theme.PulseAccent
import com.pinwave.core.ui.theme.PulseSurfaceHigh
import com.pinwave.core.ui.theme.PulseText
import com.pinwave.core.ui.theme.PulseTextFaint
import com.pinwave.core.ui.theme.PulseTextMuted

private val examplePrompts = listOf(
    "What's trending in men's fashion?",
    "Find rising handbag trends",
    "Show me home decor trends",
    "What should I make content about this week?",
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    onTrendClick: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val results by viewModel.results.collectAsStateWithLifecycle()
    val recentSearches by viewModel.recentSearches.collectAsStateWithLifecycle()
    val demo by viewModel.demo.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(top = 8.dp),
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = PulseText,
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = "What are you looking for?",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(20.dp))

        TextField(
            value = query,
            onValueChange = viewModel::onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            shape = CircleShape,
            singleLine = true,
            leadingIcon = { Text("✨") },
            placeholder = {
                Text(
                    text = "summer fashion for men",
                    style = MaterialTheme.typography.bodyLarge,
                    color = PulseTextFaint,
                )
            },
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                imeAction = ImeAction.Search,
            ),
            keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                onSearch = { viewModel.submitSearch() },
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = PulseSurfaceHigh,
                unfocusedContainerColor = PulseSurfaceHigh,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = PulseAccent,
                focusedTextColor = PulseText,
                unfocusedTextColor = PulseText,
            ),
        )
        Spacer(Modifier.height(20.dp))

        Box(Modifier.weight(1f)) {
            if (query.isBlank()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                ) {
                    OverlineLabel("Try asking")
                    Spacer(Modifier.height(12.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        examplePrompts.forEach { prompt ->
                            CategoryChip(
                                label = prompt,
                                selected = false,
                                accent = PulseAccent,
                                onClick = { viewModel.search(prompt) },
                                modifier = Modifier.heightIn(min = 48.dp),
                            )
                        }
                    }
                    if (recentSearches.isNotEmpty()) {
                        Spacer(Modifier.height(28.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            OverlineLabel("Recent", modifier = Modifier.weight(1f))
                            TextButton(onClick = viewModel::clearRecentSearches) {
                                Text("Clear", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        recentSearches.forEach { recent ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 48.dp)
                                    .clickable { viewModel.onQueryChange(recent) }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.History,
                                    contentDescription = null,
                                    tint = PulseTextMuted,
                                    modifier = Modifier.size(18.dp),
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = recent,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = PulseText,
                                )
                            }
                        }
                    }
                }
            } else {
                when (val current = results) {
                    is UiState.Loading -> LoadingPulse()
                    is UiState.Error -> FriendlyError(current.message)
                    is UiState.Success -> {
                        if (current.data.isEmpty()) {
                            EmptyState(
                                glyph = "✨",
                                title = "Your radar is quiet.",
                                body = "Try another topic and we'll find what's moving.",
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                items(current.data, key = { it.id }) { trend ->
                                    TrendRow(
                                        trend = trend,
                                        onClick = { onTrendClick(trend.id) },
                                    )
                                }
                                if (demo) {
                                    item {
                                        Text(
                                            text = "Demo data",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = PulseTextFaint,
                                            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
