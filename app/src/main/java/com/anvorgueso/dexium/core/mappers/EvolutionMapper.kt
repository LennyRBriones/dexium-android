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
        val detail = chain.evolutionDetails.pickDisplayable()

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


    /**
     * 44 links list more than one way to evolve, and showing every alternative would not fit
     * under a sprite. Two rules pick the one worth showing:
     *
     * Methods that produce a different form are dropped first, because the chain still shows the
     * default species. Without this, Sandshrew would read "Ice Stone" when that stone actually
     * gives Sandslash-Alola, and level 22 gives the Sandslash on screen.
     *
     * Among what is left, an item wins over anything else. PokeAPI orders methods oldest-first,
     * so Leafeon led with the Eterna Forest mossy rock while the Leaf Stone — the method the
     * current games use, and the one a player can act on — sat last of six.
     */
    private fun List<EvolutionDetailDto>.pickDisplayable(): EvolutionDetailDto? {
        val defaultForm = filter { it.evolvedForm == null }.ifEmpty { this }
        return defaultForm.firstOrNull { it.item != null } ?: defaultForm.firstOrNull()
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
