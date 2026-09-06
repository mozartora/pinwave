package com.pinwave.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinwave.data.prefs.UserPreferences
import com.pinwave.data.pinterest.auth.PinterestAuthManager
import com.pinwave.data.repository.SavedRepository
import com.pinwave.domain.model.Plan
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefs: UserPreferences,
    private val auth: PinterestAuthManager,
    private val saved: SavedRepository,
) : ViewModel() {

    val pinterestConnected: StateFlow<Boolean> = prefs.pinterestConnected
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val notificationsEnabled: StateFlow<Boolean> = prefs.notificationsEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val appearance: StateFlow<String> = prefs.appearance
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "dark")

    val plan: StateFlow<Plan> = prefs.plan
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Plan.FREE)

    fun connectDemo() {
        viewModelScope.launch { auth.markConnectedDemo() }
    }

    fun disconnect() {
        viewModelScope.launch { auth.disconnect() }
    }

    fun setNotifications(enabled: Boolean) {
        viewModelScope.launch { prefs.setNotificationsEnabled(enabled) }
    }

    fun setAppearance(value: String) {
        viewModelScope.launch { prefs.setAppearance(value) }
    }

    /** Local-only demo reset: clear app data and disconnect Pinterest. */
    fun deleteAccount() {
        viewModelScope.launch {
            saved.clearRecentSearches()
            auth.disconnect()
        }
    }
}
