package com.pinwave.ui.trend

import android.content.Intent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.pinwave.core.ui.UiState
import com.pinwave.core.ui.components.AnalyzeSheetContent
import com.pinwave.core.ui.components.CategoryDot
import com.pinwave.core.ui.components.FriendlyError
import com.pinwave.core.ui.components.LoadingPulse
import com.pinwave.core.ui.components.MomentumBadge
import com.pinwave.core.ui.components.MomentumLineChart
import com.pinwave.core.ui.components.OverlineLabel
import com.pinwave.core.ui.components.ShareTrendCard
import com.pinwave.core.ui.components.TrendHeatmap
import com.pinwave.core.ui.theme.PulseAccent
import com.pinwave.core.ui.theme.PulseBackground
import com.pinwave.core.ui.theme.PulseOutline
import com.pinwave.core.ui.theme.PulseSurface
import com.pinwave.core.ui.theme.PulseSurfaceHigh
import com.pinwave.core.ui.theme.PulseText
import com.pinwave.core.ui.theme.PulseTextFaint
import com.pinwave.core.ui.theme.PulseTextMuted
import com.pinwave.core.util.ShareCardSharer
import com.pinwave.domain.model.Pin
import com.pinwave.domain.model.Trend
import kotlinx.coroutines.launch

private val weekDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrendDetailScreen(
    onBack: () -> Unit,
    onSeePins: (String) -> Unit,
    onSteal: (String) -> Unit,
    onCreateContent: (String) -> Unit,
    viewModel: TrendDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val demo by viewModel.demo.collectAsStateWithLifecycle()
    val pins by viewModel.pins.collectAsStateWithLifecycle()
    val isSaved by viewModel.isSaved.collectAsStateWithLifecycle()
    val analyzePhase by viewModel.analyzePhase.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PulseBackground),
    ) {
        when (val s = state) {
            is UiState.Loading -> LoadingPulse()
            is UiState.Error -> FriendlyError(message = s.message, onRetry = viewModel::retry)
            is UiState.Success -> {
                TrendDetailContent(
                    trend = s.data,
                    demo = demo,
                    pins = pins,
                    isSaved = isSaved,
                    onBack = onBack,
                    onToggleSave = viewModel::toggleSave,
                    onSeePins = onSeePins,
                    onSteal = onSteal,
                    onCreateContent = onCreateContent,
                )
                ExtendedFloatingActionButton(
                    onClick = viewModel::analyze,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 20.dp, bottom = 24.dp),
                    containerColor = PulseAccent,
                    contentColor = PulseBackground,
                ) {
                    Text("✨ Analyze", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }

    analyzePhase?.let { phase ->
        ModalBottomSheet(
            onDismissRequest = viewModel::clearAnalysis,
            containerColor = PulseSurface,
        ) {
            AnalyzeSheetContent(phase)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            ) {
                TextButton(
                    onClick = viewModel::clearAnalysis,
                    modifier = Modifier.align(Alignment.CenterEnd),
                ) {
                    Text("Done", color = PulseAccent)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TrendDetailContent(
    trend: Trend,
    demo: Boolean,
    pins: List<Pin>,
    isSaved: Boolean,
    onBack: () -> Unit,
    onToggleSave: () -> Unit,
    onSeePins: (String) -> Unit,
    onSteal: (String) -> Unit,
    onCreateContent: (String) -> Unit,
) {
    var showShare by remember { mutableStateOf(false) }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            TrendHeader(
                trend = trend,
                isSaved = isSaved,
                onBack = onBack,
                onToggleSave = onToggleSave,
                onShare = { showShare = true },
            )
        }
        item {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CategoryDot(trend.category)
                    Spacer(Modifier.width(8.dp))
                    OverlineLabel("${trend.category.label} · PINTEREST TREND")
                }
                if (demo) {
                    Text(
                        "Demo data",
                        style = MaterialTheme.typography.labelSmall,
                        color = PulseTextFaint,
                    )
                }
                Text(
                    text = trend.title,
                    style = MaterialTheme.typography.displayMedium,
                    color = PulseText,
                )
                MomentumBadge(
                    growthPercent = trend.growthPercent,
                    status = trend.status,
                    showLabel = true,
                    large = true,
                )

                Section("TREND MOMENTUM") {
                    MomentumLineChart(points = trend.momentum, status = trend.status)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Demo momentum series — live data arrives with the Pinterest integration.",
                        style = MaterialTheme.typography.labelSmall,
                        color = PulseTextFaint,
                    )
                }

                Section("TREND ACTIVITY") {
                    TrendHeatmap(days = weekDays, intensities = null)
                }

                Section("WHY IT'S RISING") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(PulseSurface)
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        OverlineLabel("AI ANALYSIS (DEMO)", color = PulseAccent)
                        Text(
                            text = trend.aiWhyRising,
                            style = MaterialTheme.typography.bodyLarge,
                            color = PulseTextMuted,
                        )
                    }
                }

                Section("VISUAL THEMES") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        trend.themes.forEach { theme ->
                            Text(
                                text = theme,
                                style = MaterialTheme.typography.labelLarge,
                                color = PulseTextMuted,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(PulseSurfaceHigh)
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                            )
                        }
                    }
                }

                Section("CONTENT OPPORTUNITIES") {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OpportunityRow("🎥", "TikTok") { onCreateContent(trend.id) }
                        OpportunityRow("📸", "Instagram") { onCreateContent(trend.id) }
                        OpportunityRow("📌", "Pinterest") { onCreateContent(trend.id) }
                    }
                }

                if (pins.isNotEmpty()) {
                    Section("PINS IN THIS TREND") {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            pins.take(3).forEach { pin ->
                                AsyncImage(
                                    model = pin.imageUrl,
                                    contentDescription = pin.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(90.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(PulseSurfaceHigh),
                                )
                            }
                        }
                        TextButton(onClick = { onSeePins(trend.id) }) {
                            Text("Explore all Pins", color = PulseAccent)
                        }
                    }
                }

                Button(
                    onClick = { onSteal(trend.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PulseAccent,
                        contentColor = PulseBackground,
                    ),
                ) {
                    Text("Steal the trend", style = MaterialTheme.typography.titleMedium)
                }
                OutlinedButton(
                    onClick = { onCreateContent(trend.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.dp, PulseOutline),
                ) {
                    Text(
                        "Turn this trend into content",
                        style = MaterialTheme.typography.titleMedium,
                        color = PulseText,
                    )
                }

                Spacer(Modifier.height(96.dp))
            }
        }
    }

    if (showShare) {
        ShareTrendDialog(trend = trend, onDismiss = { showShare = false })
    }
}

