package com.anvorgueso.dexium.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.dp
import kotlin.math.sin

/**
 * Presents [content] as a hologram projection: a glass plate at the bottom with a beam of
 * light rising out of it and fading toward the top, drifting scanlines inside the beam, and a
 * slow pulse on the plate.
 *
 * Everything is drawn in the draw phase reading animated state, so the projection animates
 * without recomposing the content it wraps.
 */
@Composable
fun HologramStage(
    accentColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val transition = rememberInfiniteTransition(label = "hologram")
    val drift = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(3200, easing = LinearEasing)),
        label = "scanlineDrift"
    )
    val pulse = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(2600, easing = LinearEasing)),
        label = "platePulse"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.drawBehind {
            val w = size.width
            val h = size.height
            val cx = w / 2f

            // The plate sits low so the subject reads as floating above it.
            val plateY = h * 0.88f
            val plateRx = w * 0.30f
            val plateRy = plateRx * 0.17f

            val beamTopY = h * 0.10f
            val beamBottomHalf = plateRx * 0.92f
            val beamTopHalf = plateRx * 1.22f

            // Gentle breathing so the projection never looks like a static image.
            val breath = 0.88f + 0.12f * sin(pulse.value * 2f * Math.PI.toFloat())

            val beam = Path().apply {
                moveTo(cx - beamBottomHalf, plateY)
                lineTo(cx + beamBottomHalf, plateY)
                lineTo(cx + beamTopHalf, beamTopY)
                lineTo(cx - beamTopHalf, beamTopY)
                close()
            }

            // Beam body: brightest where it leaves the plate, gone by the top.
            drawPath(
                path = beam,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        accentColor.copy(alpha = 0.10f * breath),
                        accentColor.copy(alpha = 0.26f * breath)
                    ),
                    startY = beamTopY,
                    endY = plateY
                )
            )
            // Frosted-glass wash over the beam, the same white-over-tint as GlassCard.
            drawPath(
                path = beam,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = 0.05f),
                        Color.White.copy(alpha = 0.13f)
                    ),
                    startY = beamTopY,
                    endY = plateY
                )
            )

            // Scanlines drifting upward inside the beam, fading out as they rise.
            val spacing = 9.dp.toPx()
            clipPath(beam) {
                val span = plateY - beamTopY
                var offset = drift.value * spacing
                while (offset < span) {
                    val y = plateY - offset
                    val t = ((plateY - y) / span).coerceIn(0f, 1f)
                    drawLine(
                        color = Color.White.copy(alpha = 0.16f * (1f - t) * breath),
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 1f
                    )
                    offset += spacing
                }
            }

            // Halo bleeding out of the plate.
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.32f * breath),
                        Color.Transparent
                    ),
                    center = Offset(cx, plateY),
                    radius = plateRx * 1.7f
                ),
                topLeft = Offset(cx - plateRx * 1.7f, plateY - plateRy * 3.4f),
                size = Size(plateRx * 3.4f, plateRy * 6.8f)
            )

            // The glass plate itself: a lit pane seen almost edge-on.
            drawOval(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.26f),
                        accentColor.copy(alpha = 0.20f),
                        Color.White.copy(alpha = 0.06f)
                    ),
                    start = Offset(cx - plateRx, plateY - plateRy),
                    end = Offset(cx + plateRx, plateY + plateRy)
                ),
                topLeft = Offset(cx - plateRx, plateY - plateRy),
                size = Size(plateRx * 2f, plateRy * 2f)
            )
            drawOval(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.65f),
                        accentColor.copy(alpha = 0.50f),
                        Color.White.copy(alpha = 0.18f)
                    ),
                    start = Offset(cx - plateRx, plateY - plateRy),
                    end = Offset(cx + plateRx, plateY + plateRy)
                ),
                topLeft = Offset(cx - plateRx, plateY - plateRy),
                size = Size(plateRx * 2f, plateRy * 2f),
                style = Stroke(width = 1.5.dp.toPx())
            )
            // Bright core right at the projection point.
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.34f * breath),
                        Color.Transparent
                    ),
                    center = Offset(cx, plateY),
                    radius = plateRx * 0.7f
                ),
                topLeft = Offset(cx - plateRx * 0.7f, plateY - plateRy * 1.1f),
                size = Size(plateRx * 1.4f, plateRy * 2.2f)
            )
        },
        content = content
    )
}
