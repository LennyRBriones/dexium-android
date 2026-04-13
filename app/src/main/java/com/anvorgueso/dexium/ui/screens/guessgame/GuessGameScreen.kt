package com.anvorgueso.dexium.ui.screens.guessgame

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.anvorgueso.dexium.R
import com.anvorgueso.dexium.ui.components.GlassCard
import com.anvorgueso.dexium.ui.components.GradientBackground
import com.anvorgueso.dexium.ui.components.LoadingIndicator
import com.anvorgueso.dexium.ui.theme.DexiumGlass
import com.anvorgueso.dexium.ui.theme.ErrorRed
import com.anvorgueso.dexium.ui.theme.SuccessGreen

@Composable
fun GuessGameScreen(
    onBackClick: () -> Unit,
    viewModel: GuessGameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val glass = DexiumGlass.colors

    BackHandler { viewModel.showExitDialog() }

    if (uiState.showExitDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissExitDialog() },
            title = { Text(stringResource(R.string.guess_exit_title)) },
            text = { Text(stringResource(R.string.guess_exit_message)) },
            confirmButton = {
                TextButton(onClick = onBackClick) {
                    Text(
                        stringResource(R.string.guess_exit_confirm),
                        color = ErrorRed
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissExitDialog() }) {
                    Text(
                        stringResource(R.string.guess_exit_cancel),
                        color = glass.accent
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = Color.White,
            textContentColor = Color.White.copy(alpha = 0.8f)
        )
    }

    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            if (uiState.isLoading) {
                LoadingIndicator(message = stringResource(R.string.guess_loading))
                return@GradientBackground
            }

            if (uiState.error != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.guess_no_data),
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(32.dp)
                    )
                }
                return@GradientBackground
            }


            GameTopBar(
                currentRound = uiState.currentRound + 1,
                totalRounds = uiState.totalRounds,
                score = uiState.score,
                onBackClick = { viewModel.showExitDialog() },
                glass = glass
            )


            val progress by animateFloatAsState(
                targetValue = (uiState.currentRound + 1).toFloat() / uiState.totalRounds,
                animationSpec = tween(300),
                label = "progress"
            )
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = glass.accent,
                trackColor = glass.surface
            )

            Spacer(modifier = Modifier.height(16.dp))


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {

                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .drawBehind {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        glass.accent.copy(alpha = 0.15f),
                                        Color.Transparent
                                    ),
                                    radius = size.width * 0.8f
                                ),
                                radius = size.width * 0.8f
                            )
                        }
                )
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(uiState.artworkUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Pokemon",
                    modifier = Modifier.size(200.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.weight(0.3f))


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                val cryScale by animateFloatAsState(
                    targetValue = if (uiState.isPlayingCry) 1.1f else 1f,
                    animationSpec = tween(300),
                    label = "cryScale"
                )
                GlassCard(
                    modifier = Modifier
                        .size(56.dp)
                        .graphicsLayer {
                            scaleX = cryScale
                            scaleY = cryScale
                        }
                        .clickable { viewModel.playCry() },
                    cornerRadius = 28.dp,
                    contentPadding = 0.dp,
                    glowColor = if (uiState.isPlayingCry) glass.accent else glass.glowSoft
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = stringResource(R.string.guess_play_cry),
                            tint = if (uiState.isPlayingCry) glass.accent else Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                uiState.options.forEach { option ->
                    OptionButton(
                        text = option,
                        isSelected = uiState.selectedOption == option,
                        isCorrectAnswer = option == uiState.pokemonName,
                        showFeedback = uiState.showFeedback,
                        onClick = { viewModel.onOptionSelected(option) },
                        glass = glass
                    )
                }
            }
        }
    }
}

@Composable
private fun GameTopBar(
    currentRound: Int,
    totalRounds: Int,
    score: Int,
    onBackClick: () -> Unit,
    glass: com.anvorgueso.dexium.ui.theme.GlassColors
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp)
            .padding(top = 40.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = glass.accent
            )
        }

        Text(
            text = stringResource(R.string.guess_round, currentRound, totalRounds),
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.weight(1f))

        GlassCard(
            cornerRadius = 16.dp,
            contentPadding = 0.dp,
            enableGlow = false,
            modifier = Modifier.padding(end = 12.dp)
        ) {
            Text(
                text = stringResource(R.string.guess_score, score),
                style = MaterialTheme.typography.labelLarge,
                color = glass.accent,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun OptionButton(
    text: String,
    isSelected: Boolean,
    isCorrectAnswer: Boolean,
    showFeedback: Boolean,
    onClick: () -> Unit,
    glass: com.anvorgueso.dexium.ui.theme.GlassColors
) {
    val glowColor by animateColorAsState(
        targetValue = when {
            showFeedback && isCorrectAnswer -> SuccessGreen
            showFeedback && isSelected && !isCorrectAnswer -> ErrorRed
            else -> glass.accent.copy(alpha = 0.3f)
        },
        animationSpec = tween(300),
        label = "optionGlow"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            showFeedback && isCorrectAnswer -> SuccessGreen.copy(alpha = 0.6f)
            showFeedback && isSelected && !isCorrectAnswer -> ErrorRed.copy(alpha = 0.6f)
            else -> Color.White.copy(alpha = 0.15f)
        },
        animationSpec = tween(300),
        label = "optionBorder"
    )

    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, borderColor, shape)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        glowColor.copy(alpha = 0.1f),
                        glowColor.copy(alpha = 0.05f)
                    )
                ),
                shape
            )
            .clickable(enabled = !showFeedback) { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            if (showFeedback && isCorrectAnswer) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = SuccessGreen,
                    modifier = Modifier.size(24.dp)
                )
            } else if (showFeedback && isSelected && !isCorrectAnswer) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = ErrorRed,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
