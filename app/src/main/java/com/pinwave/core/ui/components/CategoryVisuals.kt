package com.pinwave.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pinwave.core.ui.theme.AccentBeauty
import com.pinwave.core.ui.theme.AccentEcommerce
import com.pinwave.core.ui.theme.AccentFashion
import com.pinwave.core.ui.theme.AccentFitness
import com.pinwave.core.ui.theme.AccentFood
import com.pinwave.core.ui.theme.AccentHome
import com.pinwave.core.ui.theme.AccentLifestyle
import com.pinwave.core.ui.theme.AccentPhotography
import com.pinwave.core.ui.theme.AccentTravel
import com.pinwave.core.ui.theme.AccentWedding
import com.pinwave.core.ui.theme.PulseSurfaceHigh
import com.pinwave.core.ui.theme.PulseTextFaint
import com.pinwave.core.ui.theme.PulseTextMuted
import com.pinwave.domain.model.TrendCategory

/** The single place where categories get their accent (spec §4). */
fun TrendCategory.accent(): Color = when (this) {
    TrendCategory.FASHION -> AccentFashion
    TrendCategory.BEAUTY -> AccentBeauty
    TrendCategory.TRAVEL -> AccentTravel
    TrendCategory.HOME -> AccentHome
    TrendCategory.FOOD -> AccentFood
    TrendCategory.WEDDING -> AccentWedding
    TrendCategory.FITNESS -> AccentFitness
    TrendCategory.PHOTOGRAPHY -> AccentPhotography
    TrendCategory.ECOMMERCE -> AccentEcommerce
    TrendCategory.LIFESTYLE -> AccentLifestyle
}

/** Tiny category dot — the only always-on category color in dense UIs. */
@Composable
fun CategoryDot(category: TrendCategory, modifier: Modifier = Modifier) {
    Box(
        modifier
            .size(7.dp)
            .clip(CircleShape)
            .background(category.accent()),
    )
}

@Composable
fun CategoryChip(
    label: String,
    selected: Boolean,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bg = if (selected) accent.copy(alpha = 0.16f) else PulseSurfaceHigh
    val fg = if (selected) accent else PulseTextMuted
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        if (selected) {
            Box(Modifier.size(6.dp).clip(CircleShape).background(accent))
        }
        Text(label, style = MaterialTheme.typography.labelLarge, color = fg)
    }
}

@Composable
fun CategoryChipRow(
    selected: TrendCategory?,
    onSelect: (TrendCategory?) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            CategoryChip(
                label = "All",
                selected = selected == null,
                accent = MaterialTheme.colorScheme.primary,
                onClick = { onSelect(null) },
            )
        }
        items(TrendCategory.entries, key = { it.name }) { category ->
            CategoryChip(
                label = category.label,
                selected = selected == category,
                accent = category.accent(),
                onClick = { onSelect(category) },
            )
        }
    }
}

/** Small label used to mark sections powered by demo data vs AI. */
@Composable
fun OverlineLabel(text: String, color: Color = PulseTextFaint, modifier: Modifier = Modifier) {
    Text(
        text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = color,
        modifier = modifier,
    )
}
