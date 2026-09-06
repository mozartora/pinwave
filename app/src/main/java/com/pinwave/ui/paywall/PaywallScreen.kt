package com.pinwave.ui.paywall

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pinwave.core.ui.components.OverlineLabel
import com.pinwave.core.ui.components.pulseAlpha
import com.pinwave.core.ui.theme.PulseAccent
import com.pinwave.core.ui.theme.PulseDimens
import com.pinwave.core.ui.theme.PulseSurface
import com.pinwave.core.ui.theme.PulseSurfaceHigh
import com.pinwave.core.ui.theme.PulseTextFaint
import com.pinwave.core.ui.theme.PulseTextMuted
import com.pinwave.domain.model.Plan

private data class PlanDetails(val plan: Plan, val price: String, val bullets: List<String>)

private val planDetails = listOf(
    PlanDetails(
        plan = Plan.FREE,
        price = "$0",
        bullets = listOf(
            "Limited trend searches",
            "Limited AI analyses",
            "Limited saved collections",
        ),
    ),
    PlanDetails(
        plan = Plan.PRO,
        price = "$12.99/month",
        bullets = listOf(
            "Unlimited trend exploration",
            "More AI analyses",
            "Trend history",
            "Advanced discovery",
            "Content generation",
        ),
    ),
    PlanDetails(
        plan = Plan.CREATOR,
        price = "$29.99/month",
        bullets = listOf(
            "Advanced analytics",
            "Multiple collections",
            "Exports",
            "Advanced AI",
            "Higher limits",
        ),
    ),
)

@Composable
fun PaywallScreen(
    onBack: () -> Unit,
    viewModel: PaywallViewModel = hiltViewModel(),
) {
    val currentPlan by viewModel.currentPlan.collectAsStateWithLifecycle()
    val subscribing by viewModel.subscribing.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(PulseDimens.ScreenPadding),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Go further.",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Pinwave is free to explore. Upgrade when the trends start paying rent.",
            style = MaterialTheme.typography.bodyLarge,
            color = PulseTextMuted,
        )
        Spacer(Modifier.height(24.dp))

        planDetails.forEach { details ->
            PlanCard(
                details = details,
                isCurrent = currentPlan == details.plan,
                enabled = !subscribing,
                onChoose = { viewModel.subscribe(details.plan) },
            )
            Spacer(Modifier.height(PulseDimens.CardSpacing))
        }

        if (subscribing) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = PulseAccent,
                trackColor = PulseSurfaceHigh,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Talking to the demo register ✨",
                style = MaterialTheme.typography.bodyMedium,
                color = PulseTextMuted,
                modifier = Modifier.alpha(pulseAlpha()),
            )
            Spacer(Modifier.height(10.dp))
        }

        TextButton(
            onClick = viewModel::restore,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Text("Restore purchases", color = PulseAccent)
        }
        Text(
            text = "Demo billing — Google Play Billing wires in at launch.",
            style = MaterialTheme.typography.bodyMedium,
            color = PulseTextFaint,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun PlanCard(
    details: PlanDetails,
    isCurrent: Boolean,
    enabled: Boolean,
    onChoose: () -> Unit,
) {
    val emphasized = details.plan == Plan.PRO
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(if (emphasized) PulseSurfaceHigh else PulseSurface)
            .then(
                if (isCurrent || emphasized) {
                    Modifier.border(1.5.dp, PulseAccent, RoundedCornerShape(24.dp))
                } else {
                    Modifier
                },
            )
            .padding(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                details.plan.label,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (isCurrent) {
                OverlineLabel("Current", color = PulseAccent)
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            details.price,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(12.dp))
        details.bullets.forEach { bullet ->
            Text(
                text = "· $bullet",
                style = MaterialTheme.typography.bodyMedium,
                color = PulseTextMuted,
            )
            Spacer(Modifier.height(4.dp))
        }
        Spacer(Modifier.height(10.dp))
        if (isCurrent) {
            Button(
                onClick = {},
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Current plan")
            }
        } else if (emphasized) {
            Button(
                onClick = onChoose,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PulseAccent),
            ) {
                Text("Choose ${details.plan.label}")
            }
        } else {
            OutlinedButton(
                onClick = onChoose,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Choose ${details.plan.label}", color = PulseAccent)
            }
        }
    }
}
