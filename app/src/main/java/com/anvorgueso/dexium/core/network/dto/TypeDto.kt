package com.anvorgueso.dexium.core.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TypeDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String
)
