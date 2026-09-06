package com.pinwave.ui.content

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinwave.core.ui.UiState
import com.pinwave.core.ui.components.CategoryChip
import com.pinwave.core.ui.components.OverlineLabel
import com.pinwave.core.ui.components.pulseAlpha
import com.pinwave.core.ui.theme.PulseBackground
import com.pinwave.core.ui.theme.PulseDimens
import com.pinwave.core.ui.theme.PulseOutline
import com.pinwave.core.ui.theme.PulseSurface
import com.pinwave.core.ui.theme.PulseSurfaceHigh
import com.pinwave.core.ui.theme.PulseTextFaint
import com.pinwave.core.ui.theme.PulseTextMuted
import com.pinwave.data.pinterest.RepoResult
import com.pinwave.domain.model.ContentFormat
import com.pinwave.domain.model.ContentStyle
import com.pinwave.domain.model.GeneratedContent
import com.pinwave.domain.model.Trend
import com.pinwave.domain.usecase.GenerateContentUseCase
import com.pinwave.domain.usecase.GetTrendUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class TrendToContentViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTrend: GetTrendUseCase,
    private val generateContent: GenerateContentUseCase,
) : ViewModel() {

    private val trendId: String = checkNotNull(savedStateHandle["trendId"])
    private var trend: Trend? = null

    private val _selectedFormat = MutableStateFlow(ContentFormat.TIKTOK)
    val selectedFormat: StateFlow<ContentFormat> = _selectedFormat.asStateFlow()

    private val _selectedStyle = MutableStateFlow(ContentStyle.VIRAL)
    val selectedStyle: StateFlow<ContentStyle> = _selectedStyle.asStateFlow()

    private val _result = MutableStateFlow<UiState<GeneratedContent>?>(null)
    val result: StateFlow<UiState<GeneratedContent>?> = _result.asStateFlow()

    init {
        viewModelScope.launch {
            trend = (getTrend(trendId) as? RepoResult.Ok)?.data
        }
    }

    fun selectFormat(format: ContentFormat) {
        _selectedFormat.value = format
    }

    fun selectStyle(style: ContentStyle) {
        _selectedStyle.value = style
    }

    fun generate() {
        if (_result.value is UiState.Loading) return
        viewModelScope.launch {
            val current = trend ?: run {
                when (val fetched = getTrend(trendId)) {
                    is RepoResult.Ok -> fetched.data.also { trend = it }
                    is RepoResult.Blocked -> {
                        _result.value = UiState.Error(fetched.userMessage)
                        return@launch
                    }
                }
            }
            _result.value = UiState.Loading
            _result.value = UiState.Success(
                generateContent(current, _selectedFormat.value, _selectedStyle.value),
            )
        }
    }
}

@Composable
fun TrendToContentScreen(
    onBack: () -> Unit,
    viewModel: TrendToContentViewModel = hiltViewModel(),
) {
    val selectedFormat by viewModel.selectedFormat.collectAsState()
    val selectedStyle by viewModel.selectedStyle.collectAsState()
    val result by viewModel.result.collectAsState()

    LazyColumn(
        modifier = Modifier.background(PulseBackground),
        contentPadding = PaddingValues(PulseDimens.ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
        item {
            Text(
                "Turn it into content.",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        item { OverlineLabel("WHAT DO YOU CREATE?") }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ContentFormat.entries.forEach { format ->
                    FormatRow(
                        format = format,
                        selected = format == selectedFormat,
                        onClick = { viewModel.selectFormat(format) },
                    )
                }
            }
        }

        item { OverlineLabel("CONTENT STYLE") }
        item {
            @OptIn(ExperimentalLayoutApi::class)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ContentStyle.entries.forEach { style ->
                    CategoryChip(
                        label = style.label,
                        selected = style == selectedStyle,
                        accent = MaterialTheme.colorScheme.primary,
                        onClick = { viewModel.selectStyle(style) },
                    )
                }
            }
        }

        item {
            val loading = result is UiState.Loading
            Button(
                onClick = { viewModel.generate() },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = PulseBackground,
                ),
            ) {
                if (loading) {
                    Text(
                        "✨",
                        modifier = Modifier.alpha(pulseAlpha()),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Writing your ${selectedFormat.label}…",
                        style = MaterialTheme.typography.titleMedium,
                    )
                } else {
                    Text("Generate", style = MaterialTheme.typography.titleMedium)
                }
            }
        }

        when (val state = result) {
            is UiState.Error -> item {
                Text(
                    state.message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = PulseTextMuted,
                )
            }

            is UiState.Success -> {
                item { ResultCard(state.data) }
                item {
                    Text(
                        "Generated by Pinwave AI (demo). Keywords are suggestions, not Pinterest API data.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PulseTextFaint,
                    )
                }
            }

            else -> Unit
        }
    }
}

@Composable
private fun FormatRow(
    format: ContentFormat,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(PulseDimens.CardRadiusSmall))
            .background(PulseSurface)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .border(
                    width = 1.5.dp,
                    color = if (selected) MaterialTheme.colorScheme.primary else PulseOutline,
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) {
                Box(
                    Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                )
            }
        }
        Text(
            format.label,
            style = MaterialTheme.typography.titleMedium,
            color = if (selected) MaterialTheme.colorScheme.onSurface else PulseTextMuted,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ResultCard(content: GeneratedContent) {
    val clipboard = LocalClipboardManager.current

    @Composable
    fun Section(label: String, text: String, copyDescription: String) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OverlineLabel(label, modifier = Modifier.weight(1f))
                IconButton(onClick = { clipboard.setText(AnnotatedString(text)) }) {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = copyDescription,
                        tint = PulseTextMuted,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
            SelectionContainer {
                Text(
                    text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(PulseSurface)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Section("HOOK", content.hook, "Copy hook")
        Section("CONCEPT", content.concept, "Copy concept")
        Section("SCRIPT", content.script, "Copy script")
        Section("CAPTION", content.caption, "Copy caption")
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OverlineLabel("KEYWORDS", modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {
                        clipboard.setText(AnnotatedString(content.keywords.joinToString(", ")))
                    },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = "Copy keywords",
                        tint = PulseTextMuted,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                content.keywords.forEach { keyword ->
                    Text(
                        keyword,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(PulseSurfaceHigh)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    )
                }
            }
        }
    }
}
