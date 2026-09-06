package com.pinwave.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinwave.core.ui.UiState
import com.pinwave.data.pinterest.RepoResult
import com.pinwave.data.repository.SavedRepository
import com.pinwave.domain.model.Trend
import com.pinwave.domain.usecase.SearchTrendsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchTrends: SearchTrendsUseCase,
    private val saved: SavedRepository,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _demo = MutableStateFlow(false)
    val demo: StateFlow<Boolean> = _demo

    val recentSearches: StateFlow<List<String>> = saved.recentSearches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val results: StateFlow<UiState<List<Trend>>> = _query
        .debounce(350)
        .distinctUntilChanged()
        .transformLatest { q ->
            if (q.isBlank()) {
                _demo.value = false
                emit(UiState.Success(emptyList()))
            } else {
                emit(UiState.Loading)
                when (val result = searchTrends(q)) {
                    is RepoResult.Ok -> {
                        _demo.value = result.demo
                        emit(UiState.Success(result.data))
                    }
                    is RepoResult.Blocked -> emit(UiState.Error(result.userMessage))
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Success(emptyList()))

    fun onQueryChange(value: String) {
        _query.value = value
    }

    fun submitSearch() {
        viewModelScope.launch { saved.recordSearch(_query.value) }
    }

    fun search(prompt: String) {
        _query.value = prompt
        submitSearch()
    }

    fun clearRecentSearches() {
        viewModelScope.launch { saved.clearRecentSearches() }
    }
}
