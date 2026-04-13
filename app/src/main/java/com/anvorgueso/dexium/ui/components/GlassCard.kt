package com.anvorgueso.dexium.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anvorgueso.dexium.ui.theme.DexiumGlass

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    glassAlpha: Float = 0.08f,
    borderAlpha: Float = 0.2f,
    glowColor: Color? = null,
    contentPadding: Dp = 16.dp,
    enableGlow: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val glass = DexiumGlass.colors
    val effectiveGlow = glowColor ?: glass.accent

    Box(modifier = modifier) {
        if (enableGlow) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        shadowElevation = 12f
                        this.shape = shape
                        clip = false
                        ambientShadowColor = effectiveGlow.copy(alpha = 0.15f)
                        spotShadowColor = effectiveGlow.copy(alpha = 0.2f)
                    }
            )
        }

        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(shape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = glassAlpha * 1.5f),
                            Color.White.copy(alpha = glassAlpha * 0.3f),
                            Color.White.copy(alpha = glassAlpha * 0.6f)
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
                .drawWithContent {
                    drawContent()
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.12f),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = size.height * 0.35f
                        )
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.08f),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.2f, size.height * 0.15f),
                            radius = size.width * 0.5f
                        )
                    )
                }
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = borderAlpha * 1.5f),
                            Color.White.copy(alpha = borderAlpha * 0.3f)
                        )
                    ),
                    shape = shape
                )
        )

        Box(
            modifier = Modifier
                .padding(contentPadding),
            content = content
        )
    }
}
