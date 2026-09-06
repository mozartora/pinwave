package com.pinwave.core.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.pinwave.core.ui.theme.PulseSurface
import com.pinwave.core.ui.theme.PulseTextMuted
import com.pinwave.domain.model.AiAnalysis

sealed interface AnalyzePhase {
    data class Thinking(val step: Int) : AnalyzePhase
    data class Done(val analysis: AiAnalysis) : AnalyzePhase
}

val analyzeSteps = listOf(
    "Analyzing visual patterns…",
    "Finding recurring themes…",
    "Generating opportunities…",
)

/**
 * Content of the ✨ Analyze sheet (spec §11): a staged "thinking" animation
 * crossfading into the AI result. AI output is always labeled as AI — never
 * blended with Pinterest-provided data (§10).
 */
@Composable
fun AnalyzeSheetContent(phase: AnalyzePhase, modifier: Modifier = Modifier) {
    Crossfade(targetState = phase, label = "analyze", modifier = modifier) { state ->
        when (state) {
            is AnalyzePhase.Thinking -> Column(
                modifier = Modifier.fillMaxWidth().padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    "✨",
                    style = MaterialTheme.typography.displayMedium,
                    modifier = Modifier.alpha(pulseAlpha()),
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    analyzeSteps[state.step.coerceIn(0, analyzeSteps.lastIndex)],
                    style = MaterialTheme.typography.titleMedium,
                    color = PulseTextMuted,
                )
            }

            is AnalyzePhase.Done -> Column(
                modifier = Modifier.fillMaxWidth().padding(28.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                OverlineLabel("AI INSIGHT (DEMO)", color = MaterialTheme.colorScheme.primary)
                InsightSection("Visual style") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.analysis.visualStyle.forEach { tag ->
                            Text(
                                tag,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(PulseSurface)
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                            )
                        }
                    }
                }
                InsightSection("Likely audience") {
                    Text(
                        state.analysis.audience,
                        style = MaterialTheme.typography.bodyLarge,
                        color = PulseTextMuted,
                    )
                }
                InsightSection("Content opportunities") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.analysis.opportunities.forEachIndexed { i, idea ->
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    "${i + 1}.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary,
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
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun InsightSection(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OverlineLabel(title)
        content()
    }
}
