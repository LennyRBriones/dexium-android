package com.anvorgueso.dexium.domain.model

data class Generation(
    val id: Int,
    val name: String,
    val displayName: String,
    val regionName: String,
    val pokemonCount: Int
)
