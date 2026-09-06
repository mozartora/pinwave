package com.pinwave.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.pinwave.core.ui.theme.PulseSurfaceHigh
import com.pinwave.domain.model.Pin

/** Masonry pin explorer (spec §9) — images dominate; metadata stays minimal. */
@Composable
fun PinMasonryGrid(
    pins: List<Pin>,
    onPinClick: (Pin) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    header: (@Composable () -> Unit)? = null,
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalItemSpacing = 10.dp,
    ) {
        if (header != null) {
            item(span = StaggeredGridItemSpan.FullLine) { header() }
        }
        itemsIndexed(pins, key = { _, pin -> pin.id }) { index, pin ->
            AppearAnimation(index = index % 8) {
                AsyncImage(
                    model = pin.imageUrl,
                    contentDescription = pin.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(pin.aspectRatio)
                        .clip(RoundedCornerShape(18.dp))
                        .background(PulseSurfaceHigh)
                        .clickable { onPinClick(pin) },
                )
            }
        }
    }
}
