package com.anvorgueso.dexium.core.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AbilityDetailDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "names") val names: List<AbilityNameDto>
)

@JsonClass(generateAdapter = true)
data class AbilityNameDto(
    @Json(name = "name") val name: String,
    @Json(name = "language") val language: NamedApiResource
)
