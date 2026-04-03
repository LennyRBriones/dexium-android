package com.anvorgueso.dexium.core.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegionDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "names") val names: List<RegionNameDto>,
    @Json(name = "main_generation") val mainGeneration: NamedApiResource?
)

@JsonClass(generateAdapter = true)
data class RegionNameDto(
    @Json(name = "name") val name: String,
    @Json(name = "language") val language: NamedApiResource
)
