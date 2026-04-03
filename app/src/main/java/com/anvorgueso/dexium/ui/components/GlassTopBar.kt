package com.anvorgueso.dexium.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anvorgueso.dexium.ui.theme.DexiumGlass

@Composable
fun GlassTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    centerContent: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val glass = DexiumGlass.colors

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        glass.backgroundStart,
                        glass.backgroundStart.copy(alpha = 0.95f),
                        Color.White.copy(alpha = 0.03f)
                    )
                )
            )
            .drawWithContent {
                drawContent()
                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            glass.accent.copy(alpha = 0.3f),
                            glass.accent.copy(alpha = 0.5f),
                            glass.accent.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    ),
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.5f
                )
            }
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = glass.accent
                    )
                }
            }

            if (onBackClick == null) {
                NeonTitle(
                    text = title,
                    modifier = Modifier.padding(start = 16.dp)
                )
            } else {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        shadow = Shadow(
                            color = glass.accent.copy(alpha = 0.5f),
                            offset = Offset.Zero,
                            blurRadius = 12f
                        )
                    ),
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = if (onBackClick != null) 0.dp else 16.dp)
                )
            }

            if (centerContent != null) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    centerContent()
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            Row(
                horizontalArrangement = Arrangement.End,
                content = actions
            )
        }
    }
}

@Composable
private fun NeonTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    val neonColors = listOf(
        Color(0xFF4FC3F7),
        Color(0xFF81D4FA),
        Color(0xFFB388FF),
        Color(0xFF80CBC4),
        Color(0xFF4FC3F7)
    )

    Box(modifier = modifier) {

        Text(
            text = text,
            style = TextStyle(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 26.sp,
                letterSpacing = 3.sp,
                shadow = Shadow(
                    color = Color(0xFF4FC3F7).copy(alpha = 0.7f),
                    offset = Offset.Zero,
                    blurRadius = 30f
                )
            ),
            color = Color.Transparent
        )
        Text(
            text = text,
            style = TextStyle(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 26.sp,
                letterSpacing = 3.sp,
                shadow = Shadow(
                    color = Color(0xFFB388FF).copy(alpha = 0.5f),
                    offset = Offset.Zero,
                    blurRadius = 16f
                )
            ),
            color = Color.Transparent
        )
        Text(
            text = text,
            style = TextStyle(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 26.sp,
                letterSpacing = 3.sp,
                shadow = Shadow(
                    color = Color.White.copy(alpha = 0.4f),
                    offset = Offset.Zero,
                    blurRadius = 6f
                ),
                brush = Brush.horizontalGradient(neonColors)
            )
        )
    }
}
