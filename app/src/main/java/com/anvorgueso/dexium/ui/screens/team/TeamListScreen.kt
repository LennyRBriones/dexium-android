package com.anvorgueso.dexium.ui.screens.team

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.anvorgueso.dexium.domain.model.Team
import com.anvorgueso.dexium.ui.components.CardListShimmer
import com.anvorgueso.dexium.ui.components.GlassCard
import com.anvorgueso.dexium.ui.components.GlassTopBar
import com.anvorgueso.dexium.ui.components.GradientBackground
import com.anvorgueso.dexium.ui.theme.DexiumGlass
import com.anvorgueso.dexium.ui.theme.TextSecondary
import com.anvorgueso.dexium.ui.theme.TextTertiary

@Composable
fun TeamListScreen(
    onBackClick: () -> Unit,
    onCreateClick: () -> Unit,
    onTeamClick: (Long) -> Unit,
    viewModel: TeamListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val glass = DexiumGlass.colors

    GradientBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            GlassTopBar(
                title = stringResource(R.string.team_list_title),
                onBackClick = onBackClick
            )

            if (uiState.isLoading) {
                CardListShimmer(
                    rowCount = 4,
                    rowHeight = 118.dp,
                    modifier = Modifier.weight(1f)
                )
            } else if (uiState.teams.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.team_list_empty),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.teams, key = { it.id }) { team ->
                        TeamRow(
                            team = team,
                            onClick = { onTeamClick(team.id) },
                            onDelete = { viewModel.askDelete(team.id) }
                        )
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
                        .clickable(onClick = onCreateClick),
                    cornerRadius = 14.dp,
                    glowColor = glass.accent,
                    contentPadding = 14.dp
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = glass.accent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.team_create),
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }

    uiState.pendingDeleteId?.let {
        AlertDialog(
            onDismissRequest = viewModel::dismissDelete,
            containerColor = Color(0xFF0F1C2E),
            title = {
                Text(stringResource(R.string.team_delete_title), color = Color.White)
            },
            text = {
                Text(stringResource(R.string.team_delete_message), color = TextSecondary)
            },
            confirmButton = {
                TextButton(onClick = viewModel::confirmDelete) {
                    Text(stringResource(R.string.dialog_erase_confirm), color = glass.accent)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissDelete) {
                    Text(stringResource(R.string.dialog_cancel), color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun TeamRow(
    team: Team,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        cornerRadius = 16.dp
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = team.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = stringResource(
                            R.string.team_members_count,
                            team.members.size,
                            Team.MAX_MEMBERS
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                }
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = stringResource(R.string.team_delete),
                    tint = TextTertiary,
                    modifier = Modifier
                        .size(22.dp)
                        .clickable(onClick = onDelete)
                )
            }

            if (team.members.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    team.members.forEach { member ->
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(member.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = member.name,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }
            }
        }
    }
}
