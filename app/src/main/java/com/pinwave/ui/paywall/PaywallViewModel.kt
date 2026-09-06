package com.pinwave.ui.paywall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinwave.data.billing.BillingRepository
import com.pinwave.domain.model.Plan
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class PaywallViewModel @Inject constructor(
    private val billing: BillingRepository,
) : ViewModel() {

    val currentPlan: StateFlow<Plan> = billing.currentPlan
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Plan.FREE)

    private val _subscribing = MutableStateFlow(false)
    val subscribing: StateFlow<Boolean> = _subscribing.asStateFlow()

    fun subscribe(plan: Plan) {
        if (_subscribing.value) return
        viewModelScope.launch {
            _subscribing.value = true
            try {
                billing.subscribe(plan)
            } finally {
                _subscribing.value = false
            }
        }
    }

    fun restore() {
        viewModelScope.launch { billing.restore() }
    }
}
