package com.pinwave.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.pinwave.core.ui.theme.PulseAccent
import com.pinwave.core.ui.theme.PulseSurface
import com.pinwave.core.ui.theme.PulseTextFaint

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String,
)

/**
 * Custom bottom bar: flat, minimal, with the center Radar destination
 * visually emphasized by a ring (spec §29).
 */
@Composable
fun PulseBottomBar(
    items: List<BottomNavItem>,
    currentRoute: String?,
    onSelect: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PulseSurface)
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            val emphasized = item.label.equals("Radar", ignoreCase = true)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onSelect(item.route) }
                    .padding(horizontal = 14.dp, vertical = 6.dp),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = if (emphasized) {
                        Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (selected) PulseAccent.copy(alpha = 0.18f) else Color.Transparent)
                    } else {
                        Modifier.size(30.dp)
                    },
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.contentDescription,
                        tint = if (selected) PulseAccent else PulseTextFaint,
                        modifier = Modifier.size(if (emphasized) 26.dp else 22.dp),
                    )
                }
                Text(
                    item.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (selected) PulseAccent else PulseTextFaint,
                )
            }
        }
    }
}
