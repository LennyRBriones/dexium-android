package com.anvorgueso.dexium.domain.model

data class EvolutionStage(
    val pokemonId: Int,
    val pokemonName: String,
    val imageUrl: String,
    val isBaby: Boolean,
    val minLevel: Int?,
    val trigger: String?
)
