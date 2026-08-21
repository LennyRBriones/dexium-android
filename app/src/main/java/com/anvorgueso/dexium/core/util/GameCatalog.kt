package com.anvorgueso.dexium.core.util

/**
 * Display names and release ordering for the PokeAPI `version` slugs.
 *
 * The encounters endpoint returns raw slugs like "omega-ruby" in no useful order, so the
 * game selector needs both a readable label and a stable order. Unknown slugs fall back to
 * a prettified version of the slug, so a future PokeAPI addition shows up readably instead
 * of disappearing from the list.
 */
object GameCatalog {

    private data class Game(val slug: String, val en: String, val es: String)

    // Release order. The index is the sort key, so keep this list ordered.
    private val games = listOf(
        Game("red-japan", "Red (Japan)", "Rojo (Japón)"),
        Game("green-japan", "Green (Japan)", "Verde (Japón)"),
        Game("blue-japan", "Blue (Japan)", "Azul (Japón)"),
        Game("red", "Red", "Rojo"),
        Game("blue", "Blue", "Azul"),
        Game("yellow", "Yellow", "Amarillo"),
        Game("gold", "Gold", "Oro"),
        Game("silver", "Silver", "Plata"),
        Game("crystal", "Crystal", "Cristal"),
        Game("ruby", "Ruby", "Rubí"),
        Game("sapphire", "Sapphire", "Zafiro"),
        Game("emerald", "Emerald", "Esmeralda"),
        Game("firered", "FireRed", "Rojo Fuego"),
        Game("leafgreen", "LeafGreen", "Verde Hoja"),
        Game("colosseum", "Colosseum", "Colosseum"),
        Game("xd", "XD: Gale of Darkness", "XD: Tempestad Oscura"),
        Game("diamond", "Diamond", "Diamante"),
        Game("pearl", "Pearl", "Perla"),
        Game("platinum", "Platinum", "Platino"),
        Game("heartgold", "HeartGold", "Oro HeartGold"),
        Game("soulsilver", "SoulSilver", "Plata SoulSilver"),
        Game("black", "Black", "Negro"),
        Game("white", "White", "Blanco"),
        Game("black-2", "Black 2", "Negro 2"),
        Game("white-2", "White 2", "Blanco 2"),
        Game("x", "X", "X"),
        Game("y", "Y", "Y"),
        Game("omega-ruby", "Omega Ruby", "Rubí Omega"),
        Game("alpha-sapphire", "Alpha Sapphire", "Zafiro Alfa"),
        Game("sun", "Sun", "Sol"),
        Game("moon", "Moon", "Luna"),
        Game("ultra-sun", "Ultra Sun", "Ultrasol"),
        Game("ultra-moon", "Ultra Moon", "Ultraluna"),
        Game("lets-go-pikachu", "Let's Go, Pikachu!", "Let's Go, Pikachu!"),
        Game("lets-go-eevee", "Let's Go, Eevee!", "Let's Go, Eevee!"),
        Game("sword", "Sword", "Espada"),
        Game("shield", "Shield", "Escudo"),
        Game("the-isle-of-armor-sword", "Isle of Armor (Sword)", "Isla de la Armadura (Espada)"),
        Game("the-isle-of-armor-shield", "Isle of Armor (Shield)", "Isla de la Armadura (Escudo)"),
        Game("the-crown-tundra-sword", "Crown Tundra (Sword)", "Nieves Corona (Espada)"),
        Game("the-crown-tundra-shield", "Crown Tundra (Shield)", "Nieves Corona (Escudo)"),
        Game("brilliant-diamond", "Brilliant Diamond", "Diamante Brillante"),
        Game("shining-pearl", "Shining Pearl", "Perla Reluciente"),
        Game("legends-arceus", "Legends: Arceus", "Leyendas: Arceus"),
        Game("scarlet", "Scarlet", "Escarlata"),
        Game("violet", "Violet", "Púrpura"),
        Game("the-teal-mask-scarlet", "Teal Mask (Scarlet)", "Máscara Turquesa (Escarlata)"),
        Game("the-teal-mask-violet", "Teal Mask (Violet)", "Máscara Turquesa (Púrpura)"),
        Game("the-indigo-disk-scarlet", "Indigo Disk (Scarlet)", "Disco Índigo (Escarlata)"),
        Game("the-indigo-disk-violet", "Indigo Disk (Violet)", "Disco Índigo (Púrpura)"),
        Game("legends-za", "Legends: Z-A", "Leyendas: Z-A"),
        Game("mega-dimension", "Mega Dimension", "Mega Dimensión"),
        Game("champions", "Champions", "Champions")
    )

    private val bySlug = games.withIndex().associate { (index, game) -> game.slug to (index to game) }

    fun displayName(slug: String, locale: String): String {
        val game = bySlug[slug]?.second ?: return prettifySlug(slug)
        return if (locale == "es") game.es else game.en
    }

    /** Unknown slugs sort last so a new PokeAPI version does not jump to the top of the list. */
    fun sortOrder(slug: String): Int = bySlug[slug]?.first ?: Int.MAX_VALUE
}

private val lowercaseWords = setOf("of", "the", "and", "in", "on", "at", "to", "b1f", "b2f", "b3f")

/** "fields-of-honor-area" -> "Fields of Honor Area" */
internal fun prettifySlug(slug: String): String =
    slug.split("-", "_")
        .filter { it.isNotBlank() }
        .mapIndexed { index, word ->
            // Title case, but connectors stay lowercase unless they lead the name.
            if (index > 0 && word in lowercaseWords) word else word.capitalizeFirst()
        }
        .joinToString(" ")
