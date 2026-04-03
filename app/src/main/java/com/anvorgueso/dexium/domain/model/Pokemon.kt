package com.anvorgueso.dexium.domain.model

data class Pokemon(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val animatedImageUrl: String?,
    val typePrimary: String,
    val typeSecondary: String?
)
