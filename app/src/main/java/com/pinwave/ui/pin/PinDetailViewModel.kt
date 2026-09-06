package com.pinwave.ui.pin

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinwave.core.ui.UiState
import com.pinwave.core.ui.components.analyzeSteps
import com.pinwave.data.ai.PinInsight
import com.pinwave.data.pinterest.RepoResult
import com.pinwave.domain.model.Pin
import com.pinwave.domain.usecase.AnalyzePinUseCase
import com.pinwave.domain.usecase.GetPinUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class PinDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPin: GetPinUseCase,
    private val analyzePin: AnalyzePinUseCase,
) : ViewModel() {

    private val pinId: String = checkNotNull(savedStateHandle["pinId"])

    private val _state = MutableStateFlow<UiState<Pin>>(UiState.Loading)
    val state: StateFlow<UiState<Pin>> = _state

    private val _insight = MutableStateFlow<PinInsight?>(null)
    val insight: StateFlow<PinInsight?> = _insight

    private val _analyzing = MutableStateFlow(false)
    val analyzing: StateFlow<Boolean> = _analyzing

    private val _analyzeStep = MutableStateFlow(0)
    val analyzeStep: StateFlow<Int> = _analyzeStep

    init {
        viewModelScope.launch {
            _state.value = when (val result = getPin(pinId)) {
                is RepoResult.Ok -> UiState.Success(result.data)
                is RepoResult.Blocked -> UiState.Error(result.userMessage)
            }
        }
    }

    fun analyze() {
        val pin = (_state.value as? UiState.Success)?.data ?: return
        if (_analyzing.value || _insight.value != null) return
        viewModelScope.launch {
            _analyzing.value = true
            val stepCycle = launch {
                var step = 0
                while (true) {
                    _analyzeStep.value = step % analyzeSteps.size
                    step++
                    delay(800)
                }
            }
            _insight.value = analyzePin(pin)
            stepCycle.cancel()
            _analyzing.value = false
        }
    }
}
