package com.pinwave.ui.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinwave.data.repository.SavedRepository
import com.pinwave.domain.model.Trend
import com.pinwave.domain.model.TrendCollection
import com.pinwave.domain.usecase.ToggleSaveTrendUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SavedViewModel @Inject constructor(
    private val savedRepository: SavedRepository,
    private val toggleSaveTrend: ToggleSaveTrendUseCase,
) : ViewModel() {

    val savedTrends: StateFlow<List<Trend>> = savedRepository.savedTrends
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val collections: StateFlow<List<Pair<TrendCollection, Int>>> = savedRepository.collections
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Everything on this screen is saved, so toggling always removes. */
    fun toggleSave(trendId: String) {
        viewModelScope.launch { toggleSaveTrend(trendId, currentlySaved = true) }
    }

    fun createCollection(name: String, emoji: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch { savedRepository.createCollection(trimmed, emoji) }
    }

    fun deleteCollection(id: String) {
        viewModelScope.launch { savedRepository.deleteCollection(id) }
    }
}
