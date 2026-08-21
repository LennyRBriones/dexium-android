package com.anvorgueso.dexium.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val ShimmerColors = listOf(
    Color.White.copy(alpha = 0.05f),
    Color.White.copy(alpha = 0.16f),
    Color.White.copy(alpha = 0.05f)
)

private const val SHIMMER_DURATION_MS = 1400

/**
 * A placeholder block with a highlight sweeping across it, for skeleton screens.
 *
 * The animated value is read inside [drawBehind] rather than in composition, so each frame
 * only invalidates the draw phase — a skeleton can hold dozens of these without recomposing
 * every frame.
 */
@Composable
fun ShimmerBlock(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp)
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val progress = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(SHIMMER_DURATION_MS, easing = LinearEasing)
        ),
        label = "shimmerProgress"
    )

    Box(
        modifier = modifier
            .clip(shape)
            .drawBehind {
                // The band is twice the block's width so the sweep reads as a soft glow rather
                // than a hard edge, and it travels from fully off-left to fully off-right.
                val band = size.width * 2f
                val x = -band + progress.value * (size.width + band * 2f)
                drawRect(
                    brush = Brush.linearGradient(
                        colors = ShimmerColors,
                        start = Offset(x, 0f),
                        end = Offset(x + band, size.height)
                    )
                )
            }
    )
}

/** Text-line placeholder: pill-shaped, sized like the line of text it stands in for. */
@Composable
fun ShimmerLine(
    width: Dp,
    height: Dp = 14.dp,
    modifier: Modifier = Modifier
) {
    ShimmerBlock(
        modifier = modifier.width(width).height(height),
        shape = RoundedCornerShape(height / 2)
    )
}

/** Full-width text-line placeholder, for paragraphs. */
@Composable
fun ShimmerLineFill(
    height: Dp = 14.dp,
    modifier: Modifier = Modifier
) {
    ShimmerBlock(
        modifier = modifier.height(height),
        shape = RoundedCornerShape(height / 2)
    )
}

@Composable
fun ShimmerCircle(
    diameter: Dp,
    modifier: Modifier = Modifier
) {
    ShimmerBlock(
        modifier = modifier.size(diameter),
        shape = CircleShape
    )
}
