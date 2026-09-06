package com.pinwave.ui.radar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinwave.core.ui.UiState
import com.pinwave.core.ui.components.RadarCategoryEntry
import com.pinwave.data.pinterest.RepoResult
import com.pinwave.domain.model.Trend
import com.pinwave.domain.model.TrendCategory
import com.pinwave.domain.usecase.GetTrendsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RadarData(
    val trends: List<Trend>,
    val entries: List<RadarCategoryEntry>,
    val totalDetected: Int,
    val demo: Boolean,
)

@HiltViewModel
class RadarViewModel @Inject constructor(
    private val getTrends: GetTrendsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<RadarData>>(UiState.Loading)
    val state: StateFlow<UiState<RadarData>> = _state

    private val _selectedCategory = MutableStateFlow<TrendCategory?>(null)
    val selectedCategory: StateFlow<TrendCategory?> = _selectedCategory

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            _state.value = when (val result = getTrends(null)) {
                is RepoResult.Ok -> {
                    val trends = result.data
                    val entries = trends
                        .groupBy { it.category }
                        .map { (category, categoryTrends) ->
                            RadarCategoryEntry(
                                category = category,
                                trendCount = categoryTrends.size,
                                intensity = (categoryTrends.maxOf { it.growthPercent } / 100f)
                                    .coerceIn(0f, 1f),
                            )
                        }
                        .sortedByDescending { it.intensity }
                    UiState.Success(
                        RadarData(
                            trends = trends,
                            entries = entries,
                            totalDetected = trends.size,
                            demo = result.demo,
                        ),
                    )
                }
                is RepoResult.Blocked -> UiState.Error(result.userMessage)
            }
        }
    }

    fun selectCategory(category: TrendCategory?) {
        _selectedCategory.value = category
    }
}
