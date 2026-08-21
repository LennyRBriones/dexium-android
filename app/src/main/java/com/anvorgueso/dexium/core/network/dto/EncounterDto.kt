package com.anvorgueso.dexium.core.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LocationAreaEncounterDto(
    @Json(name = "location_area") val locationArea: NamedApiResource,
    @Json(name = "version_details") val versionDetails: List<EncounterVersionDetailDto>
)

@JsonClass(generateAdapter = true)
data class EncounterVersionDetailDto(
    @Json(name = "version") val version: NamedApiResource,
    @Json(name = "max_chance") val maxChance: Int,
    @Json(name = "encounter_details") val encounterDetails: List<EncounterDetailDto>
)

@JsonClass(generateAdapter = true)
data class EncounterDetailDto(
    @Json(name = "chance") val chance: Int,
    @Json(name = "min_level") val minLevel: Int,
    @Json(name = "max_level") val maxLevel: Int,
    @Json(name = "method") val method: NamedApiResource
)
