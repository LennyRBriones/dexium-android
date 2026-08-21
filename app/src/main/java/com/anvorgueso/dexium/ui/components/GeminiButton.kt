package com.anvorgueso.dexium.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Gemini's own mark is Google trademark, so this uses the standard sparkle glyph with a
 * blue-to-violet gradient that reads as "ask the model" without shipping someone's logo.
 * The sparkle turns slowly while a request is in flight.
 */
@Composable
fun GeminiButton(
    label: String,
    enabled: Boolean,
    loading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(14.dp)
    val gradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF4E8CFF),
            Color(0xFF9B72F2),
            Color(0xFFD96BC4)
        )
    )

    val spin = rememberInfiniteTransition(label = "geminiSpin")
    val angle = spin.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(animation = tween(2400, easing = LinearEasing)),
        label = "geminiAngle"
    )

    val alpha = if (enabled && !loading) 1f else 0.55f

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF4E8CFF).copy(alpha = 0.20f * alpha),
                        Color(0xFF9B72F2).copy(alpha = 0.20f * alpha),
                        Color(0xFFD96BC4).copy(alpha = 0.20f * alpha)
                    )
                ),
                shape
            )
            .border(1.dp, gradient, shape)
            .clickable(enabled = enabled && !loading, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = Color(0xFF9B72F2),
            modifier = Modifier
                .size(18.dp)
                .then(if (loading) Modifier.rotate(angle.value) else Modifier)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = Color.White.copy(alpha = alpha)
        )
    }
}
