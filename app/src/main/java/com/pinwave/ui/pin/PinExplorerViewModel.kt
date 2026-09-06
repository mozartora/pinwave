package com.pinwave.ui.pin

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinwave.core.ui.UiState
import com.pinwave.data.pinterest.RepoResult
import com.pinwave.domain.model.Pin
import com.pinwave.domain.usecase.GetPinsForTrendUseCase
import com.pinwave.domain.usecase.GetTrendUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class PinExplorerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTrend: GetTrendUseCase,
    private val getPinsForTrend: GetPinsForTrendUseCase,
) : ViewModel() {

    private val trendId: String = checkNotNull(savedStateHandle["trendId"])

    private val _state = MutableStateFlow<UiState<List<Pin>>>(UiState.Loading)
    val state: StateFlow<UiState<List<Pin>>> = _state

    private val _trendTitle = MutableStateFlow("")
    val trendTitle: StateFlow<String> = _trendTitle

    init {
        viewModelScope.launch {
            when (val result = getTrend(trendId)) {
                is RepoResult.Ok -> _trendTitle.value = result.data.title
                is RepoResult.Blocked -> { /* the pins result below carries the error */ }
            }
        }
        viewModelScope.launch {
            _state.value = when (val result = getPinsForTrend(trendId)) {
                is RepoResult.Ok -> UiState.Success(result.data)
                is RepoResult.Blocked -> UiState.Error(result.userMessage)
            }
        }
    }
}
