package com.anvorgueso.dexium.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.anvorgueso.dexium.core.util.Translations
import com.anvorgueso.dexium.domain.model.TeamTypeCoverage
import com.anvorgueso.dexium.ui.theme.PokemonTypeColors
import com.anvorgueso.dexium.ui.theme.TextSecondary
import com.anvorgueso.dexium.ui.theme.TextTertiary

/** "4x", "2x", "½x", "¼x", "0x" — reads better than a raw float in a dense chip. */
fun formatMultiplier(value: Float): String = when (value) {
    0f -> "0x"
    0.25f -> "¼x"
    0.5f -> "½x"
    1f -> "1x"
    2f -> "2x"
    4f -> "4x"
    else -> "${value.toString().trimEnd('0').trimEnd('.')}x"
}

/**
 * Compact type chip carrying a multiplier — the unit of both the single-Pokémon table and the
 * team coverage list. Localizes the type label so it matches the rest of the screen.
 */
@Composable
fun TypeMultiplierChip(
    type: String,
    trailing: String?,
    locale: String,
    modifier: Modifier = Modifier
) {
    val color = PokemonTypeColors.getColor(type)
    val shape = RoundedCornerShape(8.dp)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(shape)
            .background(color.copy(alpha = 0.22f), shape)
            .border(1.dp, color.copy(alpha = 0.5f), shape)
            .padding(start = 4.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
    ) {
        TypeSymbol(type = type, iconSize = 16.dp)
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = Translations.translateType(Translations.canonicalType(type), locale),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = Color.White
        )
        if (trailing != null) {
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = trailing,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = color
            )
        }
    }
}

/** A labelled group of chips, hidden entirely when there is nothing to show. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TypeEffectivenessGroup(
    label: String,
    entries: List<Pair<String, String>>,
    locale: String,
    modifier: Modifier = Modifier
) {
    if (entries.isEmpty()) return

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextTertiary
        )
        Spacer(modifier = Modifier.height(6.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            entries.forEach { (type, trailing) ->
                TypeMultiplierChip(type = type, trailing = trailing, locale = locale)
            }
        }
    }
}

/**
 * One block per attacking type the team is weak to. Shows the sprites of the members that
 * actually take the hit — the count alone never said *who*, and it flattened 4x into the same
 * "weak" bucket as 2x. Neutral types are omitted: they carry no decision value.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TeamCoverageList(
    coverage: List<TeamTypeCoverage>,
    locale: String,
    noAnswerLabel: String,
    resistLabel: @Composable (Int) -> String,
    modifier: Modifier = Modifier
) {
    val threats = coverage.filter { it.weakCount > 0 }
    if (threats.isEmpty()) return

    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        threats.forEach { entry ->
            val safeCount = entry.resistCount + entry.immuneCount

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TypeMultiplierChip(
                        type = entry.type,
                        trailing = null,
                        locale = locale
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (entry.hasNoAnswer) noAnswerLabel else resistLabel(safeCount),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (entry.hasNoAnswer) Color(0xFFFF8A80) else TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    entry.weakMembers.forEach { weak ->
                        WeakMemberBadge(
                            name = weak.pokemon.name,
                            imageUrl = weak.pokemon.imageUrl,
                            multiplier = weak.multiplier
                        )
                    }
                }
            }
        }
    }
}

/** Sprite plus the damage it takes. 4x is called out in red — that is the real problem case. */
@Composable
private fun WeakMemberBadge(
    name: String,
    imageUrl: String,
    multiplier: Float
) {
    val critical = multiplier >= 4f
    val accent = if (critical) Color(0xFFFF5252) else Color(0xFFFFB74D)
    val shape = RoundedCornerShape(10.dp)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(shape)
            .background(accent.copy(alpha = if (critical) 0.20f else 0.12f), shape)
            .border(1.dp, accent.copy(alpha = if (critical) 0.6f else 0.35f), shape)
            .padding(start = 2.dp, end = 8.dp, top = 2.dp, bottom = 2.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = name,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(30.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = Color.White
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = formatMultiplier(multiplier),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            ),
            color = accent
        )
    }
}
