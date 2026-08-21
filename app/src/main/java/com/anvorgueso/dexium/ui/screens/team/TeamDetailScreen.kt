package com.anvorgueso.dexium.ui.screens.team

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
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
import com.anvorgueso.dexium.ui.components.GeminiButton
import com.anvorgueso.dexium.ui.components.GlassCard
import com.anvorgueso.dexium.ui.components.GlassTopBar
import com.anvorgueso.dexium.ui.components.GradientBackground
import com.anvorgueso.dexium.ui.components.ShimmerLine
import com.anvorgueso.dexium.ui.components.ShimmerLineFill
import com.anvorgueso.dexium.ui.components.TeamDetailShimmer
import com.anvorgueso.dexium.ui.components.TeamCoverageList
import com.anvorgueso.dexium.ui.components.TypeSymbol
import com.anvorgueso.dexium.ui.theme.DexiumGlass
import com.anvorgueso.dexium.ui.theme.TextSecondary
import com.anvorgueso.dexium.ui.theme.TextTertiary

@Composable
fun TeamDetailScreen(
    onBackClick: () -> Unit,
    onEditClick: (Long) -> Unit,
    onPokemonClick: (Int, Boolean) -> Unit,
    viewModel: TeamDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val locale = LocalConfiguration.current.locales[0].language
    val team = uiState.team

    GradientBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            GlassTopBar(
                title = team?.name ?: stringResource(R.string.team_detail_title),
                onBackClick = onBackClick,
                actions = {
                    if (team != null) {
                        IconButton(onClick = { onEditClick(team.id) }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = stringResource(R.string.team_edit),
                                tint = DexiumGlass.colors.accent
                            )
                        }
                    }
                }
            )

            if (team == null) {
                TeamDetailShimmer()
                return@Column
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Roster
                GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 16.dp) {
                    Column {
                        Text(
                            text = stringResource(
                                R.string.team_members_count,
                                team.members.size,
                                Team.MAX_MEMBERS
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        team.members.forEachIndexed { index, member ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onPokemonClick(member.id, false) }
                                    .padding(vertical = 5.dp)
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(member.imageUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = member.name,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.size(44.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = member.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Color.White
                                    )
                                    Text(
                                        text = member.id.formatPokemonId(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextTertiary
                                    )
                                }
                                if (member.hasRealType) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                        TypeSymbol(type = member.typePrimary, iconSize = 18.dp)
                                        member.typeSecondary?.let {
                                            TypeSymbol(type = it, iconSize = 18.dp)
                                        }
                                    }
                                }
                            }
                            if (index < team.members.lastIndex) {
                                Spacer(modifier = Modifier.height(2.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Threats
                GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 16.dp) {
                    Column {
                        Text(
                            text = stringResource(R.string.team_threats),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (uiState.coverage.none { it.weakCount > 0 }) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = stringResource(R.string.team_no_threats),
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        } else {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.team_threats_explain),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextTertiary
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            TeamCoverageList(
                                coverage = uiState.coverage,
                                locale = locale,
                                noAnswerLabel = stringResource(R.string.team_no_answer),
                                resistLabel = { n ->
                                    if (n == 1) stringResource(R.string.team_resist_one)
                                    else stringResource(R.string.team_resist_n, n)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Gemini advice
                GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 16.dp) {
                    Column {
                        Text(
                            text = stringResource(R.string.team_ai_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        GeminiButton(
                            label = stringResource(
                                if (uiState.advice != null) R.string.team_ai_again
                                else R.string.team_ai_button
                            ),
                            enabled = team.members.isNotEmpty(),
                            loading = uiState.isAsking,
                            onClick = viewModel::askGemini
                        )

                        if (uiState.isAsking) {
                            Spacer(modifier = Modifier.height(14.dp))
                            AdviceShimmer()
                        }

                        uiState.adviceError?.let { code ->
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = when (code) {
                                    "NO_INTERNET" -> stringResource(R.string.ai_chat_no_internet)
                                    "RATE_LIMIT" -> stringResource(R.string.ai_chat_rate_limit)
                                    "INVALID_KEY" -> stringResource(R.string.ai_chat_no_api_key)
                                    "MODEL_UNAVAILABLE" ->
                                        stringResource(R.string.ai_chat_model_unavailable)
                                    "EMPTY_TEAM" -> stringResource(R.string.team_ai_empty_team)
                                    else -> stringResource(R.string.team_ai_error)
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }

                        uiState.advice?.let { text ->
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.92f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/** Shimmering text lines, so the wait reads as "an answer is coming here". */
@Composable
private fun AdviceShimmer() {
    Column(modifier = Modifier.fillMaxWidth()) {
        ShimmerLine(width = 120.dp, height = 12.dp)
        Spacer(modifier = Modifier.height(8.dp))
        ShimmerLineFill()
        Spacer(modifier = Modifier.height(6.dp))
        ShimmerLineFill()
        Spacer(modifier = Modifier.height(6.dp))
        ShimmerLine(width = 220.dp)
        Spacer(modifier = Modifier.height(12.dp))
        ShimmerLine(width = 140.dp, height = 12.dp)
        Spacer(modifier = Modifier.height(8.dp))
        ShimmerLineFill()
        Spacer(modifier = Modifier.height(6.dp))
        ShimmerLine(width = 180.dp)
    }
}
