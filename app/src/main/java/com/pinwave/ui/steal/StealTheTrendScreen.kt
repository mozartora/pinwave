package com.pinwave.ui.steal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinwave.core.ui.UiState
import com.pinwave.core.ui.components.AppearAnimation
import com.pinwave.core.ui.components.FriendlyError
import com.pinwave.core.ui.components.LoadingPulse
import com.pinwave.core.ui.components.MomentumBadge
import com.pinwave.core.ui.components.OverlineLabel
import com.pinwave.core.ui.components.pulseAlpha
import com.pinwave.core.ui.theme.PulseBackground
import com.pinwave.core.ui.theme.PulseDimens
import com.pinwave.core.ui.theme.PulseSurface
import com.pinwave.core.ui.theme.PulseTextFaint
import com.pinwave.core.ui.theme.PulseTextMuted
import com.pinwave.data.pinterest.RepoResult
import com.pinwave.domain.model.Trend
import com.pinwave.domain.usecase.GetTrendUseCase
import com.pinwave.domain.usecase.StealTheTrendUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class StealTheTrendViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTrend: GetTrendUseCase,
    private val stealTheTrend: StealTheTrendUseCase,
) : ViewModel() {

    val trendId: String = checkNotNull(savedStateHandle["trendId"])

    private val _trend = MutableStateFlow<UiState<Trend>>(UiState.Loading)
    val trend: StateFlow<UiState<Trend>> = _trend.asStateFlow()

    private val _ideas = MutableStateFlow<List<String>?>(null)
    val ideas: StateFlow<List<String>?> = _ideas.asStateFlow()

    private val _generating = MutableStateFlow(false)
    val generating: StateFlow<Boolean> = _generating.asStateFlow()

    init {
        viewModelScope.launch {
            _trend.value = when (val result = getTrend(trendId)) {
                is RepoResult.Ok -> UiState.Success(result.data)
                is RepoResult.Blocked -> UiState.Error(result.userMessage)
            }
        }
    }

    fun generate() {
        val current = (_trend.value as? UiState.Success)?.data ?: return
        if (_generating.value) return
        viewModelScope.launch {
            _generating.value = true
            try {
                _ideas.value = stealTheTrend(current)
            } finally {
                _generating.value = false
            }
        }
    }
}

@Composable
fun StealTheTrendScreen(
    onBack: () -> Unit,
    onCreateContent: (String) -> Unit,
    viewModel: StealTheTrendViewModel = hiltViewModel(),
) {
    val trendState by viewModel.trend.collectAsState()
    val ideas by viewModel.ideas.collectAsState()
    val generating by viewModel.generating.collectAsState()

    LazyColumn(
        modifier = Modifier.background(PulseBackground),
        contentPadding = PaddingValues(PulseDimens.ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
                OverlineLabel("STEAL THE TREND")
            }
        }
        item {
            Text(
                "Make it yours.",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        when (val state = trendState) {
            UiState.Loading -> item {
                Box(Modifier.fillMaxWidth().height(320.dp)) { LoadingPulse() }
            }

            is UiState.Error -> item {
                Box(Modifier.fillMaxWidth().height(320.dp)) { FriendlyError(state.message) }
            }

            is UiState.Success -> {
                val trend = state.data
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(PulseSurface)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        OverlineLabel("TREND")
                        Text(
                            trend.title,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        MomentumBadge(growthPercent = trend.growthPercent, status = trend.status)
                    }
                }
                item {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            "↓",
                            style = MaterialTheme.typography.displayMedium,
                            color = PulseTextFaint,
                        )
                    }
                }
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(PulseSurface)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        OverlineLabel("YOUR VERSION")
                        if (ideas == null) {
                            Text(
                                "e.g. '5 brown bags under \$100 that look designer'",
                                style = MaterialTheme.typography.bodyLarge,
                                color = PulseTextMuted,
                            )
                        } else {
                            Text(
                                "${ideas!!.size} angles on \"${trend.title}\"",
                                style = MaterialTheme.typography.bodyLarge,
                                color = PulseTextMuted,
                            )
                        }
                    }
                }
                item {
                    Button(
                        onClick = { viewModel.generate() },
                        enabled = !generating,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = PulseBackground,
                        ),
                    ) {
                        if (generating) {
                            Text(
                                "✨",
                                modifier = Modifier.alpha(pulseAlpha()),
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Remixing the trend…", style = MaterialTheme.typography.titleMedium)
                        } else {
                            Text(
                                if (ideas == null) "Generate Ideas" else "Generate again",
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    }
                }
                ideas?.let { list ->
                    itemsIndexed(list, key = { i, _ -> i }) { index, idea ->
                        AppearAnimation(index = index) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(PulseDimens.CardRadiusSmall))
                                    .background(PulseSurface)
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                Text(
                                    "${index + 1}.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                                Text(
                                    idea,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        }
                    }
                }
                item {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        TextButton(onClick = { onCreateContent(viewModel.trendId) }) {
                            Text(
                                "Turn this trend into content →",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }
        }
    }
}
