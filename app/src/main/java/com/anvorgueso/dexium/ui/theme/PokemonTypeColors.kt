package com.anvorgueso.dexium.ui.theme

import androidx.compose.ui.graphics.Color

object PokemonTypeColors {
    private val typeColors = mapOf(
        "Normal" to Color(0xFFA8A878),
        "Fire" to Color(0xFFF08030),
        "Water" to Color(0xFF6890F0),
        "Electric" to Color(0xFFF8D030),
        "Grass" to Color(0xFF78C850),
        "Ice" to Color(0xFF98D8D8),
        "Fighting" to Color(0xFFC03028),
        "Poison" to Color(0xFFA040A0),
        "Ground" to Color(0xFFE0C068),
        "Flying" to Color(0xFFA890F0),
        "Psychic" to Color(0xFFF85888),
        "Bug" to Color(0xFFA8B820),
        "Rock" to Color(0xFFB8A038),
        "Ghost" to Color(0xFF705898),
        "Dragon" to Color(0xFF7038F8),
        "Dark" to Color(0xFF705848),
        "Steel" to Color(0xFFB8B8D0),
        "Fairy" to Color(0xFFEE99AC),
        "Stellar" to Color(0xFF44628E),
        "Unknown" to Color(0xFF68A090),
        "Fuego" to Color(0xFFF08030),
        "Agua" to Color(0xFF6890F0),
        "Eléctrico" to Color(0xFFF8D030),
        "Planta" to Color(0xFF78C850),
        "Hielo" to Color(0xFF98D8D8),
        "Lucha" to Color(0xFFC03028),
        "Veneno" to Color(0xFFA040A0),
        "Tierra" to Color(0xFFE0C068),
        "Volador" to Color(0xFFA890F0),
        "Psíquico" to Color(0xFFF85888),
        "Bicho" to Color(0xFFA8B820),
        "Roca" to Color(0xFFB8A038),
        "Fantasma" to Color(0xFF705898),
        "Dragón" to Color(0xFF7038F8),
        "Siniestro" to Color(0xFF705848),
        "Acero" to Color(0xFFB8B8D0),
        "Hada" to Color(0xFFEE99AC),
        "Estelar" to Color(0xFF44628E),
        "Desconocido" to Color(0xFF68A090)
    )

    private val typeIds = mapOf(
        "Normal" to 1, "Fighting" to 2, "Flying" to 3, "Poison" to 4,
        "Ground" to 5, "Rock" to 6, "Bug" to 7, "Ghost" to 8,
        "Steel" to 9, "Fire" to 10, "Water" to 11, "Grass" to 12,
        "Electric" to 13, "Psychic" to 14, "Ice" to 15, "Dragon" to 16,
        "Dark" to 17, "Fairy" to 18, "Stellar" to 19, "Unknown" to 10001,
        "Lucha" to 2, "Volador" to 3, "Veneno" to 4, "Tierra" to 5,
        "Roca" to 6, "Bicho" to 7, "Fantasma" to 8, "Acero" to 9,
        "Fuego" to 10, "Agua" to 11, "Planta" to 12, "Eléctrico" to 13,
        "Psíquico" to 14, "Hielo" to 15, "Dragón" to 16, "Siniestro" to 17,
        "Hada" to 18, "Estelar" to 19, "Desconocido" to 10001
    )

    fun getColor(type: String): Color {
        return typeColors[type] ?: typeColors["Normal"]!!
    }

    fun getGradientColors(type: String): Pair<Color, Color> {
        val base = getColor(type)
        return Pair(base, base.copy(alpha = 0.6f))
    }

    fun getTypeIconUrl(type: String): String? {
        val id = typeIds[type] ?: return null
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/types/generation-ix/scarlet-violet/$id.png"
    }

    fun getTypeSymbolUrl(type: String): String? {
        val id = typeIds[type] ?: return null
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/types/generation-ix/scarlet-violet/small/$id.png"
    }
}
