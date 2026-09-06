package com.pinwave.ui.pin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pinwave.core.ui.UiState
import com.pinwave.core.ui.components.FriendlyError
import com.pinwave.core.ui.components.LoadingPulse
import com.pinwave.core.ui.components.OverlineLabel
import com.pinwave.core.ui.components.PinMasonryGrid
import com.pinwave.core.ui.theme.PulseBackground
import com.pinwave.core.ui.theme.PulseTextMuted

@Composable
fun PinExplorerScreen(
    onPinClick: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: PinExplorerViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val trendTitle by viewModel.trendTitle.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PulseBackground),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    trendTitle,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                OverlineLabel("PINS", color = PulseTextMuted)
            }
        }
        when (val current = state) {
            UiState.Loading -> LoadingPulse()
            is UiState.Error -> FriendlyError(current.message)
            is UiState.Success -> PinMasonryGrid(
                pins = current.data,
                onPinClick = { onPinClick(it.id) },
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 12.dp,
                ),
            )
        }
    }
}
