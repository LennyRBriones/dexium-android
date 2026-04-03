package com.anvorgueso.dexium.core.util

object Translations {

    private val typeNames = mapOf(
        "es" to mapOf(
            "Normal" to "Normal",
            "Fire" to "Fuego",
            "Water" to "Agua",
            "Electric" to "Eléctrico",
            "Grass" to "Planta",
            "Ice" to "Hielo",
            "Fighting" to "Lucha",
            "Poison" to "Veneno",
            "Ground" to "Tierra",
            "Flying" to "Volador",
            "Psychic" to "Psíquico",
            "Bug" to "Bicho",
            "Rock" to "Roca",
            "Ghost" to "Fantasma",
            "Dragon" to "Dragón",
            "Dark" to "Siniestro",
            "Steel" to "Acero",
            "Fairy" to "Hada",
            "Stellar" to "Estelar",
            "Unknown" to "Desconocido"
        )
    )

    private val habitatNames = mapOf(
        "es" to mapOf(
            "Cave" to "Cueva",
            "Forest" to "Bosque",
            "Grassland" to "Pradera",
            "Mountain" to "Montaña",
            "Rare" to "Raro",
            "Rough-terrain" to "Terreno Áspero",
            "Sea" to "Mar",
            "Urban" to "Urbano",
            "Waters-edge" to "Orilla del Agua"
        )
    )

    fun translateType(type: String, locale: String): String {
        if (locale == "en") return type
        return typeNames[locale]?.get(type) ?: type
    }

    fun translateHabitat(habitat: String, locale: String): String {
        if (locale == "en") return habitat
        return habitatNames[locale]?.get(habitat) ?: habitat
    }
}
