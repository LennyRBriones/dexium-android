package com.anvorgueso.dexium.core.util

import com.anvorgueso.dexium.domain.model.Pokemon
import com.anvorgueso.dexium.domain.model.TeamTypeCoverage
import com.anvorgueso.dexium.domain.model.WeakMember

/**
 * Defensive type effectiveness. Generated from PokeAPI's `damage_relations` and verified
 * against the game's eight immunities (Normal/Ghost both ways, Ghost-Fighting, Flying-Ground,
 * Ground-Electric, Steel-Poison, Dark-Psychic, Fairy-Dragon).
 *
 * Hardcoded rather than fetched: the chart is static game data, so this keeps the type table
 * and team analysis working offline and costs no requests.
 *
 * Keys are canonical capitalized English names. Call [Translations.canonicalType] first when
 * the input may be localized — the detail screen stores translated type names.
 */
object TypeChart {

    val ALL_TYPES = listOf("Normal", "Fire", "Water", "Electric", "Grass", "Ice", "Fighting", "Poison", "Ground", "Flying", "Psychic", "Bug", "Rock", "Ghost", "Dragon", "Dark", "Steel", "Fairy")

    /** Defending type -> (attacking type -> multiplier). Absent entries are neutral (1x). */
    private val relations: Map<String, Map<String, Float>> = mapOf(
        "Normal" to mapOf(
            "Ghost" to 0f,
            "Fighting" to 2f
        ),
        "Fire" to mapOf(
            "Bug" to 0.5f,
            "Fairy" to 0.5f,
            "Fire" to 0.5f,
            "Grass" to 0.5f,
            "Ice" to 0.5f,
            "Steel" to 0.5f,
            "Ground" to 2f,
            "Rock" to 2f,
            "Water" to 2f
        ),
        "Water" to mapOf(
            "Fire" to 0.5f,
            "Ice" to 0.5f,
            "Steel" to 0.5f,
            "Water" to 0.5f,
            "Electric" to 2f,
            "Grass" to 2f
        ),
        "Electric" to mapOf(
            "Electric" to 0.5f,
            "Flying" to 0.5f,
            "Steel" to 0.5f,
            "Ground" to 2f
        ),
        "Grass" to mapOf(
            "Electric" to 0.5f,
            "Grass" to 0.5f,
            "Ground" to 0.5f,
            "Water" to 0.5f,
            "Bug" to 2f,
            "Fire" to 2f,
            "Flying" to 2f,
            "Ice" to 2f,
            "Poison" to 2f
        ),
        "Ice" to mapOf(
            "Ice" to 0.5f,
            "Fighting" to 2f,
            "Fire" to 2f,
            "Rock" to 2f,
            "Steel" to 2f
        ),
        "Fighting" to mapOf(
            "Bug" to 0.5f,
            "Dark" to 0.5f,
            "Rock" to 0.5f,
            "Fairy" to 2f,
            "Flying" to 2f,
            "Psychic" to 2f
        ),
        "Poison" to mapOf(
            "Bug" to 0.5f,
            "Fairy" to 0.5f,
            "Fighting" to 0.5f,
            "Grass" to 0.5f,
            "Poison" to 0.5f,
            "Ground" to 2f,
            "Psychic" to 2f
        ),
        "Ground" to mapOf(
            "Electric" to 0f,
            "Poison" to 0.5f,
            "Rock" to 0.5f,
            "Grass" to 2f,
            "Ice" to 2f,
            "Water" to 2f
        ),
        "Flying" to mapOf(
            "Ground" to 0f,
            "Bug" to 0.5f,
            "Fighting" to 0.5f,
            "Grass" to 0.5f,
            "Electric" to 2f,
            "Ice" to 2f,
            "Rock" to 2f
        ),
        "Psychic" to mapOf(
            "Fighting" to 0.5f,
            "Psychic" to 0.5f,
            "Bug" to 2f,
            "Dark" to 2f,
            "Ghost" to 2f
        ),
        "Bug" to mapOf(
            "Fighting" to 0.5f,
            "Grass" to 0.5f,
            "Ground" to 0.5f,
            "Fire" to 2f,
            "Flying" to 2f,
            "Rock" to 2f
        ),
        "Rock" to mapOf(
            "Fire" to 0.5f,
            "Flying" to 0.5f,
            "Normal" to 0.5f,
            "Poison" to 0.5f,
            "Fighting" to 2f,
            "Grass" to 2f,
            "Ground" to 2f,
            "Steel" to 2f,
            "Water" to 2f
        ),
        "Ghost" to mapOf(
            "Fighting" to 0f,
            "Normal" to 0f,
            "Bug" to 0.5f,
            "Poison" to 0.5f,
            "Dark" to 2f,
            "Ghost" to 2f
        ),
        "Dragon" to mapOf(
            "Electric" to 0.5f,
            "Fire" to 0.5f,
            "Grass" to 0.5f,
            "Water" to 0.5f,
            "Dragon" to 2f,
            "Fairy" to 2f,
            "Ice" to 2f
        ),
        "Dark" to mapOf(
            "Psychic" to 0f,
            "Dark" to 0.5f,
            "Ghost" to 0.5f,
            "Bug" to 2f,
            "Fairy" to 2f,
            "Fighting" to 2f
        ),
        "Steel" to mapOf(
            "Poison" to 0f,
            "Bug" to 0.5f,
            "Dragon" to 0.5f,
            "Fairy" to 0.5f,
            "Flying" to 0.5f,
            "Grass" to 0.5f,
            "Ice" to 0.5f,
            "Normal" to 0.5f,
            "Psychic" to 0.5f,
            "Rock" to 0.5f,
            "Steel" to 0.5f,
            "Fighting" to 2f,
            "Fire" to 2f,
            "Ground" to 2f
        ),
        "Fairy" to mapOf(
            "Dragon" to 0f,
            "Bug" to 0.5f,
            "Dark" to 0.5f,
            "Fighting" to 0.5f,
            "Poison" to 2f,
            "Steel" to 2f
        )
    )

