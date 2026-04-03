package com.anvorgueso.dexium.core.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PokemonDetailDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "height") val height: Int,
    @Json(name = "weight") val weight: Int,
    @Json(name = "base_experience") val baseExperience: Int?,
    @Json(name = "sprites") val sprites: SpritesDto,
    @Json(name = "stats") val stats: List<StatSlotDto>,
    @Json(name = "types") val types: List<TypeSlotDto>,
    @Json(name = "abilities") val abilities: List<AbilitySlotDto>,
    @Json(name = "species") val species: NamedApiResource
)

@JsonClass(generateAdapter = true)
data class SpritesDto(
    @Json(name = "front_default") val frontDefault: String?,
    @Json(name = "front_shiny") val frontShiny: String?,
    @Json(name = "other") val other: OtherSpritesDto?
)

@JsonClass(generateAdapter = true)
data class OtherSpritesDto(
    @Json(name = "official-artwork") val officialArtwork: OfficialArtworkDto?,
    @Json(name = "home") val home: HomeSpritesDto?,
    @Json(name = "dream_world") val dreamWorld: DreamWorldSpritesDto?,
    @Json(name = "showdown") val showdown: ShowdownSpritesDto?
)

@JsonClass(generateAdapter = true)
data class OfficialArtworkDto(
    @Json(name = "front_default") val frontDefault: String?,
    @Json(name = "front_shiny") val frontShiny: String?
)

@JsonClass(generateAdapter = true)
data class HomeSpritesDto(
    @Json(name = "front_default") val frontDefault: String?,
    @Json(name = "front_shiny") val frontShiny: String?
)

@JsonClass(generateAdapter = true)
data class DreamWorldSpritesDto(
    @Json(name = "front_default") val frontDefault: String?
)

@JsonClass(generateAdapter = true)
data class ShowdownSpritesDto(
    @Json(name = "front_default") val frontDefault: String?,
    @Json(name = "front_shiny") val frontShiny: String?
)

@JsonClass(generateAdapter = true)
data class StatSlotDto(
    @Json(name = "base_stat") val baseStat: Int,
    @Json(name = "effort") val effort: Int,
    @Json(name = "stat") val stat: NamedApiResource
)

@JsonClass(generateAdapter = true)
data class TypeSlotDto(
    @Json(name = "slot") val slot: Int,
    @Json(name = "type") val type: NamedApiResource
)

@JsonClass(generateAdapter = true)
data class AbilitySlotDto(
    @Json(name = "ability") val ability: NamedApiResource,
    @Json(name = "is_hidden") val isHidden: Boolean,
    @Json(name = "slot") val slot: Int
)
