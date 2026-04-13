package com.anvorgueso.dexium.domain.model

data class PokemonDetail(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val animatedImageUrl: String?,
    val shinySpriteUrl: String?,
    val shinyAnimatedSpriteUrl: String?,
    val height: Int,
    val weight: Int,
    val baseExperience: Int,
    val stats: List<Stat>,
    val types: List<String>,
    val abilities: List<Ability>,
    val description: String,
    val genus: String,
    val color: String,
    val habitat: String?,
    val isLegendary: Boolean,
    val isMythical: Boolean,
    val evolutionChain: List<EvolutionStage>
)
