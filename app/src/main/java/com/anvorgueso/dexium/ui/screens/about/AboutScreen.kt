package com.anvorgueso.dexium.ui.screens.about

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.anvorgueso.dexium.ui.components.GlassCard
import com.anvorgueso.dexium.ui.components.GlassTopBar
import com.anvorgueso.dexium.ui.components.GradientBackground
import com.anvorgueso.dexium.ui.theme.DexiumGlass
import com.anvorgueso.dexium.ui.theme.ErrorRed
import com.anvorgueso.dexium.ui.theme.TextSecondary
import com.anvorgueso.dexium.ui.theme.TextTertiary
import androidx.compose.ui.res.stringResource
import com.anvorgueso.dexium.R

@Composable
fun AboutScreen(
    onBackClick: () -> Unit,
    viewModel: AboutViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val glass = DexiumGlass.colors

    var showDownloadAlert by remember { mutableStateOf(false) }
    var showEraseAlert by remember { mutableStateOf(false) }
    var showAnimatedAlert by remember { mutableStateOf(false) }

    if (showDownloadAlert) {
        DataWarningDialog(
            title = stringResource(R.string.dialog_download_title),
            message = stringResource(R.string.dialog_download_message),
            confirmText = stringResource(R.string.dialog_download_confirm),
            onConfirm = {
                showDownloadAlert = false
                viewModel.syncAllData()
            },
            onDismiss = { showDownloadAlert = false }
        )
    }

    if (showEraseAlert) {
        DataWarningDialog(
            title = stringResource(R.string.dialog_erase_title),
            message = stringResource(R.string.dialog_erase_message),
            confirmText = stringResource(R.string.dialog_erase_confirm),
            isDestructive = true,
            onConfirm = {
                showEraseAlert = false
                viewModel.eraseDownloadedData()
            },
            onDismiss = { showEraseAlert = false }
        )
    }

    if (showAnimatedAlert) {
        DataWarningDialog(
            title = stringResource(R.string.dialog_animated_title),
            message = stringResource(R.string.dialog_animated_message),
            confirmText = stringResource(R.string.dialog_animated_confirm),
            onConfirm = {
                showAnimatedAlert = false
                viewModel.toggleHdImages(true)
            },
            onDismiss = { showAnimatedAlert = false }
        )
    }

    GradientBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            GlassTopBar(
                title = stringResource(R.string.about_title),
                onBackClick = onBackClick
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.anvorgueso.dexium.R.mipmap.ic_launcher_foreground),
                    contentDescription = null,
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(20.dp))
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Dexium",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = stringResource(R.string.about_version),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextTertiary
                )


                Spacer(modifier = Modifier.height(32.dp))

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 16.dp
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.settings_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.settings_animated_sprites),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.White
                                )
                                Text(
                                    text = if (uiState.useHdImages) stringResource(R.string.settings_animated_on)
                                    else stringResource(R.string.settings_animated_off),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextTertiary
                                )
                            }

                            Switch(
                                checked = uiState.useHdImages,
                                onCheckedChange = { enabled ->
                                    if (enabled) {
                                        showAnimatedAlert = true
                                    } else {
                                        viewModel.toggleHdImages(false)
                                    }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = glass.accent,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = glass.surface
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.settings_imperial),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.White
                                )
                                Text(
                                    text = if (uiState.useImperialUnits) stringResource(R.string.settings_imperial_on)
                                    else stringResource(R.string.settings_imperial_off),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextTertiary
                                )
                            }

                            Switch(
                                checked = uiState.useImperialUnits,
                                onCheckedChange = viewModel::toggleImperialUnits,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = glass.accent,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = glass.surface
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 16.dp
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.data_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(R.string.data_cached, uiState.cachedPokemonCount, uiState.totalPokemonCount),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )

                        if (uiState.syncProgress.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = uiState.syncProgress,
                                style = MaterialTheme.typography.bodySmall,
                                color = glass.accent
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { showDownloadAlert = true },
                            enabled = !uiState.isSyncing,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = glass.accent.copy(alpha = 0.2f),
                                contentColor = Color.White,
                                disabledContainerColor = glass.surface,
                                disabledContentColor = TextTertiary
                            )
                        ) {
                            if (uiState.isSyncing) {
                                CircularProgressIndicator(
                                    color = glass.accent,
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.CloudDownload,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = if (uiState.isSyncing) stringResource(R.string.data_downloading) else stringResource(R.string.data_download)
                            )
                        }

                        if (uiState.cachedPokemonCount > 0 && !uiState.isSyncing) {
                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = { showEraseAlert = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ErrorRed.copy(alpha = 0.15f),
                                    contentColor = ErrorRed
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteForever,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                                Text(text = stringResource(R.string.data_erase))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 16.dp
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.credits_developed_by),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextTertiary
                        )
                        Text(
                            text = "Anvorgueso Company",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = stringResource(R.string.credits_powered_by),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextTertiary
                        )
                        Text(
                            text = "pokeapi.co",
                            style = MaterialTheme.typography.bodySmall,
                            color = glass.accent
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 16.dp,
                    glassAlpha = 0.04f,
                    borderAlpha = 0.1f,
                    enableGlow = false
                ) {
                    Text(
                        text = stringResource(R.string.disclaimer),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun DataWarningDialog(
    title: String,
    message: String,
    confirmText: String,
    isDestructive: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val glass = DexiumGlass.colors
    val accentColor = if (isDestructive) ErrorRed else glass.accent

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF0F1C2E))
        ) {
        GlassCard(
            cornerRadius = 24.dp,
            glassAlpha = 0.15f,
            borderAlpha = 0.3f,
            glowColor = accentColor,
            contentPadding = 24.dp,
            enableGlow = true
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        shadow = Shadow(
                            color = accentColor.copy(alpha = 0.5f),
                            offset = Offset.Zero,
                            blurRadius = 12f
                        )
                    ),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.06f),
                            contentColor = TextSecondary
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.dialog_cancel),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accentColor.copy(alpha = 0.25f),
                            contentColor = if (isDestructive) ErrorRed else Color.White
                        )
                    ) {
                        Text(
                            text = confirmText,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        }
        }
    }
}
