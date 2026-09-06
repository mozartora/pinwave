package com.pinwave.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinwave.core.ui.UiState
import com.pinwave.data.pinterest.RepoResult
import com.pinwave.domain.model.Trend
import com.pinwave.domain.usecase.GetTrendingNowUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTrendingNow: GetTrendingNowUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<List<Trend>>>(UiState.Loading)
    val state: StateFlow<UiState<List<Trend>>> = _state.asStateFlow()

    private val _demoData = MutableStateFlow(false)
    val demoData: StateFlow<Boolean> = _demoData.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            when (val result = getTrendingNow()) {
                is RepoResult.Ok -> {
                    _demoData.value = result.demo
                    _state.value = UiState.Success(result.data)
                }
                is RepoResult.Blocked -> _state.value = UiState.Error(result.userMessage)
            }
        }
    }
}
