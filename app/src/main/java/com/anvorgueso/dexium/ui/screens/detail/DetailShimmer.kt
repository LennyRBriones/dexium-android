package com.anvorgueso.dexium.ui.screens.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anvorgueso.dexium.ui.components.GlassCard
import com.anvorgueso.dexium.ui.components.ShimmerBlock
import com.anvorgueso.dexium.ui.components.ShimmerCircle
import com.anvorgueso.dexium.ui.components.ShimmerLine
import com.anvorgueso.dexium.ui.components.ShimmerLineFill

/**
 * Skeleton for [DetailScreen] while the detail loads. Mirrors the real layout card for card
 * and keeps the same spacing, so content lands in place instead of shifting when it arrives.
 * The sprite/stat/ability counts are fixed guesses — the real ones are unknown until the
 * payload lands, and a stable skeleton beats one that resizes.
 */
@Composable
fun DetailShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        // Hero: id, sprite, type badges + cry button
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            ShimmerLine(width = 64.dp, height = 18.dp)

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                ShimmerCircle(diameter = 180.dp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShimmerBlock(
                    modifier = Modifier.width(92.dp).height(26.dp),
                    shape = RoundedCornerShape(8.dp)
                )
                ShimmerBlock(
                    modifier = Modifier.width(92.dp).height(26.dp),
                    shape = RoundedCornerShape(8.dp)
                )
                ShimmerCircle(diameter = 34.dp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Sprites card: title + a toggle row
        ShimmerCard {
            SectionTitle(width = 90.dp)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShimmerLine(width = 130.dp)
                Spacer(modifier = Modifier.weight(1f))
                ShimmerBlock(
                    modifier = Modifier.width(52.dp).height(28.dp),
                    shape = RoundedCornerShape(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Evolution card: title + three stages with arrows between them
        ShimmerCard {
            SectionTitle(width = 150.dp)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        ShimmerCircle(diameter = 64.dp)
                        Spacer(modifier = Modifier.height(4.dp))
                        ShimmerLine(width = 56.dp, height = 11.dp)
                    }
                    if (index < 2) {
                        ShimmerLine(width = 16.dp, height = 11.dp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Stats card: title + six stat bars (HP..SPD)
        ShimmerCard {
            SectionTitle(width = 160.dp)
            Spacer(modifier = Modifier.height(12.dp))
            repeat(6) { index ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ShimmerLine(width = 38.dp, height = 12.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    ShimmerLine(width = 28.dp, height = 12.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    ShimmerLineFill(height = 10.dp, modifier = Modifier.weight(1f))
                }
                if (index < 5) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // About card: title, genus, description lines, three info items, habitat
        ShimmerCard {
            SectionTitle(width = 110.dp)
            Spacer(modifier = Modifier.height(8.dp))
            ShimmerLine(width = 140.dp)
            Spacer(modifier = Modifier.height(10.dp))
            ShimmerLineFill()
            Spacer(modifier = Modifier.height(6.dp))
            ShimmerLineFill()
            Spacer(modifier = Modifier.height(6.dp))
            ShimmerLine(width = 200.dp)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(3) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        ShimmerLine(width = 54.dp, height = 15.dp)
                        Spacer(modifier = Modifier.height(4.dp))
                        ShimmerLine(width = 40.dp, height = 10.dp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            ShimmerLine(width = 130.dp, height = 11.dp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Abilities card: title + two ability rows
        ShimmerCard {
            SectionTitle(width = 120.dp)
            Spacer(modifier = Modifier.height(10.dp))
            repeat(2) { index ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ShimmerLine(width = if (index == 0) 120.dp else 96.dp)
                    if (index == 1) {
                        Spacer(modifier = Modifier.width(8.dp))
                        ShimmerBlock(
                            modifier = Modifier.width(56.dp).height(18.dp),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
                if (index == 0) Spacer(modifier = Modifier.height(10.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Locations card: title + game selector pill + three encounter rows
        ShimmerCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionTitle(width = 170.dp)
                Spacer(modifier = Modifier.weight(1f))
                ShimmerBlock(
                    modifier = Modifier.width(120.dp).height(28.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            ShimmerLine(width = 90.dp, height = 11.dp)
            Spacer(modifier = Modifier.height(10.dp))
            repeat(3) { index ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
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

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ShimmerCard(content: @Composable () -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 16.dp) {
        Column { content() }
    }
}

@Composable
private fun SectionTitle(width: Dp) {
    ShimmerLine(width = width, height = 18.dp)
}
