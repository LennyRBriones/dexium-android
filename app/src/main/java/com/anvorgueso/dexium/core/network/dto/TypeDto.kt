package com.anvorgueso.dexium.core.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TypeDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "pokemon") val pokemon: List<TypePokemonSlotDto>
)

@JsonClass(generateAdapter = true)
data class TypePokemonSlotDto(
    @Json(name = "slot") val slot: Int,
    @Json(name = "pokemon") val pokemon: NamedApiResource
)
