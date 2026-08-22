package com.anvorgueso.dexium.domain.model

data class EvolutionStage(
    val pokemonId: Int,
    val pokemonName: String,
    val imageUrl: String,
    /** Shiny artwork for the same stage, so the Shiny Dex keeps the whole line consistent. */
    val shinyImageUrl: String,
    val isBaby: Boolean,
    val minLevel: Int?,
    val trigger: String?,
    /** What this stage needs to be reached. Null on the first stage, which evolves from nothing. */
    val requirement: EvolutionRequirement? = null
)

/**
 * The conditions PokeAPI attaches to one evolution, kept as data so the label can be built in
 * the user's language at draw time instead of being frozen into the cache in one language.
 *
 * Slugs stay canonical (`water-stone`, `mt-coronet`); [com.anvorgueso.dexium.core.util.LocalizedEvolutionNames]
 * turns them into display names.
 */
data class EvolutionRequirement(
    val trigger: String?,
    val minLevel: Int?,
    val item: String?,
    val heldItem: String?,
    val knownMove: String?,
    val knownMoveType: String?,
    val location: String?,
    val partySpecies: String?,
    val partyType: String?,
    val tradeSpecies: String?,
    val minHappiness: Int?,
    val minAffection: Int?,
    val minBeauty: Int?,
    val timeOfDay: String?,
    val gender: Int?,
    val relativePhysicalStats: Int?,
    val needsOverworldRain: Boolean,
    val turnUpsideDown: Boolean
) {
    /** True when there is nothing to show, so the UI can skip the label entirely. */
    val isEmpty: Boolean
        get() = trigger == null && minLevel == null && item == null && heldItem == null &&
            knownMove == null && knownMoveType == null && location == null &&
            partySpecies == null && partyType == null && tradeSpecies == null &&
            minHappiness == null && minAffection == null && minBeauty == null &&
            timeOfDay == null && gender == null && relativePhysicalStats == null &&
            !needsOverworldRain && !turnUpsideDown
}
