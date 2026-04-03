package com.anvorgueso.dexium.core.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GenerationDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "main_region") val mainRegion: NamedApiResource,
    @Json(name = "pokemon_species") val pokemonSpecies: List<NamedApiResource>,
    @Json(name = "names") val names: List<GenerationNameDto>
)

@JsonClass(generateAdapter = true)
data class GenerationNameDto(
    @Json(name = "name") val name: String,
    @Json(name = "language") val language: NamedApiResource
)
