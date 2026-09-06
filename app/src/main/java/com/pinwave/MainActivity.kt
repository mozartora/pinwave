package com.pinwave

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.rememberNavController
import com.pinwave.core.ui.theme.PinwaveTheme
import com.pinwave.data.prefs.UserPreferences
import com.pinwave.ui.navigation.PinwaveNavHost
import com.pinwave.ui.navigation.Routes
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class RootViewModel @Inject constructor(prefs: UserPreferences) : ViewModel() {
    val startDestination: StateFlow<String?> = prefs.onboardingDone
        .map { done -> if (done) Routes.HOME else Routes.ONBOARDING }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PinwaveTheme {
                PinwaveRoot()
            }
        }
    }
}

@Composable
private fun PinwaveRoot(viewModel: RootViewModel = hiltViewModel()) {
    val startDestination by viewModel.startDestination.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    Box(Modifier.fillMaxSize()) {
        startDestination?.let { start ->
            PinwaveNavHost(navController = navController, startDestination = start)
        }
    }
}
