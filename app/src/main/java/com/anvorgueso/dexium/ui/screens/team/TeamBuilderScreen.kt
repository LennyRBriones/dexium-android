package com.anvorgueso.dexium.ui.screens.team

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.anvorgueso.dexium.R
import com.anvorgueso.dexium.core.util.formatPokemonId
import com.anvorgueso.dexium.domain.model.Team
import com.anvorgueso.dexium.ui.components.DexiumSearchBar
import com.anvorgueso.dexium.ui.components.GlassCard
import com.anvorgueso.dexium.ui.components.GlassTopBar
import com.anvorgueso.dexium.ui.components.GradientBackground
import com.anvorgueso.dexium.ui.components.SearchResultsShimmer
import com.anvorgueso.dexium.ui.components.TypeSymbol
import com.anvorgueso.dexium.ui.theme.DexiumGlass
import com.anvorgueso.dexium.ui.theme.PokemonTypeColors
import com.anvorgueso.dexium.ui.theme.TextSecondary
import com.anvorgueso.dexium.ui.theme.TextTertiary

@Composable
fun TeamBuilderScreen(
    onBackClick: () -> Unit,
    onSaved: (Long) -> Unit,
    viewModel: TeamBuilderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val glass = DexiumGlass.colors

    LaunchedEffect(uiState.savedId) {
        uiState.savedId?.let(onSaved)
    }

    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            GlassTopBar(
                title = stringResource(
                    if (uiState.isEditing) R.string.team_edit_title
                    else R.string.team_builder_title
                ),
                onBackClick = onBackClick
            )

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Spacer(modifier = Modifier.height(18.dp))

                TextField(
                    value = uiState.teamName,
                    onValueChange = viewModel::onNameChange,
                    placeholder = {
                        Text(
                            text = stringResource(R.string.team_name_hint),
                            color = TextTertiary
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp)),
                    textStyle = MaterialTheme.typography.titleSmall.copy(color = Color.White),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.06f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.06f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = glass.accent
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Roster: six slots, filled ones tappable to remove.
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(Team.MAX_MEMBERS) { index ->
                        val member = uiState.members.getOrNull(index)
                        Box(modifier = Modifier.weight(1f)) {
                            if (member == null) {
                                EmptySlot()
                            } else {
                                FilledSlot(
                                    imageUrl = member.imageUrl,
                                    name = member.name,
                                    accent = PokemonTypeColors.getColor(member.typePrimary),
                                    onRemove = { viewModel.removeMemberAt(index) }
                                )
                            }
                        }
                    }
                }

                uiState.validationError?.let { code ->
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = when (code) {
                            "TEAM_FULL" -> stringResource(R.string.team_full)
                            "NEEDS_NAME" -> stringResource(R.string.team_needs_name)
                            else -> stringResource(R.string.team_needs_member)
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFFF8A80)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                DexiumSearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = viewModel::onQueryChange,
                    placeholder = stringResource(R.string.team_search_hint)
                )
            }

            if (uiState.isSearching && uiState.results.isEmpty()) {
                SearchResultsShimmer(modifier = Modifier.weight(1f))
            } else
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.results, key = { it.id }) { pokemon ->
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !uiState.isFull) {
                                viewModel.addMember(pokemon)
                            },
                        cornerRadius = 14.dp,
                        contentPadding = 10.dp
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(pokemon.imageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = pokemon.name,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = pokemon.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = Color.White
                                )
                                Text(
                                    text = pokemon.id.formatPokemonId(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextTertiary
                                )
                            }
                            if (pokemon.hasRealType) {
                                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                    TypeSymbol(type = pokemon.typePrimary, iconSize = 18.dp)
                                    pokemon.typeSecondary?.let {
                                        TypeSymbol(type = it, iconSize = 18.dp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = if (uiState.isFull) TextTertiary else glass.accent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp)
            ) {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = uiState.canSave) { viewModel.save() },
                    cornerRadius = 14.dp,
                    glowColor = if (uiState.canSave) glass.accent else null,
                    contentPadding = 14.dp
                ) {
                    Text(
                        text = stringResource(R.string.team_save),
                        style = MaterialTheme.typography.titleSmall,
                        color = if (uiState.canSave) Color.White else TextSecondary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptySlot() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.04f))
            .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(12.dp))
    )
}

@Composable
private fun FilledSlot(
    imageUrl: String,
    name: String,
    accent: Color,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(accent.copy(alpha = 0.16f))
            .border(1.dp, accent.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
            .clickable(onClick = onRemove),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = name,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(40.dp)
        )
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(2.dp)
                .size(12.dp)
                .clip(CircleShape)
        )
    }
}
