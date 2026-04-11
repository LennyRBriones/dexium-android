package com.anvorgueso.dexium.core.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PokemonSpeciesDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "color") val color: NamedApiResource,
    @Json(name = "habitat") val habitat: NamedApiResource?,
    @Json(name = "generation") val generation: NamedApiResource,
    @Json(name = "evolution_chain") val evolutionChain: EvolutionChainUrlDto?,
    @Json(name = "flavor_text_entries") val flavorTextEntries: List<FlavorTextEntryDto>,
    @Json(name = "genera") val genera: List<GenusDto>,
    @Json(name = "is_legendary") val isLegendary: Boolean,
    @Json(name = "is_mythical") val isMythical: Boolean,
    @Json(name = "gender_rate") val genderRate: Int,
    @Json(name = "capture_rate") val captureRate: Int,
    @Json(name = "base_happiness") val baseHappiness: Int?,
    @Json(name = "growth_rate") val growthRate: NamedApiResource,
    @Json(name = "egg_groups") val eggGroups: List<NamedApiResource>
)

@JsonClass(generateAdapter = true)
data class EvolutionChainUrlDto(
    @Json(name = "url") val url: String
)

@JsonClass(generateAdapter = true)
data class FlavorTextEntryDto(
    @Json(name = "flavor_text") val flavorText: String,
    @Json(name = "language") val language: NamedApiResource,
    @Json(name = "version") val version: NamedApiResource
)

@JsonClass(generateAdapter = true)
data class GenusDto(
    @Json(name = "genus") val genus: String,
    @Json(name = "language") val language: NamedApiResource
)
