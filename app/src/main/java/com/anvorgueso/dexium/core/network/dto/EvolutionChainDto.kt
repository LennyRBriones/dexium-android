package com.anvorgueso.dexium.core.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EvolutionChainDto(
    @Json(name = "id") val id: Int,
    @Json(name = "chain") val chain: ChainLinkDto
)

@JsonClass(generateAdapter = true)
data class ChainLinkDto(
    @Json(name = "is_baby") val isBaby: Boolean,
    @Json(name = "species") val species: NamedApiResource,
    @Json(name = "evolution_details") val evolutionDetails: List<EvolutionDetailDto>,
    @Json(name = "evolves_to") val evolvesTo: List<ChainLinkDto>
)

/**
 * Every condition PokeAPI reports often enough to be worth showing. Surveyed across all 541
 * evolution chains: only 367 of the 553 conditions carry a `min_level`, so the rest are
 * described entirely by the fields below.
 */
@JsonClass(generateAdapter = true)
data class EvolutionDetailDto(
    @Json(name = "min_level") val minLevel: Int?,
    @Json(name = "trigger") val trigger: NamedApiResource?,
    /**
     * Set when this method produces a form other than the default one, like Sandshrew's Ice
     * Stone leading to Sandslash-Alola. The chain still lists the default species, so a method
     * carrying this does not describe the sprite being shown.
     */
    @Json(name = "evolved_form") val evolvedForm: NamedApiResource?,
    @Json(name = "item") val item: NamedApiResource?,
    @Json(name = "held_item") val heldItem: NamedApiResource?,
    @Json(name = "known_move") val knownMove: NamedApiResource?,
    @Json(name = "known_move_type") val knownMoveType: NamedApiResource?,
    @Json(name = "location") val location: NamedApiResource?,
    @Json(name = "party_species") val partySpecies: NamedApiResource?,
    @Json(name = "party_type") val partyType: NamedApiResource?,
    @Json(name = "trade_species") val tradeSpecies: NamedApiResource?,
    @Json(name = "min_happiness") val minHappiness: Int?,
    @Json(name = "min_affection") val minAffection: Int?,
    @Json(name = "min_beauty") val minBeauty: Int?,
    @Json(name = "time_of_day") val timeOfDay: String?,
    @Json(name = "gender") val gender: Int?,
    @Json(name = "relative_physical_stats") val relativePhysicalStats: Int?,
    @Json(name = "needs_overworld_rain") val needsOverworldRain: Boolean?,
    @Json(name = "turn_upside_down") val turnUpsideDown: Boolean?
)
