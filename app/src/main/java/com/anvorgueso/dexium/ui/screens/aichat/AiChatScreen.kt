package com.anvorgueso.dexium.ui.screens.aichat

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.anvorgueso.dexium.R
import com.anvorgueso.dexium.domain.model.ChatMessage
import com.anvorgueso.dexium.domain.model.Pokemon
import com.anvorgueso.dexium.ui.components.GlassTopBar
import com.anvorgueso.dexium.ui.components.GradientBackground
import com.anvorgueso.dexium.ui.components.PokemonCard
import com.anvorgueso.dexium.ui.theme.DexiumGlass
import com.anvorgueso.dexium.ui.theme.TextSecondary

@Composable
fun AiChatScreen(
    onBackClick: () -> Unit,
    onPokemonClick: (Int) -> Unit,
    viewModel: AiChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val glass = DexiumGlass.colors
    val listState = rememberLazyListState()
    val welcomeText = stringResource(R.string.ai_chat_welcome)

    LaunchedEffect(Unit) {
        viewModel.initWelcomeMessage(welcomeText)
    }

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            GlassTopBar(
                title = stringResource(R.string.ai_chat_title),
                onBackClick = onBackClick
            )

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = uiState.messages,
                    key = { it.id }
                ) { message ->
                    ChatBubble(
                        message = message,
                        onPokemonClick = onPokemonClick,
                        glass = glass
                    )
                }
            }

            ChatInputBar(
                inputText = uiState.inputText,
                isProcessing = uiState.isProcessing,
                onInputChange = viewModel::onInputChange,
                onSend = viewModel::sendMessage,
                glass = glass,
                modifier = Modifier.navigationBarsPadding()
            )
        }
    }
}

@Composable
private fun ChatBubble(
    message: ChatMessage,
    onPokemonClick: (Int) -> Unit,
    glass: com.anvorgueso.dexium.ui.theme.GlassColors
) {
    val alignment = if (message.isFromUser) Alignment.End else Alignment.Start
    val bubbleShape = if (message.isFromUser) {
        RoundedCornerShape(18.dp, 4.dp, 18.dp, 18.dp)
    } else {
        RoundedCornerShape(4.dp, 18.dp, 18.dp, 18.dp)
    }

    val bubbleBrush = if (message.isFromUser) {
        Brush.horizontalGradient(
            listOf(
                glass.accent.copy(alpha = 0.3f),
                glass.accent.copy(alpha = 0.18f)
            )
        )
    } else {
        Brush.horizontalGradient(
            listOf(
                Color.White.copy(alpha = 0.08f),
                Color.White.copy(alpha = 0.04f)
            )
        )
    }

    val borderColor = if (message.isFromUser) {
        glass.accent.copy(alpha = 0.4f)
    } else {
        Color.White.copy(alpha = 0.15f)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(bubbleShape)
                .border(1.dp, borderColor, bubbleShape)
                .background(bubbleBrush, bubbleShape)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            when {
                message.isLoading -> TypingIndicator(glass)
                message.isError && message.pokemonResults.isEmpty() -> ErrorContent(message.content)
                message.pokemonResults.isNotEmpty() -> ResultsHeader()
                else -> Text(
                    text = message.content,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        if (message.pokemonResults.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            PokemonResultsGrid(
                pokemon = message.pokemonResults,
                onPokemonClick = onPokemonClick
            )
        }
    }
}

@Composable
private fun ResultsHeader() {
    Text(
        text = stringResource(R.string.ai_chat_results),
        color = Color.White,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Medium
    )
}

@Composable
private fun ErrorContent(errorCode: String) {
    val message = when (errorCode) {
        "NO_INTERNET" -> stringResource(R.string.ai_chat_no_internet)
        "RATE_LIMIT" -> stringResource(R.string.ai_chat_rate_limit)
        "INVALID_KEY" -> stringResource(R.string.ai_chat_no_api_key)
        "MODEL_UNAVAILABLE" -> stringResource(R.string.ai_chat_model_unavailable)
        "EMPTY_RESPONSE", "SERVER_ERROR" -> stringResource(R.string.ai_chat_error)
        else -> stringResource(R.string.ai_chat_no_results)
    }
    Text(
        text = message,
        color = Color.White,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
private fun TypingIndicator(glass: com.anvorgueso.dexium.ui.theme.GlassColors) {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, delayMillis = index * 150),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot_$index"
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(glass.accent.copy(alpha = alpha))
            )
        }
    }
}

@Composable
private fun PokemonResultsGrid(
    pokemon: List<Pokemon>,
    onPokemonClick: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        pokemon.chunked(3).forEach { rowPokemon ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowPokemon.forEach { p ->
                    Box(modifier = Modifier.weight(1f)) {
                        PokemonCard(
                            pokemon = p,
                            onClick = { onPokemonClick(p.id) }
                        )
                    }
                }
                repeat(3 - rowPokemon.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ChatInputBar(
    inputText: String,
    isProcessing: Boolean,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    glass: com.anvorgueso.dexium.ui.theme.GlassColors,
    modifier: Modifier = Modifier
) {
    val keyboard = LocalSoftwareKeyboardController.current
    val shape = RoundedCornerShape(28.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(shape)
                .border(1.dp, Color.White.copy(alpha = 0.15f), shape)
                .background(Color.White.copy(alpha = 0.06f), shape)
        ) {
            TextField(
                value = inputText,
                onValueChange = onInputChange,
                placeholder = {
                    Text(
                        text = stringResource(R.string.ai_chat_input_hint),
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isProcessing,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White,
                    fontSize = 15.sp
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        onSend()
                        keyboard?.hide()
                    }
                ),
                maxLines = 4,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = glass.accent
                )
            )
        }

        val canSend = inputText.isNotBlank() && !isProcessing
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    if (canSend) {
                        Brush.radialGradient(
                            listOf(
                                glass.accent.copy(alpha = 0.5f),
                                glass.accent.copy(alpha = 0.25f)
                            )
                        )
                    } else {
                        Brush.radialGradient(
                            listOf(
                                Color.White.copy(alpha = 0.08f),
                                Color.White.copy(alpha = 0.04f)
                            )
                        )
                    }
                )
                .border(
                    1.dp,
                    if (canSend) glass.accent.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.15f),
                    CircleShape
                )
                .clickable(enabled = canSend) {
                    onSend()
                    keyboard?.hide()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = stringResource(R.string.ai_chat_send),
                tint = if (canSend) Color.White else TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
