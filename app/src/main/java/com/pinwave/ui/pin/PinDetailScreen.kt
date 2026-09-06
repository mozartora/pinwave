package com.pinwave.ui.pin

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.pinwave.core.ui.UiState
import com.pinwave.core.ui.components.FriendlyError
import com.pinwave.core.ui.components.LoadingPulse
import com.pinwave.core.ui.components.OverlineLabel
import com.pinwave.core.ui.components.analyzeSteps
import com.pinwave.core.ui.components.pulseAlpha
import com.pinwave.core.ui.theme.PulseAccent
import com.pinwave.core.ui.theme.PulseBackground
import com.pinwave.core.ui.theme.PulseSurface
import com.pinwave.core.ui.theme.PulseTextFaint
import com.pinwave.core.ui.theme.PulseTextMuted
import com.pinwave.data.ai.PinInsight
import com.pinwave.domain.model.Pin

@Composable
fun PinDetailScreen(
    onBack: () -> Unit,
    viewModel: PinDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val insight by viewModel.insight.collectAsStateWithLifecycle()
    val analyzing by viewModel.analyzing.collectAsStateWithLifecycle()
    val analyzeStep by viewModel.analyzeStep.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PulseBackground),
    ) {
        when (val current = state) {
            UiState.Loading -> LoadingPulse()
            is UiState.Error -> FriendlyError(current.message)
            is UiState.Success -> PinDetailContent(
                pin = current.data,
                insight = insight,
                analyzing = analyzing,
                analyzeStep = analyzeStep,
                onAnalyze = viewModel::analyze,
                onBack = onBack,
            )
        }
    }
}

@Composable
private fun PinDetailContent(
    pin: Pin,
    insight: PinInsight?,
    analyzing: Boolean,
    analyzeStep: Int,
    onAnalyze: () -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val accent = MaterialTheme.colorScheme.primary

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = pin.imageUrl,
                    contentDescription = pin.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(pin.aspectRatio),
                )
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(8.dp)
                        .align(Alignment.TopStart)
                        .clip(RoundedCornerShape(50))
                        .background(PulseBackground.copy(alpha = 0.55f)),
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                // Pinterest-provided data — attributed, never blended with AI (§10, §25).
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OverlineLabel("PINTEREST DATA")
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(PulseSurface)
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(pin.title, style = MaterialTheme.typography.titleLarge)
                        Text(
                            pin.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = PulseTextMuted,
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                "Source: ${pin.sourceName}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PulseTextFaint,
                            )
                            TextButton(
                                onClick = {
                                    context.startActivity(
                                        Intent(Intent.ACTION_VIEW, Uri.parse(pin.sourceUrl)),
                                    )
                                },
                                modifier = Modifier.heightIn(min = 48.dp),
                            ) {
                                Text("Open on Pinterest ↗", color = accent)
                            }
                        }
                    }
                    Text(
                        "Demo content — live Pins load via the official Pinterest API.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PulseTextFaint,
                    )
                }

                // AI insight — visually distinct provenance from the Pinterest data above.
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OverlineLabel("AI INSIGHT", color = accent)
                    when {
                        insight != null -> Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            Text(
                                insight.summary,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                insight.visualCharacteristics.forEach { trait ->
                                    Text(
                                        trait,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = accent,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50))
                                            .background(PulseSurface)
                                            .padding(horizontal = 12.dp, vertical = 6.dp),
                                    )
                                }
                            }
                            OverlineLabel("CONTENT IDEAS", color = accent)
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                insight.contentIdeas.forEachIndexed { index, idea ->
                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Text(
                                            "${index + 1}.",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = accent,
                                        )
                                        Text(
                                            idea,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurface,
                                        )
                                    }
                                }
                            }
                        }

                        analyzing -> Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Text(
                                "✨",
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.alpha(pulseAlpha()),
                            )
                            Text(
                                analyzeSteps[analyzeStep.coerceIn(0, analyzeSteps.lastIndex)],
                                style = MaterialTheme.typography.titleMedium,
                                color = PulseTextMuted,
                            )
                        }

                        else -> Button(
                            onClick = onAnalyze,
                            modifier = Modifier.heightIn(min = 48.dp),
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PulseAccent,
                                contentColor = PulseBackground,
                            ),
                        ) {
                            Text("✨ Analyze this Pin")
                        }
                    }
                }
            }
        }
    }
}
