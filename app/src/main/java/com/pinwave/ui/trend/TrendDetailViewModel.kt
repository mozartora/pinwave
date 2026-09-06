package com.pinwave.ui.trend

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinwave.core.ui.UiState
import com.pinwave.core.ui.components.AnalyzePhase
import com.pinwave.data.pinterest.RepoResult
import com.pinwave.data.repository.SavedRepository
import com.pinwave.domain.model.Pin
import com.pinwave.domain.model.Trend
import com.pinwave.domain.usecase.AnalyzeTrendUseCase
import com.pinwave.domain.usecase.GetPinsForTrendUseCase
import com.pinwave.domain.usecase.GetTrendUseCase
import com.pinwave.domain.usecase.ToggleSaveTrendUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class TrendDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTrend: GetTrendUseCase,
    private val getPinsForTrend: GetPinsForTrendUseCase,
    private val analyzeTrend: AnalyzeTrendUseCase,
    private val toggleSaveTrend: ToggleSaveTrendUseCase,
    savedRepository: SavedRepository,
) : ViewModel() {

    private val trendId: String = checkNotNull(savedStateHandle["trendId"])

    private val _state = MutableStateFlow<UiState<Trend>>(UiState.Loading)
    val state: StateFlow<UiState<Trend>> = _state.asStateFlow()

    private val _demo = MutableStateFlow(false)
    val demo: StateFlow<Boolean> = _demo.asStateFlow()

    private val _pins = MutableStateFlow<List<Pin>>(emptyList())
    val pins: StateFlow<List<Pin>> = _pins.asStateFlow()

    val isSaved: StateFlow<Boolean> = savedRepository.isSaved(trendId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _analyzePhase = MutableStateFlow<AnalyzePhase?>(null)
    val analyzePhase: StateFlow<AnalyzePhase?> = _analyzePhase.asStateFlow()

    private var currentTrend: Trend? = null
    private var analyzeJob: Job? = null

    init {
        load()
    }

    fun retry() = load()

    private fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            when (val result = getTrend(trendId)) {
                is RepoResult.Ok -> {
                    currentTrend = result.data
                    _demo.value = result.demo
                    _state.value = UiState.Success(result.data)
                    loadPins()
                }
                is RepoResult.Blocked -> _state.value = UiState.Error(result.userMessage)
            }
        }
    }

    private fun loadPins() {
        viewModelScope.launch {
            when (val result = getPinsForTrend(trendId)) {
                is RepoResult.Ok -> _pins.value = result.data
                is RepoResult.Blocked -> Unit // the pins preview is optional
            }
        }
    }

    fun toggleSave() {
        viewModelScope.launch {
            toggleSaveTrend(trendId, isSaved.value)
        }
    }

    fun analyze() {
        val trend = currentTrend ?: return
        if (analyzeJob?.isActive == true) return
        analyzeJob = viewModelScope.launch {
            _analyzePhase.value = AnalyzePhase.Thinking(0)
            val cycler = launch {
                var step = 0
                while (true) {
                    delay(800)
                    step = (step + 1) % 3
                    _analyzePhase.value = AnalyzePhase.Thinking(step)
                }
            }
            try {
                val analysis = analyzeTrend(trend)
                _analyzePhase.value = AnalyzePhase.Done(analysis)
            } finally {
                cycler.cancel()
            }
        }
    }

    fun clearAnalysis() {
        analyzeJob?.cancel()
        _analyzePhase.value = null
    }
}
