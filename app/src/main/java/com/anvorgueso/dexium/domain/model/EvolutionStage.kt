package com.anvorgueso.dexium.domain.model

data class EvolutionStage(
    val pokemonId: Int,
    val pokemonName: String,
    val imageUrl: String,
    /** Shiny artwork for the same stage, so the Shiny Dex keeps the whole line consistent. */
    val shinyImageUrl: String,
    val isBaby: Boolean,
    val minLevel: Int?,
    val trigger: String?
)
