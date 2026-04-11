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

@JsonClass(generateAdapter = true)
data class EvolutionDetailDto(
    @Json(name = "min_level") val minLevel: Int?,
    @Json(name = "trigger") val trigger: NamedApiResource?,
    @Json(name = "item") val item: NamedApiResource?
)
