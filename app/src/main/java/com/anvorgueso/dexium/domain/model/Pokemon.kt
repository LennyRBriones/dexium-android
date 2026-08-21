package com.anvorgueso.dexium.domain.model

data class Pokemon(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val animatedImageUrl: String?,
    val typePrimary: String,
    val typeSecondary: String?
) {
    /**
     * Grid rows start as stubs from the name precache and carry [UNKNOWN_TYPE], because
     * fetching real types per Pokémon would be one request each. Anything that renders a
     * type (badges, glow color) must stay hidden until the type backfill fills these in.
     */
    val hasRealType: Boolean
        get() = typePrimary != UNKNOWN_TYPE

    companion object {
        const val UNKNOWN_TYPE = "Unknown"
    }
}
