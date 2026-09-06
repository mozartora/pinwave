package com.pinwave.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pinwave.core.ui.components.AppearAnimation
import com.pinwave.core.ui.components.pulseAlpha
import com.pinwave.core.ui.theme.PulseAccent
import com.pinwave.core.ui.theme.PulseBackground
import com.pinwave.core.ui.theme.PulseOutline
import com.pinwave.core.ui.theme.PulseTextMuted

private data class OnboardingPage(
    val headline: String,
    val subline: String,
)

private val statementPages = listOf(
    OnboardingPage(
        headline = "See what's about to trend.",
        subline = "Pinwave watches Pinterest so you don't have to.",
    ),
    OnboardingPage(
        headline = "Understand why it's trending.",
        subline = "Momentum, visual themes, and honest AI breakdowns.",
    ),
    OnboardingPage(
        headline = "Turn trends into content.",
        subline = "Hooks, scripts and ideas generated from what's actually rising.",
    ),
)

private const val PAGE_COUNT = 4

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val done by viewModel.done.collectAsStateWithLifecycle()
    LaunchedEffect(done) {
        if (done) onFinish()
    }

    val pagerState = rememberPagerState(pageCount = { PAGE_COUNT })

    Box(Modifier.fillMaxSize().background(PulseBackground)) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            if (page < statementPages.size) {
                StatementPage(statementPages[page])
            } else {
                ConnectPage(
                    onConnect = viewModel::connectDemo,
                    onSkip = viewModel::finishOnboarding,
                )
            }
        }

        if (pagerState.currentPage < statementPages.size) {
            TextButton(
                onClick = viewModel::finishOnboarding,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(top = 4.dp, end = 8.dp),
            ) {
                Text("Skip", color = PulseTextMuted)
            }
        }

        PageDots(
            count = PAGE_COUNT,
            current = pagerState.currentPage,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp),
        )
    }
}

@Composable
private fun StatementPage(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        AppearAnimation(0) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "✨",
                    style = MaterialTheme.typography.displayMedium,
                    modifier = Modifier.alpha(pulseAlpha()),
                )
                Spacer(Modifier.height(28.dp))
                Text(
                    page.headline,
                    style = MaterialTheme.typography.displayLarge,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(18.dp))
                Text(
                    page.subline,
                    style = MaterialTheme.typography.bodyLarge,
                    color = PulseTextMuted,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun ConnectPage(
    onConnect: () -> Unit,
    onSkip: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        AppearAnimation(0) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "◎",
                    style = MaterialTheme.typography.displayMedium,
                    color = PulseAccent,
                    modifier = Modifier.alpha(pulseAlpha()),
                )
                Spacer(Modifier.height(28.dp))
                Text(
                    "Connect Pinterest",
                    style = MaterialTheme.typography.displayLarge,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(18.dp))
                Text(
                    "Pinwave uses the official Pinterest API. We never scrape. " +
                        "Connect to unlock live trends from your account — " +
                        "or keep exploring with demo data.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = PulseTextMuted,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(36.dp))
                Button(
                    onClick = onConnect,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PulseAccent,
                        contentColor = PulseBackground,
                    ),
                ) {
                    Text("Connect Pinterest", style = MaterialTheme.typography.titleLarge)
                }
                Spacer(Modifier.height(4.dp))
                TextButton(onClick = onSkip) {
                    Text("Continue without connecting", color = PulseTextMuted)
                }
            }
        }
    }
}

@Composable
private fun PageDots(
    count: Int,
    current: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        repeat(count) { index ->
            val selected = index == current
            Box(
                Modifier
                    .height(6.dp)
                    .width(if (selected) 18.dp else 6.dp)
                    .clip(CircleShape)
                    .background(if (selected) PulseAccent else PulseOutline),
            )
        }
    }
}
