package com.anvorgueso.dexium.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.anvorgueso.dexium.core.util.formatPokemonId
import com.anvorgueso.dexium.domain.model.Pokemon
import com.anvorgueso.dexium.ui.theme.PokemonTypeColors
import com.anvorgueso.dexium.ui.theme.TextTertiary

@Composable
fun PokemonCard(
    pokemon: Pokemon,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val typeColor = PokemonTypeColors.getColor(pokemon.typePrimary)

    var useAnimated by remember(pokemon.id) { mutableStateOf(pokemon.animatedImageUrl != null) }
    val displayUrl = if (useAnimated && pokemon.animatedImageUrl != null) {
        pokemon.animatedImageUrl
    } else {
        pokemon.imageUrl
    }

    val density = LocalDensity.current
    val sizePx = with(density) { 60.dp.roundToPx() }

    GlassCard(
        modifier = modifier.clickable(onClick = onClick),
        cornerRadius = 14.dp,
        glowColor = typeColor,
        borderAlpha = 0.18f,
        glassAlpha = 0.06f,
        contentPadding = 6.dp,
        enableGlow = true
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = pokemon.id.formatPokemonId(),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = TextTertiary,
                modifier = Modifier.align(Alignment.End)
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    typeColor.copy(alpha = 0.2f),
                                    Color.Transparent
                                ),
                                center = Offset(size.width / 2, size.height / 2),
                                radius = size.width * 0.4f
                            )
                        )
                    }
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(displayUrl)
                        .crossfade(true)
                        .size(sizePx, sizePx)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .build(),
                    contentDescription = pokemon.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(60.dp),
                    onError = {
                        if (useAnimated) useAnimated = false
                    }
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = pokemon.name,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                ),
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

        }
    }
}
