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

    // Keyed by the raw PokeAPI encounter-method slug, since these never reach the DB and so
    // are never pre-capitalized the way types and habitats are.
    private val encounterMethods = mapOf(
        "en" to mapOf(
            "walk" to "Walking in grass",
            "surf" to "Surfing",
            "old-rod" to "Old Rod",
            "good-rod" to "Good Rod",
            "super-rod" to "Super Rod",
            "rock-smash" to "Rock Smash",
            "headbutt" to "Headbutt",
            "headbutt-low" to "Headbutt (low yield)",
            "headbutt-normal" to "Headbutt (normal yield)",
            "headbutt-high" to "Headbutt (high yield)",
            "dark-grass" to "Dark grass",
            "grass-spots" to "Rustling grass",
            "cave-spots" to "Dust cloud",
            "bridge-spots" to "Bird shadow",
            "surf-spots" to "Rippling water",
            "super-rod-spots" to "Rippling water (Super Rod)",
            "yellow-flowers" to "Yellow flowers",
            "purple-flowers" to "Purple flowers",
            "red-flowers" to "Red flowers",
            "rough-terrain" to "Rough terrain",
            "gift" to "Gift",
            "gift-egg" to "Gift (Egg)",
            "static" to "Static encounter",
            "npc-trade" to "In-game trade",
            "seaweed" to "Seaweed",
            "squirt-bottle" to "Squirt Bottle",
            "wailmer-pail" to "Wailmer Pail",
            "island-scan" to "Island Scan",
            "sos" to "SOS Battle",
            "sos-from-bubbling-spot" to "SOS Battle (bubbling spot)",
            "bubbling-spots" to "Bubbling spot",
            "berry-trees" to "Berry trees",
            "devon-scope" to "Devon Scope",
            "feebas-tile-fishing" to "Feebas tile fishing",
            "roaming-grass" to "Roaming (land)",
            "roaming-water" to "Roaming (water)",
            "pokeflute" to "Poké Flute",
            "overworld" to "Overworld",
            "overworld-water" to "Overworld (water)",
            "overworld-flying" to "Overworld (flying)",
            "overworld-dirt" to "Overworld (dirt)",
            "overworld-special" to "Overworld (special)",
            "overworld-flying-special" to "Overworld (flying, special)",
            "overworld-water-special" to "Overworld (water, special)",
            "horde" to "Horde Battle",
            "hidden-grotto" to "Hidden Grotto",
            "honey-tree" to "Honey Tree",
            "wanderer" to "Wandering",
            "wanderer-water" to "Wandering (water)",
            "chase-water" to "Water chase",
            "dynamax-adventure" to "Dynamax Adventure",
            "max-raid" to "Max Raid Battle",
            "trash-can-ambush" to "Trash can ambush",
            "rustling-bush-ambush" to "Rustling bush ambush",
            "ceiling-ambush" to "Ceiling ambush",
            "ground-ambush" to "Ground ambush",
            "sky-ambush" to "Sky ambush",
            "pokespot" to "Poké Spot",
            "snag" to "Snag",
            "snag-rematch" to "Snag (rematch)",
            "pokemon-ranger" to "Pokémon Ranger",
            "pokemon-channel-pal" to "Pokémon Channel",
            "pokemon-battle-revolution" to "Pokémon Battle Revolution",
            "colosseum-bonus-disc-us" to "Colosseum Bonus Disc (US)",
            "colosseum-bonus-disc-jpn" to "Colosseum Bonus Disc (JP)",
            "new-york-pokecenter-wish-eggs" to "New York Pokémon Center"
        ),
        "es" to mapOf(
            "walk" to "Caminando en hierba",
            "surf" to "Surf",
            "old-rod" to "Caña Vieja",
            "good-rod" to "Caña Buena",
            "super-rod" to "Súper Caña",
            "rock-smash" to "Golpe Roca",
            "headbutt" to "Cabezazo",
            "headbutt-low" to "Cabezazo (poco frecuente)",
            "headbutt-normal" to "Cabezazo (frecuencia normal)",
            "headbutt-high" to "Cabezazo (muy frecuente)",
            "dark-grass" to "Hierba oscura",
            "grass-spots" to "Hierba agitada",
            "cave-spots" to "Nube de polvo",
            "bridge-spots" to "Sombra de ave",
            "surf-spots" to "Agua ondulante",
            "super-rod-spots" to "Agua ondulante (Súper Caña)",
            "yellow-flowers" to "Flores amarillas",
            "purple-flowers" to "Flores moradas",
            "red-flowers" to "Flores rojas",
            "rough-terrain" to "Terreno áspero",
            "gift" to "Regalo",
            "gift-egg" to "Regalo (Huevo)",
            "static" to "Encuentro fijo",
            "npc-trade" to "Intercambio en el juego",
            "seaweed" to "Algas",
            "squirt-bottle" to "Botella Spray",
            "wailmer-pail" to "Cubo Wailmer",
            "island-scan" to "Escáner Insular",
            "sos" to "Combate SOS",
            "sos-from-bubbling-spot" to "Combate SOS (zona de burbujas)",
            "bubbling-spots" to "Zona de burbujas",
            "berry-trees" to "Árboles de bayas",
            "devon-scope" to "Buscagafas Devon",
            "feebas-tile-fishing" to "Pesca en casilla de Feebas",
            "roaming-grass" to "Itinerante (tierra)",
            "roaming-water" to "Itinerante (agua)",
            "pokeflute" to "Flauta Poké",
            "overworld" to "En el mapa",
            "overworld-water" to "En el mapa (agua)",
            "overworld-flying" to "En el mapa (volando)",
            "overworld-dirt" to "En el mapa (tierra)",
            "overworld-special" to "En el mapa (especial)",
            "overworld-flying-special" to "En el mapa (volando, especial)",
            "overworld-water-special" to "En el mapa (agua, especial)",
            "horde" to "Combate en horda",
            "hidden-grotto" to "Gruta Oculta",
            "honey-tree" to "Árbol con miel",
            "wanderer" to "Errante",
            "wanderer-water" to "Errante (agua)",
            "chase-water" to "Persecución en agua",
            "dynamax-adventure" to "Aventura Dinamax",
            "max-raid" to "Combate Raid Dinamax",
            "trash-can-ambush" to "Emboscada en basurero",
            "rustling-bush-ambush" to "Emboscada en arbusto",
            "ceiling-ambush" to "Emboscada desde el techo",
            "ground-ambush" to "Emboscada desde el suelo",
            "sky-ambush" to "Emboscada desde el cielo",
            "pokespot" to "Poké Spot",
            "snag" to "Captura (Snag)",
            "snag-rematch" to "Captura (revancha)",
            "pokemon-ranger" to "Pokémon Ranger",
            "pokemon-channel-pal" to "Pokémon Channel",
            "pokemon-battle-revolution" to "Pokémon Battle Revolution",
            "colosseum-bonus-disc-us" to "Disco Bonus Colosseum (EE. UU.)",
            "colosseum-bonus-disc-jpn" to "Disco Bonus Colosseum (Japón)",
            "new-york-pokecenter-wish-eggs" to "Centro Pokémon de Nueva York"
        )
    )

    // Reverse of every typeNames table, so a translated name can be resolved back to the
    // canonical English key. Needed because PokemonDetailEntity stores type names already
    // translated, so on a Spanish device the detail screen holds "Fuego", not "Fire".
    private val canonicalTypes: Map<String, String> =
        typeNames.values.flatMap { it.entries }.associate { (english, translated) ->
            translated.lowercase() to english
        }

    fun translateType(type: String, locale: String): String {
        if (locale == "en") return type
        return typeNames[locale]?.get(type) ?: type
    }

    /** Resolves a type name in any supported language to its canonical English key. */
    fun canonicalType(type: String): String =
        canonicalTypes[type.lowercase()] ?: type.capitalizeFirst()

    /** Falls back to the prettified slug so an unmapped method stays readable. */
    fun translateEncounterMethod(method: String, locale: String): String {
        val table = encounterMethods[locale] ?: encounterMethods["en"]
        return table?.get(method) ?: encounterMethods["en"]?.get(method) ?: prettifySlug(method)
    }

    fun translateHabitat(habitat: String, locale: String): String {
        if (locale == "en") return habitat
        return habitatNames[locale]?.get(habitat) ?: habitat
    }
}
