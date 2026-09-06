package com.pinwave.ui.explore

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinwave.core.ui.UiState
import com.pinwave.data.pinterest.RepoResult
import com.pinwave.domain.model.Trend
import com.pinwave.domain.model.TrendCategory
import com.pinwave.domain.usecase.GetTrendsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ExploreViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTrends: GetTrendsUseCase,
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow(
        savedStateHandle.get<String>("category")?.let { name ->
            runCatching { TrendCategory.valueOf(name) }.getOrNull()
        },
    )
    val selectedCategory: StateFlow<TrendCategory?> = _selectedCategory

    private val _state = MutableStateFlow<UiState<List<Trend>>>(UiState.Loading)
    val state: StateFlow<UiState<List<Trend>>> = _state

    private val _demo = MutableStateFlow(false)
    val demo: StateFlow<Boolean> = _demo

    init {
        load()
    }

    fun setCategory(category: TrendCategory?) {
        if (_selectedCategory.value == category) return
        _selectedCategory.value = category
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            when (val result = getTrends(_selectedCategory.value)) {
                is RepoResult.Ok -> {
                    _demo.value = result.demo
                    _state.value = UiState.Success(result.data)
                }
                is RepoResult.Blocked -> _state.value = UiState.Error(result.userMessage)
            }
        }
    }
}
