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
    val evolutionChain: List<EvolutionStage>,
    /**
     * The animated Showdown sprite the home grid uses, but *not* gated on the sprite
     * preference — the detail screen's 3D toggle needs it available on demand.
     */
    val animated3dUrl: String?,
    val shinyAnimated3dUrl: String?,
    /**
     * Pokémon HOME render, used as the hologram's still frame when the animated sprite is
     * missing. PokeAPI reports no Showdown sprite for the newest gen 9 entries, so without this
     * the toggle switches itself off for them.
     */
    val still3dUrl: String?,
    val shinyStill3dUrl: String?
)
