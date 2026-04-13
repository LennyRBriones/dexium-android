package com.anvorgueso.dexium.core.mappers

import com.anvorgueso.dexium.core.network.dto.ChainLinkDto
import com.anvorgueso.dexium.core.util.extractIdFromUrl
import com.anvorgueso.dexium.domain.model.EvolutionStage

object EvolutionMapper {

    fun ChainLinkDto.toEvolutionStages(useHdImages: Boolean): List<EvolutionStage> {
        val stages = mutableListOf<EvolutionStage>()
        flattenChain(this, stages, useHdImages)
        return stages
    }

    private fun flattenChain(
        chain: ChainLinkDto,
        stages: MutableList<EvolutionStage>,
        useHdImages: Boolean
    ) {
        val pokemonId = chain.species.url.extractIdFromUrl()
        val minLevel = chain.evolutionDetails.firstOrNull()?.minLevel
        val trigger = chain.evolutionDetails.firstOrNull()?.trigger?.name

        stages.add(
            EvolutionStage(
                pokemonId = pokemonId,
                pokemonName = chain.species.name,
                imageUrl = buildSpriteUrl(pokemonId, useHdImages),
                isBaby = chain.isBaby,
                minLevel = minLevel,
                trigger = trigger
            )
        )

        chain.evolvesTo.forEach { next ->
            flattenChain(next, stages, useHdImages)
        }
    }

    private fun buildSpriteUrl(pokemonId: Int, useHd: Boolean): String {
        return if (useHd) {
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$pokemonId.png"
        } else {
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$pokemonId.png"
        }
    }
}
