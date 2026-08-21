package com.anvorgueso.dexium.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Skeletons for the app's loading states. Each one mirrors the layout it stands in for, so
 * content lands in place instead of shifting when it arrives.
 */

/** Grid of card placeholders, matching the Home grid's 3 columns and 6dp gaps. */
@Composable
fun PokemonGridShimmer(
    modifier: Modifier = Modifier,
    itemCount: Int = 18
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        userScrollEnabled = false,
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        items(count = itemCount) {
            ShimmerBlock(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(128.dp),
                shape = RoundedCornerShape(14.dp)
            )
        }
    }
}

/** Footer placeholder for paginated grids — replaces the load-more spinner. */
@Composable
fun GridFooterShimmer(modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        repeat(3) {
            ShimmerBlock(
                modifier = Modifier
                    .weight(1f)
                    .height(128.dp),
                shape = RoundedCornerShape(14.dp)
            )
        }
    }
}

/** Stack of card-shaped rows, for list screens (generations, teams, search results). */
@Composable
fun CardListShimmer(
    modifier: Modifier = Modifier,
    rowCount: Int = 6,
    rowHeight: Dp = 76.dp
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        repeat(rowCount) {
            ShimmerBlock(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(rowHeight),
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

/** Mirrors the guess-game round: artwork, cry button, then three answer buttons. */
@Composable
fun GuessGameShimmer(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        ShimmerLine(width = 120.dp, height = 16.dp)
        Spacer(modifier = Modifier.height(28.dp))
        ShimmerCircle(diameter = 200.dp)
        Spacer(modifier = Modifier.height(24.dp))
        ShimmerCircle(diameter = 56.dp)
        Spacer(modifier = Modifier.height(32.dp))
        repeat(3) {
            ShimmerBlock(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

/** Encounter rows for the detail screen's locations card. */
@Composable
fun LocationsShimmer(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        ShimmerLine(width = 90.dp, height = 11.dp)
        Spacer(modifier = Modifier.height(10.dp))
        repeat(3) { index ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    ShimmerLine(width = 150.dp)
                    Spacer(modifier = Modifier.height(4.dp))
                    ShimmerLine(width = 96.dp, height = 10.dp)
                }
                ShimmerBlock(
                    modifier = Modifier.width(64.dp).height(20.dp),
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                ShimmerLine(width = 30.dp, height = 11.dp)
            }
            if (index < 2) Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

/** Team detail: roster card, threats card, advice card. */
@Composable
fun TeamDetailShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        ShimmerBlock(
            modifier = Modifier.fillMaxWidth().height(320.dp),
            shape = RoundedCornerShape(16.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        ShimmerBlock(
            modifier = Modifier.fillMaxWidth().height(220.dp),
            shape = RoundedCornerShape(16.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        ShimmerBlock(
            modifier = Modifier.fillMaxWidth().height(120.dp),
            shape = RoundedCornerShape(16.dp)
        )
    }
}

/** Chat bubble placeholder, replacing the typing dots while the model answers. */
@Composable
fun ChatBubbleShimmer(modifier: Modifier = Modifier) {
    Column(modifier = modifier.width(180.dp)) {
        ShimmerLineFill(height = 11.dp)
        Spacer(modifier = Modifier.height(6.dp))
        ShimmerLine(width = 120.dp, height = 11.dp)
    }
}

/** Search-result rows for the team builder. */
@Composable
fun SearchResultsShimmer(
    modifier: Modifier = Modifier,
    rowCount: Int = 5
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        repeat(rowCount) {
            ShimmerBlock(
                modifier = Modifier.fillMaxWidth().height(64.dp),
                shape = RoundedCornerShape(14.dp)
            )
        }
    }
}
