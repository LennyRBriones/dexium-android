package com.anvorgueso.dexium.ui.screens.guessgame

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.anvorgueso.dexium.R
import com.anvorgueso.dexium.ui.components.GlassCard
import com.anvorgueso.dexium.ui.components.GlassTopBar
import com.anvorgueso.dexium.ui.components.GradientBackground
import com.anvorgueso.dexium.ui.theme.DexiumGlass
import com.anvorgueso.dexium.ui.theme.ErrorRed
import com.anvorgueso.dexium.ui.theme.SuccessGreen
import com.anvorgueso.dexium.ui.theme.WarningAmber

@Composable
fun GameResultScreen(
    score: Int,
    total: Int,
    roundResults: List<RoundResult>,
    onPlayAgain: () -> Unit,
    onBackToHome: () -> Unit
) {
    val glass = DexiumGlass.colors
    val percentage = if (total > 0) score.toFloat() / total else 0f

    val scoreColor = when {
        percentage >= 1f -> SuccessGreen
        percentage >= 0.7f -> Color(0xFF66BB6A)
        percentage >= 0.5f -> WarningAmber
        else -> ErrorRed
    }

    val message = stringResource(
        when {
            percentage >= 1f -> R.string.guess_result_perfect
            percentage >= 0.8f -> R.string.guess_result_great
            percentage >= 0.6f -> R.string.guess_result_good
            percentage >= 0.4f -> R.string.guess_result_ok
            else -> R.string.guess_result_poor
        }
    )

    GradientBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            GlassTopBar(title = stringResource(R.string.guess_result_title))

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
            ) {
                // Score card
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        glowColor = scoreColor,
                        contentPadding = 24.dp
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Stars
                            Row {
                                val stars = when {
                                    percentage >= 1f -> 3
                                    percentage >= 0.7f -> 2
                                    percentage >= 0.4f -> 1
                                    else -> 0
                                }
                                repeat(3) { index ->
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (index < stars) scoreColor else Color.White.copy(alpha = 0.15f),
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "$score/$total",
                                style = MaterialTheme.typography.displayLarge,
                                color = scoreColor,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 56.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = message,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Round results
                items(roundResults) { result ->
                    val resultColor = if (result.isCorrect) SuccessGreen else ErrorRed
                    val shape = RoundedCornerShape(16.dp)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(shape)
                            .border(
                                1.dp,
                                resultColor.copy(alpha = 0.3f),
                                shape
                            )
                            .background(
                                resultColor.copy(alpha = 0.08f),
                                shape
                            )
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(result.imageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = result.pokemonName,
                                modifier = Modifier.size(48.dp),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = result.pokemonName,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                if (!result.isCorrect) {
                                    Text(
                                        text = stringResource(R.string.guess_wrong_answer, result.pokemonName),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.5f)
                                    )
                                }
                            }
                            Icon(
                                imageVector = if (result.isCorrect) Icons.Default.Check else Icons.Default.Close,
                                contentDescription = null,
                                tint = resultColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                // Action buttons
                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Play Again
                    val playShape = RoundedCornerShape(20.dp)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(playShape)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        glass.accent.copy(alpha = 0.3f),
                                        glass.accent.copy(alpha = 0.15f)
                                    )
                                ),
                                playShape
                            )
                            .border(1.dp, glass.accent.copy(alpha = 0.4f), playShape)
                            .clickable { onPlayAgain() }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = glass.accent,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.guess_play_again),
                                style = MaterialTheme.typography.titleMedium,
                                color = glass.accent,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Back to Home
                    val homeShape = RoundedCornerShape(20.dp)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(homeShape)
                            .border(1.dp, Color.White.copy(alpha = 0.2f), homeShape)
                            .clickable { onBackToHome() }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.guess_back_home),
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
