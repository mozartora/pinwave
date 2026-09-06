package com.pinwave.ui.legal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pinwave.core.ui.theme.PulseDimens
import com.pinwave.core.ui.theme.PulseTextMuted

private data class LegalPage(val title: String, val paragraphs: List<String>)

private val pages = mapOf(
    "privacy" to LegalPage(
        title = "Privacy Policy",
        paragraphs = listOf(
            "Pinwave is built local-first. What you do in the app stays on your device unless a feature explicitly needs the network.",
            "Stored locally on your device: your saved trends and collections, your preferences (appearance, notifications, plan), and your recent searches.",
            "Fetched over the network: trend and pin content from the official Pinterest API, and demo content while Pinterest is not connected.",
            "What Pinwave never does: your Pinterest tokens are never sent to analytics or third parties, your data is never sold, and your saved items are never uploaded to a Pinwave server — in this build there is no account server at all.",
            "You can clear local data at any time from Settings → Delete account, or disconnect Pinterest from Settings → Pinterest.",
        ),
    ),
    "terms" to LegalPage(
        title = "Terms of Service",
        paragraphs = listOf(
            "This is a demo build of Pinwave. Content, metrics and AI analyses may be simulated and must not be treated as professional, financial or marketing advice.",
            "Trend and pin content shown in the app belongs to its respective creators and rights holders. Pinwave surfaces it for discovery and analysis only; no ownership is transferred.",
            "Acceptable use: don't use Pinwave to misrepresent trends, harass creators, or redistribute Pinterest content outside what the platform permits.",
            "The service is provided as-is, without warranties. Features may change, pause or disappear between builds.",
        ),
    ),
    "pinterest_api" to LegalPage(
        title = "Pinterest API Usage",
        paragraphs = listOf(
            "Pinwave uses only the official Pinterest API. It does not scrape Pinterest.",
            "The app only shows content the API returns under the authorized access granted to this application.",
            "Pinterest does not endorse Pinwave, and Pinwave is not affiliated with Pinterest.",
            "When content cannot be returned by the API, you may see: \"This content isn't available through the Pinterest API with the permissions currently available to this application.\"",
            "While Pinterest is not connected, Pinwave shows clearly-labelled demo data instead of live Pinterest content.",
        ),
    ),
    "data" to LegalPage(
        title = "Data & Permissions",
        paragraphs = listOf(
            "Pinterest access uses OAuth: you authorize Pinwave on Pinterest's own page, and the app receives a limited token — never your password. Only the scopes the app needs are requested.",
            "Retention: Pinterest content is cached briefly with expiration and is not stored long-term. Your saved trends, collections, preferences and recent searches stay in a local database on your device.",
            "Tokens are kept in encrypted storage and never leave your device except to talk to Pinterest.",
            "You are in control: disconnect Pinterest at any time in Settings → Pinterest, and remove all local data with Settings → Delete account.",
        ),
    ),
)

@Composable
fun LegalScreen(
    onBack: () -> Unit,
    viewModel: LegalViewModel = hiltViewModel(),
) {
    val page = pages[viewModel.page]

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(PulseDimens.ScreenPadding),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }
                Text(
                    text = page?.title ?: "Page not found",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
            Spacer(Modifier.height(18.dp))
        }

        if (page == null) {
            item {
                Text(
                    text = "This page doesn't exist. It may have moved or been renamed.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = PulseTextMuted,
                )
            }
        } else {
            item {
                SelectionContainer {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        page.paragraphs.forEach { paragraph ->
                            Text(
                                text = paragraph,
                                style = MaterialTheme.typography.bodyLarge,
                                color = PulseTextMuted,
                            )
                        }
                    }
                }
            }
        }
    }
}
