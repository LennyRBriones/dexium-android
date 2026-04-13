package com.anvorgueso.dexium.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.anvorgueso.dexium.core.util.capitalizeFirst
import com.anvorgueso.dexium.domain.model.Stat

@Composable
fun StatBar(
    stat: Stat,
    maxStat: Int = 255,
    animDelay: Int = 0,
    modifier: Modifier = Modifier
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed) stat.baseStat.toFloat() / maxStat else 0f,
        animationSpec = tween(durationMillis = 800, delayMillis = animDelay),
        label = "stat_animation"
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    val statColor = getStatColor(stat.name)
    val displayName = formatStatName(stat.name)

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = displayName,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.width(50.dp)
        )

        Text(
            text = stat.baseStat.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            textAlign = TextAlign.End,
            modifier = Modifier.width(36.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White.copy(alpha = 0.1f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .clip(RoundedCornerShape(4.dp))
                    .background(statColor)
            )
        }
    }
}

private fun getStatColor(statName: String): Color {
    return when (statName.lowercase()) {
        "hp" -> Color(0xFFFF5252)
        "attack" -> Color(0xFFFF9800)
        "defense" -> Color(0xFFFFEB3B)
        "special-attack" -> Color(0xFF42A5F5)
        "special-defense" -> Color(0xFF66BB6A)
        "speed" -> Color(0xFFEC407A)
        else -> Color(0xFF78909C)
    }
}

private fun formatStatName(name: String): String {
    return when (name.lowercase()) {
        "hp" -> "HP"
        "attack" -> "ATK"
        "defense" -> "DEF"
        "special-attack" -> "SpA"
        "special-defense" -> "SpD"
        "speed" -> "SPD"
        else -> name.capitalizeFirst()
    }
}
