package com.pinwave.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinwave.data.pinterest.auth.PinterestAuthManager
import com.pinwave.data.prefs.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val prefs: UserPreferences,
    private val auth: PinterestAuthManager,
) : ViewModel() {

    private val _done = MutableStateFlow(false)
    val done: StateFlow<Boolean> = _done.asStateFlow()

    fun finishOnboarding() {
        viewModelScope.launch {
            prefs.setOnboardingDone()
            _done.value = true
        }
    }

    fun connectDemo() {
        viewModelScope.launch {
            auth.markConnectedDemo()
            prefs.setOnboardingDone()
            _done.value = true
        }
    }
}
