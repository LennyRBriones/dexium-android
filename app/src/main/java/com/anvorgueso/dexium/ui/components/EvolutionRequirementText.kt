package com.anvorgueso.dexium.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.anvorgueso.dexium.R
import com.anvorgueso.dexium.core.util.LocalizedEvolutionNames
import com.anvorgueso.dexium.core.util.Translations
import com.anvorgueso.dexium.domain.model.EvolutionRequirement

/**
 * The short line under an evolution sprite, in the app's content language.
 *
 * Built here rather than stored, because the same cached chain has to read correctly after the
 * user switches languages. Conditions are joined with a middle dot: two thirds of the chains in
 * PokeAPI need more than a level, and a few need two things at once (Espeon is friendship *and*
 * daytime), so a single value would drop information.
 *
 * Returns null when there is nothing to say, which is the first stage of every chain.
 */
@Composable
fun evolutionRequirementLabel(
    requirement: EvolutionRequirement?,
    language: String
): String? {
    if (requirement == null || requirement.isEmpty) return null

    val parts = mutableListOf<String>()

    requirement.item?.let { parts += LocalizedEvolutionNames.item(it, language) }
    requirement.minLevel?.let { parts += stringResource(R.string.evo_level, it) }
    requirement.minHappiness?.let { parts += stringResource(R.string.evo_friendship) }
    requirement.minBeauty?.let { parts += stringResource(R.string.evo_beauty) }
    requirement.minAffection?.let { parts += stringResource(R.string.evo_affection) }
    requirement.knownMove?.let {
        parts += stringResource(R.string.evo_knows_move, LocalizedEvolutionNames.move(it, language))
    }
    requirement.knownMoveType?.let {
        parts += stringResource(R.string.evo_knows_move_type, Translations.translateType(it, language))
    }
    requirement.tradeSpecies?.let {
        parts += stringResource(
            R.string.evo_trade_for,
            LocalizedEvolutionNames.species(it, language)
        )
    }
    if (requirement.trigger == "trade" && requirement.tradeSpecies == null) {
        parts += stringResource(R.string.evo_trade)
    }
    requirement.heldItem?.let {
        parts += stringResource(R.string.evo_holding, LocalizedEvolutionNames.item(it, language))
    }
    requirement.location?.let { parts += LocalizedEvolutionNames.location(it, language) }
    requirement.partySpecies?.let {
        parts += stringResource(
            R.string.evo_with_party,
            LocalizedEvolutionNames.species(it, language)
        )
    }
    requirement.partyType?.let {
        parts += stringResource(R.string.evo_with_party, Translations.translateType(it, language))
    }
    requirement.gender?.let {
        parts += stringResource(
            if (it == 1) R.string.evo_female else R.string.evo_male
        )
    }
    requirement.relativePhysicalStats?.let {
        parts += stringResource(
            when {
                it > 0 -> R.string.evo_atk_gt_def
                it < 0 -> R.string.evo_atk_lt_def
                else -> R.string.evo_atk_eq_def
            }
        )
    }
    if (requirement.needsOverworldRain) parts += stringResource(R.string.evo_rain)
    if (requirement.turnUpsideDown) parts += stringResource(R.string.evo_upside_down)
    requirement.timeOfDay?.let { time ->
        val res = when (time) {
            "day" -> R.string.evo_time_day
            "night" -> R.string.evo_time_night
            "dusk" -> R.string.evo_time_dusk
            "full-moon" -> R.string.evo_time_full_moon
            else -> null
        }
        parts += res?.let { stringResource(it) } ?: time
    }

    // Triggers like `spin` or `three-critical-hits` carry the whole condition in their name, so
    // they are only worth printing when nothing more specific was found.
    if (parts.isEmpty()) triggerLabel(requirement.trigger)?.let { parts += it }

    return parts.takeIf { it.isNotEmpty() }?.joinToString(" · ")
}

@Composable
private fun triggerLabel(trigger: String?): String? = when (trigger) {
    null -> null
    "level-up" -> stringResource(R.string.evo_level_up)
    "trade" -> stringResource(R.string.evo_trade)
    "use-item" -> stringResource(R.string.evo_use_item)
    "shed" -> stringResource(R.string.evo_shed)
    "spin" -> stringResource(R.string.evo_spin)
    "tower-of-darkness" -> stringResource(R.string.evo_tower_of_darkness)
    "tower-of-waters" -> stringResource(R.string.evo_tower_of_waters)
    "three-critical-hits" -> stringResource(R.string.evo_three_critical_hits)
    "take-damage" -> stringResource(R.string.evo_take_damage)
    "recoil-damage" -> stringResource(R.string.evo_recoil_damage)
    "agile-style-move" -> stringResource(R.string.evo_agile_style)
    "strong-style-move" -> stringResource(R.string.evo_strong_style)
    "three-defeated-bisharp" -> stringResource(R.string.evo_defeat_bisharp)
    "gimmighoul-coins" -> stringResource(R.string.evo_gimmighoul_coins)
    "use-move" -> stringResource(R.string.evo_use_move)
    "other" -> stringResource(R.string.evo_other)
    else -> stringResource(R.string.evo_other)
}
