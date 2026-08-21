package com.anvorgueso.dexium.core.util

/**
 * Display names and release ordering for the PokeAPI `version` slugs.
 *
 * Localized names are generated from each version's `names` array. Only entries that differ
 * from the English name are stored, so the table stays readable; anything missing falls back
 * to English rather than showing a slug.
 *
 * The encounters endpoint returns raw slugs in no useful order, so the game selector needs
 * both a readable label and a stable order. Unknown slugs fall back to a prettified slug, so a
 * future PokeAPI addition shows up readably instead of disappearing from the list.
 */
object GameCatalog {

    private data class Game(
        val slug: String,
        val en: String,
        val localized: Map<String, String> = emptyMap()
    )

    // Release order. The index is the sort key, so keep this list ordered.
    private val games = listOf(
        Game("red-japan", "Red", mapOf("es" to "Rojo", "es-419" to "Rojo", "fr" to "Rouge", "de" to "Rot", "it" to "Rossa", "ja" to "赤", "ja-hrkt" to "赤", "ko" to "레드")),
        Game("green-japan", "Green", mapOf("es" to "Verde", "es-419" to "Verde", "fr" to "Vert", "de" to "Grüne", "it" to "Verde", "ja" to "緑", "ja-hrkt" to "緑", "ko" to "그린")),
        Game("blue-japan", "Blue", mapOf("es" to "Azul", "es-419" to "Azul", "fr" to "Bleu", "de" to "Blau", "it" to "Blu", "ja" to "青", "ja-hrkt" to "青", "ko" to "블루")),
        Game("red", "Red", mapOf("es" to "Rojo", "es-419" to "Rojo", "fr" to "Rouge", "de" to "Rot", "it" to "Rossa", "ja" to "赤", "ja-hrkt" to "赤", "ko" to "레드", "zh-hans" to "紅", "zh-hant" to "紅")),
        Game("blue", "Blue", mapOf("es" to "Azul", "es-419" to "Azul", "fr" to "Bleu", "de" to "Blau", "it" to "Blu", "ja" to "青", "ja-hrkt" to "青", "ko" to "블루", "zh-hans" to "藍", "zh-hant" to "藍")),
        Game("yellow", "Yellow", mapOf("es" to "Amarillo", "es-419" to "Amarillo", "fr" to "Jaune", "de" to "Gelb", "it" to "Gialla", "ja" to "ピカチュウ", "ja-hrkt" to "ピカチュウ", "ko" to "피카츄", "zh-hans" to "皮卡丘", "zh-hant" to "皮卡丘")),
        Game("gold", "Gold", mapOf("es" to "Oro", "es-419" to "Oro", "fr" to "Or", "it" to "Oro", "ja" to "金", "ja-hrkt" to "金", "ko" to "골드", "zh-hans" to "金", "zh-hant" to "金")),
        Game("silver", "Silver", mapOf("es" to "Plata", "es-419" to "Plata", "fr" to "Argent", "de" to "Silber", "it" to "Argento", "ja" to "銀", "ja-hrkt" to "銀", "ko" to "실버", "zh-hans" to "銀", "zh-hant" to "銀")),
        Game("crystal", "Crystal", mapOf("es" to "Cristal", "es-419" to "Cristal", "fr" to "Cristal", "de" to "Kristall", "it" to "Cristallo", "ja" to "クリスタル", "ja-hrkt" to "クリスタル", "ko" to "크리스탈", "zh-hans" to "水晶", "zh-hant" to "水晶")),
        Game("ruby", "Ruby", mapOf("es" to "Rubí", "es-419" to "Rubí", "fr" to "Rubis", "de" to "Rubin", "it" to "Rubino", "ja" to "ルビー", "ja-hrkt" to "ルビー", "ko" to "루비", "zh-hans" to "紅寶石", "zh-hant" to "紅寶石")),
        Game("sapphire", "Sapphire", mapOf("es" to "Zafiro", "es-419" to "Zafiro", "fr" to "Saphir", "de" to "Saphir", "it" to "Zaffiro", "ja" to "サファイア", "ja-hrkt" to "サファイア", "ko" to "사파이어", "zh-hans" to "藍寶石", "zh-hant" to "藍寶石")),
        Game("emerald", "Emerald", mapOf("es" to "Esmeralda", "es-419" to "Esmeralda", "fr" to "Émeraude", "de" to "Smaragd", "it" to "Smeraldo", "ja" to "エメラルド", "ja-hrkt" to "エメラルド", "ko" to "에메랄드", "zh-hans" to "綠寶石", "zh-hant" to "綠寶石")),
        Game("firered", "FireRed", mapOf("es" to "Rojo Fuego", "es-419" to "Rojo Fuego", "fr" to "Rouge Feu", "de" to "Feuerrot", "it" to "Rosso Fuoco", "ja" to "ファイアレッド", "ja-hrkt" to "ファイアレッド", "ko" to "파이어레드", "zh-hans" to "火紅", "zh-hant" to "火紅")),
        Game("leafgreen", "LeafGreen", mapOf("es" to "Verde Hoja", "es-419" to "Verde Hoja", "fr" to "Vert Feuille", "de" to "Blattgrün", "it" to "Verde Foglia", "ja" to "リーフグリーン", "ja-hrkt" to "リーフグリーン", "ko" to "리프그린", "zh-hans" to "葉綠", "zh-hant" to "葉綠")),
        Game("colosseum", "Colosseum", mapOf("ja" to "コロシアム", "ja-hrkt" to "コロシアム", "ko" to "콜로세움")),
        Game("xd", "XD"),
        Game("diamond", "Diamond", mapOf("es" to "Diamante", "es-419" to "Diamante", "fr" to "Diamant", "de" to "Diamant", "it" to "Diamante", "ja" to "ダイヤモンド", "ja-hrkt" to "ダイヤモンド", "ko" to "디아루가", "zh-hans" to "钻石", "zh-hant" to "鑽石")),
        Game("pearl", "Pearl", mapOf("es" to "Perla", "es-419" to "Perla", "fr" to "Perle", "de" to "Perl", "it" to "Perla", "ja" to "パール", "ja-hrkt" to "パール", "ko" to "펄기아", "zh-hans" to "珍珠", "zh-hant" to "珍珠")),
        Game("platinum", "Platinum", mapOf("es" to "Platino", "es-419" to "Platino", "fr" to "Platine", "de" to "Platin", "it" to "Platino", "ja" to "プラチナ", "ja-hrkt" to "プラチナ", "ko" to "기라티나", "zh-hans" to "白金", "zh-hant" to "白金")),
        Game("heartgold", "HeartGold", mapOf("es" to "Oro HeartGold", "es-419" to "Oro HeartGold", "fr" to "Or HeartGold", "it" to "Oro HeartGold", "ja" to "ハートゴールド", "ja-hrkt" to "ハートゴールド", "ko" to "하트골드", "zh-hans" to "心金", "zh-hant" to "心金")),
        Game("soulsilver", "SoulSilver", mapOf("es" to "Plata SoulSilver", "es-419" to "Plata SoulSilver", "fr" to "Argent SoulSilver", "it" to "Argento SoulSilver", "ja" to "ソウルシルバー", "ja-hrkt" to "ソウルシルバー", "ko" to "소울실버", "zh-hans" to "魂銀", "zh-hant" to "魂銀")),
        Game("black", "Black", mapOf("es" to "Negro", "es-419" to "Negro", "fr" to "Noir", "de" to "Schwarz", "it" to "Nera", "ja" to "ブラック", "ja-hrkt" to "ブラック", "ko" to "블랙", "zh-hans" to "黑", "zh-hant" to "黑")),
        Game("white", "White", mapOf("es" to "Blanco", "es-419" to "Blanco", "fr" to "Blanc", "de" to "Weiß", "it" to "Bianca", "ja" to "ホワイト", "ja-hrkt" to "ホワイト", "ko" to "화이트", "zh-hans" to "白", "zh-hant" to "白")),
        Game("black-2", "Black 2", mapOf("es" to "Negro 2", "es-419" to "Negro 2", "fr" to "Noir 2", "de" to "Schwarz 2", "it" to "Nera 2", "ja" to "ブラック2", "ja-hrkt" to "ブラック2", "ko" to "블랙 2", "zh-hans" to "黑2", "zh-hant" to "黑2")),
        Game("white-2", "White 2", mapOf("es" to "Blanco 2", "es-419" to "Blanco 2", "fr" to "Blanc 2", "de" to "Weiß 2", "it" to "Bianca 2", "ja" to "ホワイト2", "ja-hrkt" to "ホワイト2", "ko" to "화이트 2", "zh-hans" to "白2", "zh-hant" to "白2")),
        Game("x", "X"),
        Game("y", "Y"),
        Game("omega-ruby", "Omega Ruby", mapOf("es" to "Rubí Omega", "es-419" to "Rubí Omega", "fr" to "Rubis Oméga", "de" to "Omega Rubin", "it" to "Rubino Omega", "ja" to "オメガルビー", "ja-hrkt" to "オメガルビー", "ko" to "오메가루비", "zh-hans" to "欧米伽红宝石", "zh-hant" to "歐米加紅寶石")),
        Game("alpha-sapphire", "Alpha Sapphire", mapOf("es" to "Zafiro Alfa", "es-419" to "Zafiro Alfa", "fr" to "Saphir Alpha", "de" to "Alpha Saphir", "it" to "Zaffiro Alpha", "ja" to "アルファサファイア", "ja-hrkt" to "アルファサファイア", "ko" to "알파사파이어", "zh-hans" to "阿尔法蓝宝石", "zh-hant" to "阿爾法藍寶石")),
        Game("sun", "Sun", mapOf("es" to "Sol", "es-419" to "Sol", "fr" to "Soleil", "de" to "Sonne", "it" to "Sole", "ja" to "サン", "ja-hrkt" to "サン", "ko" to "썬", "zh-hans" to "太阳", "zh-hant" to "太陽")),
        Game("moon", "Moon", mapOf("es" to "Luna", "es-419" to "Luna", "fr" to "Lune", "de" to "Mond", "it" to "Luna", "ja" to "ムーン", "ja-hrkt" to "ムーン", "ko" to "문", "zh-hans" to "月亮", "zh-hant" to "月亮")),
        Game("ultra-sun", "Ultra Sun", mapOf("es" to "Ultrasol", "es-419" to "Ultrasol", "fr" to "Ultra-Soleil", "de" to "Ultrasonne", "it" to "Ultrasole", "ja" to "ウルトラサン", "ja-hrkt" to "ウルトラサン", "ko" to "울트라썬", "zh-hans" to "究极之日", "zh-hant" to "究極之日")),
        Game("ultra-moon", "Ultra Moon", mapOf("es" to "Ultraluna", "es-419" to "Ultraluna", "fr" to "Ultra-Lune", "de" to "Ultramond", "it" to "Ultraluna", "ja" to "ウルトラムーン", "ja-hrkt" to "ウルトラムーン", "ko" to "울트라문", "zh-hans" to "究极之月", "zh-hant" to "究極之月")),
        Game("lets-go-pikachu", "Let’s Go, Pikachu!", mapOf("fr" to "Let’s Go, Pikachu", "ja" to "Let’s Go! ピカチュウ", "ja-hrkt" to "Let’s Go! ピカチュウ", "ko" to "레츠고! 피카츄", "zh-hans" to "Let’s Go！皮卡丘", "zh-hant" to "Let’s Go！皮卡丘")),
        Game("lets-go-eevee", "Let’s Go, Eevee!", mapOf("fr" to "Let’s Go, Évoli", "de" to "Let’s Go, Evoli!", "ja" to "Let’s Go! イーブイ", "ja-hrkt" to "Let’s Go! イーブイ", "ko" to "레츠고! 이브이", "zh-hans" to "Let’s Go！伊布", "zh-hant" to "Let’s Go！伊布")),
        Game("sword", "Sword", mapOf("es" to "Espada", "es-419" to "Espada", "fr" to "Épée", "de" to "Schwert", "it" to "Spada", "ja" to "ソード", "ja-hrkt" to "ソード", "ko" to "소드", "zh-hans" to "剑", "zh-hant" to "劍")),
        Game("shield", "Shield", mapOf("es" to "Escudo", "es-419" to "Escudo", "fr" to "Bouclier", "de" to "Schild", "it" to "Scudo", "ja" to "シールド", "ja-hrkt" to "シールド", "ko" to "실드", "zh-hans" to "盾", "zh-hant" to "盾")),
        Game("the-isle-of-armor-sword", "Sword: The Isle of Armor", mapOf("es" to "Espada: La isla de la armadura", "es-419" to "Espada: La isla de la armadura", "fr" to "Épée: L’île solitaire de l’Armure", "de" to "Schwert: Die Insel der Rüstung", "it" to "Spada: L’isola solitaria dell’armatura", "ja" to "ソード: 鎧の孤島", "ja-hrkt" to "ソード: 鎧の孤島", "ko" to "소드: 갑옷의 외딴섬", "zh-hans" to "剑: 铠之孤岛", "zh-hant" to "劍: 鎧之孤島")),
        Game("the-isle-of-armor-shield", "Shield: The Isle of Armor", mapOf("es" to "Escudo: La isla de la armadura", "es-419" to "Escudo: La isla de la armadura", "fr" to "Bouclier: L’île solitaire de l’Armure", "de" to "Schild: Die Insel der Rüstung", "it" to "Scudo: L’isola solitaria dell’armatura", "ja" to "シールド: 鎧の孤島", "ja-hrkt" to "シールド: 鎧の孤島", "ko" to "실드: 갑옷의 외딴섬", "zh-hans" to "盾: 铠之孤岛", "zh-hant" to "盾: 鎧之孤島")),
        Game("the-crown-tundra-sword", "Sword: The Crown Tundra", mapOf("es" to "Espada: Las nieves de la corona", "es-419" to "Espada: Las nieves de la corona", "fr" to "Épée: Les terres enneigées de la Couronne", "de" to "Schwert: Die Schneelande der Krone", "it" to "Spada: Le terre innevate della corona", "ja" to "ソード: 冠の雪原", "ja-hrkt" to "ソード: 冠の雪原", "ko" to "소드: 왕관의 설원", "zh-hans" to "剑: 冠之雪原", "zh-hant" to "劍: 冠之雪原")),
        Game("the-crown-tundra-shield", "Shield: The Crown Tundra", mapOf("es" to "Escudo: Las nieves de la corona", "es-419" to "Escudo: Las nieves de la corona", "fr" to "Bouclier: Les terres enneigées de la Couronne", "de" to "Schild: Die Schneelande der Krone", "it" to "Scudo: Le terre innevate della corona", "ja" to "シールド: 冠の雪原", "ja-hrkt" to "シールド: 冠の雪原", "ko" to "실드: 왕관의 설원", "zh-hans" to "盾: 冠之雪原", "zh-hant" to "盾: 冠之雪原")),
        Game("brilliant-diamond", "Brilliant Diamond", mapOf("es" to "Diamante Brillante", "es-419" to "Diamante Brillante", "fr" to "Diamant Étincelant", "de" to "Strahlender Diamant", "it" to "Diamante Lucente", "ja" to "ブリリアントダイヤモンド", "ja-hrkt" to "ブリリアントダイヤモンド", "ko" to "브릴리언트 다이아몬드", "zh-hans" to "晶灿钻石", "zh-hant" to "晶燦鑽石")),
        Game("shining-pearl", "Shining Pearl", mapOf("es" to "Perla Reluciente", "es-419" to "Perla Reluciente", "fr" to "Perle Scintillante", "de" to "Leuchtende Perle", "it" to "Perla Splendente", "ja" to "シャイニングパール", "ja-hrkt" to "シャイニングパール", "ko" to "샤이닝 펄", "zh-hans" to "明亮珍珠", "zh-hant" to "明亮珍珠")),
        Game("legends-arceus", "Legends: Arceus", mapOf("es" to "Leyendas: Arceus", "es-419" to "Leyendas: Arceus", "fr" to "Légendes : Arceus", "de" to "Legenden: Arceus", "it" to "Leggende: Arceus", "ja" to "LEGENDS アルセウス", "ja-hrkt" to "LEGENDS アルセウス", "ko" to "LEGENDS 아르세우스", "zh-hans" to "传说 阿尔宙斯", "zh-hant" to "傳說 阿爾宙斯")),
        Game("scarlet", "Scarlet", mapOf("es" to "Escarlata", "es-419" to "Escarlata", "fr" to "Écarlate", "de" to "Karmesin", "it" to "Scarlatto", "ja" to "スカーレット", "ja-hrkt" to "スカーレット", "ko" to "스칼렛", "zh-hans" to "朱", "zh-hant" to "朱")),
        Game("violet", "Violet", mapOf("es" to "Púrpura", "es-419" to "Púrpura", "de" to "Purpur", "it" to "Violetto", "ja" to "バイオレット", "ja-hrkt" to "バイオレット", "ko" to "바이올렛", "zh-hans" to "紫", "zh-hant" to "紫")),
        Game("the-teal-mask-scarlet", "Scarlet: The Teal Mask", mapOf("es" to "Escarlata: La máscara turquesa", "es-419" to "Escarlata: La máscara turquesa", "fr" to "Écarlate: Le Masque Turquoise", "de" to "Karmesin: Die Türkisgrüne Maske", "it" to "Scarlatto: La maschera turchese", "ja" to "スカーレット: 碧の仮面", "ja-hrkt" to "スカーレット: 碧の仮面", "zh-hans" to "朱: 碧之假面")),
        Game("the-teal-mask-violet", "Violet: The Teal Mask", mapOf("es" to "Púrpura: La máscara turquesa", "es-419" to "Púrpura: La máscara turquesa", "fr" to "Violet: Le Masque Turquoise", "de" to "Purpur: Die Türkisgrüne Maske", "it" to "Violetto: La maschera turchese", "ja" to "バイオレット: 碧の仮面", "ja-hrkt" to "バイオレット: 碧の仮面", "zh-hans" to "紫: 碧之假面")),
        Game("the-indigo-disk-scarlet", "Scarlet: The Indigo Disk", mapOf("es" to "Escarlata: El disco índigo", "es-419" to "Escarlata: El disco índigo", "fr" to "Écarlate: Le Disque Indigo", "de" to "Karmesin: Die Indigoblaue Scheibe", "it" to "Scarlatto: Il disco indaco", "ja" to "スカーレット: 藍の円盤", "ja-hrkt" to "スカーレット: 藍の円盤", "zh-hans" to "朱: 藍之圓盤")),
        Game("the-indigo-disk-violet", "Violet: The Indigo Disk", mapOf("es" to "Púrpura: El disco índigo", "es-419" to "Púrpura: El disco índigo", "fr" to "Violet: Le Disque Indigo", "de" to "Purpur: Die Indigoblaue Scheibe", "it" to "Violetto: Il disco indaco", "ja" to "バイオレット: 藍の円盤", "ja-hrkt" to "バイオレット: 藍の円盤", "zh-hans" to "紫: 藍之圓盤")),
        Game("legends-za", "Legends: Z-A", mapOf("es" to "Leyendas: Z-A", "es-419" to "Leyendas: Z-A", "fr" to "Légendes : Z-A", "de" to "Legenden: Z-A", "it" to "Leggende: Z-A", "ja" to "レジェンズ Z-A", "ja-hrkt" to "レジェンズ Z-A", "ko" to "LEGENDS Z-A", "zh-hans" to "传说 Z-A", "zh-hant" to "傳說 Z-A")),
        Game("mega-dimension", "Mega Dimension", mapOf("es" to "Megadimensión", "es-419" to "Megadimensión", "fr" to "Méga-Dimension", "de" to "Mega-Dimension", "it" to "Megadimensione", "ko" to "메가 차원 러시", "zh-hans" to "超次元爆涌", "zh-hant" to "超次元爆湧")),
        Game("champions", "Champions")
    )

    private val bySlug = games.withIndex().associate { (index, game) -> game.slug to (index to game) }

    fun displayName(slug: String, language: String): String {
        val game = bySlug[slug]?.second ?: return prettifySlug(slug)
        return game.localized[language] ?: game.en
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
