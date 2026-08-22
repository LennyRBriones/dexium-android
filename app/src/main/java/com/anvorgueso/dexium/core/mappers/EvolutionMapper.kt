package com.anvorgueso.dexium.core.mappers

import com.anvorgueso.dexium.core.network.dto.ChainLinkDto
import com.anvorgueso.dexium.core.network.dto.EvolutionDetailDto
import com.anvorgueso.dexium.core.util.extractIdFromUrl
import com.anvorgueso.dexium.domain.model.EvolutionRequirement
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
        // 44 links list more than one way to evolve; the first is the one the games document,
        // and showing every alternative would not fit under a sprite.
        val detail = chain.evolutionDetails.firstOrNull()

        stages.add(
            EvolutionStage(
                pokemonId = pokemonId,
                pokemonName = chain.species.name,
                imageUrl = buildSpriteUrl(pokemonId, useHdImages),
                shinyImageUrl = buildShinySpriteUrl(pokemonId),
                isBaby = chain.isBaby,
                minLevel = detail?.minLevel,
                trigger = detail?.trigger?.name,
                requirement = detail?.toRequirement()
            )
        )

        chain.evolvesTo.forEach { next ->
            flattenChain(next, stages, useHdImages)
        }
    }

    private fun EvolutionDetailDto.toRequirement() = EvolutionRequirement(
        trigger = trigger?.name,
        minLevel = minLevel,
        item = item?.name,
        heldItem = heldItem?.name,
        knownMove = knownMove?.name,
        knownMoveType = knownMoveType?.name,
        location = location?.name,
        partySpecies = partySpecies?.name,
        partyType = partyType?.name,
        tradeSpecies = tradeSpecies?.name,
        minHappiness = minHappiness,
        minAffection = minAffection,
        minBeauty = minBeauty,
        timeOfDay = timeOfDay?.takeIf { it.isNotBlank() },
        gender = gender,
        relativePhysicalStats = relativePhysicalStats,
        needsOverworldRain = needsOverworldRain == true,
        turnUpsideDown = turnUpsideDown == true
    )

    private fun buildShinySpriteUrl(pokemonId: Int): String {
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/shiny/$pokemonId.png"
    }

    private fun buildSpriteUrl(pokemonId: Int, useHd: Boolean): String {
        return if (useHd) {
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$pokemonId.png"
        } else {
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$pokemonId.png"
        }
    }
}
