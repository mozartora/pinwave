package com.pinwave.ui.drop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinwave.core.ui.UiState
import com.pinwave.core.ui.components.AppearAnimation
import com.pinwave.core.ui.components.FriendlyError
import com.pinwave.core.ui.components.LoadingPulse
import com.pinwave.core.ui.components.OverlineLabel
import com.pinwave.core.ui.components.TrendRow
import com.pinwave.core.ui.theme.PulseBackground
import com.pinwave.core.ui.theme.PulseDimens
import com.pinwave.core.ui.theme.PulseTextFaint
import com.pinwave.core.ui.theme.PulseTextMuted
import com.pinwave.data.mock.MockData
import com.pinwave.data.pinterest.RepoResult
import com.pinwave.domain.model.Trend
import com.pinwave.domain.usecase.GetTrendingNowUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class TodaysDropViewModel @Inject constructor(
    getTrendingNow: GetTrendingNowUseCase,
) : ViewModel() {

    val state: StateFlow<UiState<List<Trend>>> = flow {
        when (val result = getTrendingNow()) {
            is RepoResult.Ok -> {
                val byId = result.data.associateBy { it.id }
                val drop = MockData.dailyDropIds.mapNotNull { byId[it] }
                emit(
                    UiState.Success(
                        drop.ifEmpty {
                            result.data.sortedByDescending { it.growthPercent }.take(5)
                        },
                    ),
                )
            }

            is RepoResult.Blocked -> emit(UiState.Error(result.userMessage))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)
}

@Composable
fun TodaysDropScreen(
    onTrendClick: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: TodaysDropViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val dateLabel = LocalDate.now().format(
        DateTimeFormatter.ofPattern("MMMM d", Locale.getDefault()),
    )

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
                OverlineLabel("TODAY'S TREND DROP")
            }
        }
        item {
            Text(
                "$dateLabel — five trends to watch.",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        when (val current = state) {
            UiState.Loading -> item {
                Box(Modifier.fillMaxWidth().height(320.dp)) { LoadingPulse() }
            }

            is UiState.Error -> item {
                Box(Modifier.fillMaxWidth().height(320.dp)) { FriendlyError(current.message) }
            }

            is UiState.Success -> {
                itemsIndexed(current.data, key = { _, trend -> trend.id }) { index, trend ->
                    AppearAnimation(index = index) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            Text(
                                text = "%02d".format(index + 1),
                                style = MaterialTheme.typography.displayMedium,
                                color = PulseTextFaint,
                            )
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                TrendRow(
                                    trend = trend,
                                    onClick = { onTrendClick(trend.id) },
                                )
                                Text(
                                    trend.aiWhyRising,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = PulseTextMuted,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(horizontal = 4.dp),
                                )
                            }
                        }
                    }
                }
                item {
                    Text(
                        "Demo data — the daily drop is computed from live Pinterest data once connected.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PulseTextFaint,
                    )
                }
            }
        }
    }
}