    /** Multiplier an [attacking] move deals to a Pokémon whose types are [defending]. */
    fun multiplier(attacking: String, defending: List<String>): Float {
        val atk = Translations.canonicalType(attacking)
        return defending.fold(1f) { acc, type ->
            acc * (relations[Translations.canonicalType(type)]?.get(atk) ?: 1f)
        }
    }

    /** Every attacking type mapped to its multiplier against [defending], neutral included. */
    fun profile(defending: List<String>): Map<String, Float> =
        ALL_TYPES.associateWith { multiplier(it, defending) }

    fun weaknesses(defending: List<String>): Map<String, Float> =
        profile(defending).filterValues { it > 1f }

    fun resistances(defending: List<String>): Map<String, Float> =
        profile(defending).filterValues { it < 1f && it > 0f }

    fun immunities(defending: List<String>): List<String> =
        profile(defending).filterValues { it == 0f }.keys.toList()

    /**
     * Defensive picture for a whole team, worst holes first.
     *
     * Ordering puts the most urgent problems on top: a type that hits someone for 4x outranks
     * one that hits the same number of members for 2x, and having nobody who resists breaks
     * the remaining ties.
     */
    fun teamCoverage(members: List<Pokemon>): List<TeamTypeCoverage> =
        ALL_TYPES.map { attacking ->
            val hits = members.map { member ->
                member to multiplier(attacking, listOfNotNull(member.typePrimary, member.typeSecondary))
            }
            TeamTypeCoverage(
                type = attacking,
                weakMembers = hits
                    .filter { (_, mult) -> mult > 1f }
                    .map { (member, mult) -> WeakMember(member, mult) }
                    .sortedByDescending { it.multiplier },
                resistCount = hits.count { (_, mult) -> mult < 1f && mult > 0f },
                immuneCount = hits.count { (_, mult) -> mult == 0f }
            )
        }.sortedWith(
            compareByDescending<TeamTypeCoverage> { it.weakCount }
                .thenByDescending { it.worstMultiplier }
                .thenBy { it.resistCount + it.immuneCount }
                .thenBy { it.type }
        )
}
