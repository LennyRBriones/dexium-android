package com.anvorgueso.dexium.domain.model

/** Every place a Pokémon can be found in one specific game version. */
data class GameEncounters(
    val versionSlug: String,
    val gameName: String,
    val locations: List<EncounterLocation>
)

data class EncounterLocation(
    val locationName: String,
    val method: String,
    val minLevel: Int,
    val maxLevel: Int,
    val chance: Int
)
