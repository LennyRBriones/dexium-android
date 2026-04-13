package com.anvorgueso.dexium.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anvorgueso.dexium.ui.theme.DexiumGlass

@Composable
fun DexiumFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glass = DexiumGlass.colors
    val shape = RoundedCornerShape(20.dp)

    val backgroundColor = if (isSelected) {
        Brush.horizontalGradient(
            colors = listOf(
                glass.accent.copy(alpha = 0.35f),
                glass.accent.copy(alpha = 0.2f)
            )
        )
    } else {
        Brush.horizontalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.06f),
                Color.White.copy(alpha = 0.03f)
            )
        )
    }

    val borderColor = if (isSelected) glass.accent.copy(alpha = 0.6f) else glass.border
    val textColor = if (isSelected) glass.accent else Color.White.copy(alpha = 0.7f)

    Text(
        text = label,
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 12.sp
        ),
        color = textColor,
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .border(0.5.dp, borderColor, shape)
            .clickable(onClick = onClick)
            .drawWithContent {
                drawContent()
                if (isSelected) {
                    drawLine(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                glass.accent.copy(alpha = 0.25f),
                                Color.Transparent
                            )
                        ),
                        start = Offset(size.width * 0.15f, size.height - 1f),
                        end = Offset(size.width * 0.85f, size.height - 1f),
                        strokeWidth = 1.5f
                    )
                }
            }
            .padding(horizontal = 14.dp, vertical = 7.dp)
    )
}
