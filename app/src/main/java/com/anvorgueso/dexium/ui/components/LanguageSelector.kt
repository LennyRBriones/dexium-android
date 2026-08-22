package com.anvorgueso.dexium.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.anvorgueso.dexium.core.util.AppLanguage
import com.anvorgueso.dexium.ui.theme.DexiumGlass

/**
 * Language picker for settings, built on the same pill-plus-popup shape as [GameSelector].
 *
 * [selected] is what the app is actually using, and [isFollowingDevice] says whether that came
 * from the device or from an explicit choice — the "follow device" row stays selectable so a
 * user can hand the decision back.
 */
@Composable
fun LanguageSelector(
    selected: AppLanguage,
    isFollowingDevice: Boolean,
    onLanguageSelected: (AppLanguage?) -> Unit,
    followDeviceLabel: String,
    modifier: Modifier = Modifier
) {
    val glass = DexiumGlass.colors
    var expanded by remember { mutableStateOf(false) }
    val accent = glass.accent

    Box(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            accent.copy(alpha = 0.2f),
                            accent.copy(alpha = 0.1f)
                        )
                    )
                )
                .border(0.5.dp, accent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .clickable { expanded = !expanded }
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(accent)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = selected.label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = accent,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.widthIn(max = 170.dp)
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(16.dp)
            )
        }

        if (expanded) {
            Popup(
                alignment = Alignment.TopEnd,
                onDismissRequest = { expanded = false },
                properties = PopupProperties(focusable = true)
            ) {
                AnimatedVisibility(
                    visible = expanded,
                    enter = fadeIn(tween(200)) + expandVertically(tween(200)),
                    exit = fadeOut(tween(150)) + shrinkVertically(tween(150))
                ) {
                    val dropdownShape = RoundedCornerShape(16.dp)

                    Box(
                        modifier = Modifier
                            .padding(top = 36.dp)
                            .clip(dropdownShape)
                            .background(Color(0xFF0F1C2E))
                    ) {
                        GlassCard(
                            cornerRadius = 16.dp,
                            glassAlpha = 0.1f,
                            borderAlpha = 0.2f,
                            glowColor = accent,
                            contentPadding = 6.dp,
                            enableGlow = true
                        ) {
                            Column(
                                modifier = Modifier
                                    .widthIn(min = 210.dp, max = 280.dp)
                                    .heightIn(max = 380.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                LanguageRow(
                                    label = followDeviceLabel,
                                    isSelected = isFollowingDevice,
                                    accent = accent,
                                    onClick = {
                                        onLanguageSelected(null)
                                        expanded = false
                                    }
                                )

                                AppLanguage.entries.forEach { language ->
                                    LanguageRow(
                                        label = language.label,
                                        isSelected = !isFollowingDevice && language == selected,
                                        accent = accent,
                                        onClick = {
                                            onLanguageSelected(language)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguageRow(
    label: String,
    isSelected: Boolean,
    accent: Color,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) accent.copy(alpha = 0.12f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 11.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(accent)
                .drawWithContent {
                    drawContent()
                    if (isSelected) {
                        drawCircle(
                            color = accent.copy(alpha = 0.4f),
                            radius = size.width
                        )
                    }
                }
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                shadow = if (isSelected) Shadow(
                    color = accent.copy(alpha = 0.4f),
                    offset = Offset.Zero,
                    blurRadius = 8f
                ) else null
            ),
            color = if (isSelected) accent else Color.White.copy(alpha = 0.8f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