@Composable
private fun TrendHeader(
    trend: Trend,
    isSaved: Boolean,
    onBack: () -> Unit,
    onToggleSave: () -> Unit,
    onShare: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
    ) {
        AsyncImage(
            model = trend.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color(0x660E0E11),
                        0.5f to Color.Transparent,
                        1f to PulseBackground,
                    ),
                ),
        )
        HeaderIconButton(
            onClick = onBack,
            contentDescription = "Back",
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 16.dp),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = Color.White,
            )
        }
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 16.dp, top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            HeaderIconButton(onClick = onShare, contentDescription = "Share trend") {
                Icon(
                    imageVector = Icons.Outlined.Share,
                    contentDescription = null,
                    tint = Color.White,
                )
            }
            SaveButton(saved = isSaved, onToggle = onToggleSave)
        }
    }
}

@Composable
private fun HeaderIconButton(
    onClick: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.semantics { this.contentDescription = contentDescription },
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.35f)),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}

@Composable
private fun SaveButton(saved: Boolean, onToggle: () -> Unit) {
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }
    IconButton(
        onClick = {
            onToggle()
            scope.launch {
                scale.snapTo(0.6f)
                scale.animateTo(
                    1.15f,
                    spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
                )
                scale.animateTo(
                    1f,
                    spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow),
                )
            }
        },
        modifier = Modifier.semantics {
            contentDescription = if (saved) "Remove from saved" else "Save trend"
        },
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.35f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (saved) "♥" else "♡",
                fontSize = 20.sp,
                color = if (saved) PulseAccent else Color.White,
                modifier = Modifier.graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                },
            )
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        OverlineLabel(title)
        content()
    }
}

@Composable
private fun OpportunityRow(emoji: String, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(PulseSurface)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(emoji, fontSize = 18.sp)
        Spacer(Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = PulseText,
            modifier = Modifier.weight(1f),
        )
        Text("↗", style = MaterialTheme.typography.titleMedium, color = PulseTextMuted)
    }
}

@Composable
private fun ShareTrendDialog(trend: Trend, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(28.dp))
                .background(PulseSurface)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ShareTrendCard(
                trend = trend,
                modifier = Modifier.drawWithContent {
                    graphicsLayer.record { this@drawWithContent.drawContent() }
                    drawLayer(graphicsLayer)
                },
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    scope.launch {
                        try {
                            ShareCardSharer.share(context, graphicsLayer, trend.title)
                        } catch (e: Exception) {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "${trend.title} — spotted on Pinwave")
                            }
                            context.startActivity(Intent.createChooser(intent, "Share trend"))
                        }
                        onDismiss()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PulseAccent,
                    contentColor = PulseBackground,
                ),
            ) {
                Text("Share", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
