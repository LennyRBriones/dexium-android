package com.anvorgueso.dexium.core.util

/**
 * Localized names for the things an evolution can require, generated from PokeAPI's `names`
 * arrays (`/item`, `/move`, `/location`, `/pokemon-species`).
 *
 * Baked in for the same reason as [LocalizedTypeNames]: it is a small table that never changes,
 * and fetching it would mean one request per requirement every time a detail screen opens.
 * Only the slugs that actually appear in an evolution chain are here, surveyed across all 541
 * chains, so this is the complete set rather than the whole item list.
 *
 * PokeAPI has no Chinese or kana names for any of these, and it is missing Spanish, Italian and
 * Korean for the generation 8 and 9 items, so [of] falls back to English and then to a
 * prettified slug. That gap is upstream, not ours.
 */
object LocalizedEvolutionNames {

    private val items: Map<String, Map<String, String>> = mapOf(
        "auspicious-armor" to mapOf("en" to "Auspicious Armor", "fr" to "Armure de la Fortune", "ja" to "イワイノヨロイ"),
        "black-augurite" to mapOf("en" to "Black Augurite", "de" to "Schwarzaugit"),
        "chipped-pot" to mapOf("en" to "Chipped Pot", "es" to "Tetera Rota", "fr" to "Théière Ébréchée", "de" to "Löchrige Kanne", "it" to "Teiera crepata", "ja" to "かけたポット", "ko" to "이빠진포트"),
        "cracked-pot" to mapOf("en" to "Cracked Pot", "es" to "Tetera Agrietada", "fr" to "Théière Fêlée", "de" to "Rissige Kanne", "it" to "Teiera rotta", "ja" to "われたポット", "ko" to "깨진포트"),
        "dawn-stone" to mapOf("en" to "Dawn Stone", "es" to "Piedra Alba", "fr" to "Pierre Aube", "de" to "Funkelstein", "it" to "Pietralbore", "ja" to "めざめいし", "ko" to "각성의돌"),
        "deep-sea-scale" to mapOf("en" to "Deep Sea Scale", "es" to "Escama Marina", "fr" to "Écaille Océan", "de" to "Abyssplatte", "it" to "Squamabissi", "ja" to "しんかいのウロコ", "ko" to "심해의비늘"),
        "deep-sea-tooth" to mapOf("en" to "Deep Sea Tooth", "es" to "Diente Marino", "fr" to "Dent Océan", "de" to "Abysszahn", "it" to "Dente Abissi", "ja" to "しんかいのキバ", "ko" to "심해의이빨"),
        "dragon-scale" to mapOf("en" to "Dragon Scale", "es" to "Escama Dragón", "fr" to "Écaille Draco", "de" to "Drachenhaut", "it" to "Squama Drago", "ja" to "りゅうのウロコ", "ko" to "용의비늘"),
        "dubious-disc" to mapOf("en" to "Dubious Disc", "es" to "Disco Extraño", "fr" to "CD Douteux", "de" to "Dubiosdisc", "it" to "Dubbiodisco", "ja" to "あやしいパッチ", "ko" to "괴상한패치"),
        "dusk-stone" to mapOf("en" to "Dusk Stone", "es" to "Piedra Noche", "fr" to "Pierre Nuit", "de" to "Finsterstein", "it" to "Neropietra", "ja" to "やみのいし", "ko" to "어둠의돌"),
        "electirizer" to mapOf("en" to "Electirizer", "es" to "Electrizador", "fr" to "Électriseur", "de" to "Stromisierer", "it" to "Elettritore", "ja" to "エレキブースター", "ko" to "에레키부스터"),
        "fire-stone" to mapOf("en" to "Fire Stone", "es" to "Piedra Fuego", "fr" to "Pierre Feu", "de" to "Feuerstein", "it" to "Pietrafocaia", "ja" to "ほのおのいし", "ko" to "불꽃의돌"),
        "galarica-cuff" to mapOf("en" to "Galarica Cuff", "es" to "Brazal Galanuez", "fr" to "Bracelet Galanoa", "de" to "Galarnuss-Reif", "it" to "Fascia Galarnoce", "ja" to "ガラナツブレス", "ko" to "가라두구팔찌"),
        "galarica-wreath" to mapOf("en" to "Galarica Wreath", "es" to "Corona Galanuez", "fr" to "Couronne Galanoa", "de" to "Galarnuss-Kranz", "it" to "Corona Galarnoce", "ja" to "ガラナツリース", "ko" to "가라두구머리장식"),
        "ice-stone" to mapOf("en" to "Ice Stone", "es" to "Piedra Hielo", "fr" to "Pierre Glace", "de" to "Eisstein", "it" to "Pietragelo", "ja" to "こおりのいし", "ko" to "얼음의돌"),
        "kings-rock" to mapOf("en" to "King’s Rock", "es" to "Roca del Rey", "fr" to "Roche Royale", "de" to "King-Stein", "it" to "Roccia di Re", "ja" to "おうじゃのしるし", "ko" to "왕의징표석"),
        "leaf-stone" to mapOf("en" to "Leaf Stone", "es" to "Piedra Hoja", "fr" to "Pierre Plante", "de" to "Blattstein", "it" to "Pietrafoglia", "ja" to "リーフのいし", "ko" to "리프의돌"),
        "magmarizer" to mapOf("en" to "Magmarizer", "es" to "Magmatizador", "fr" to "Magmariseur", "de" to "Magmaisierer", "it" to "Magmatore", "ja" to "マグマブースター", "ko" to "마그마부스터"),
        "malicious-armor" to mapOf("en" to "Malicious Armor", "fr" to "Armure de la Rancune", "ja" to "ノロイノヨロイ"),
        "masterpiece-teacup" to mapOf("en" to "Masterpiece Teacup", "fr" to "Bol Exceptionnel", "ja" to "ボンサクのちゃわん"),
        "metal-alloy" to mapOf("en" to "Metal Alloy", "de" to "Legierungsmetall"),
        "metal-coat" to mapOf("en" to "Metal Coat", "es" to "Revest. Metálico", "fr" to "Peau Métal", "de" to "Metallmantel", "it" to "Metalcoperta", "ja" to "メタルコート", "ko" to "금속코트"),
        "moon-stone" to mapOf("en" to "Moon Stone", "es" to "Piedra Lunar", "fr" to "Pierre Lune", "de" to "Mondstein", "it" to "Pietralunare", "ja" to "つきのいし", "ko" to "달의돌"),
        "oval-stone" to mapOf("en" to "Oval Stone", "es" to "Piedra Oval", "fr" to "Pierre Ovale", "de" to "Ovaler Stein", "it" to "Pietraovale", "ja" to "まんまるいし", "ko" to "동글동글돌"),
        "peat-block" to mapOf("en" to "Peat Block", "de" to "Torfblock"),
        "prism-scale" to mapOf("en" to "Prism Scale", "es" to "Escama Bella", "fr" to "Bel’Écaille", "de" to "Schönschuppe", "it" to "Squama Bella", "ja" to "きれいなウロコ", "ko" to "고운비늘"),
        "protector" to mapOf("en" to "Protector", "es" to "Protector", "fr" to "Protecteur", "de" to "Schützer", "it" to "Copertura", "ja" to "プロテクター", "ko" to "프로텍터"),
        "razor-claw" to mapOf("en" to "Razor Claw", "es" to "Garra Afilada", "fr" to "Griffe Rasoir", "de" to "Scharfklaue", "it" to "Affilartigli", "ja" to "するどいツメ", "ko" to "예리한손톱"),
        "razor-fang" to mapOf("en" to "Razor Fang", "es" to "Colmillo Agudo", "fr" to "Croc Rasoir", "de" to "Scharfzahn", "it" to "Affilodente", "ja" to "するどいキバ", "ko" to "예리한이빨"),
        "reaper-cloth" to mapOf("en" to "Reaper Cloth", "es" to "Tela Terrible", "fr" to "Tissu Fauche", "de" to "Düsterumhang", "it" to "Terrorpanno", "ja" to "れいかいのぬの", "ko" to "영계의천"),
        "sachet" to mapOf("en" to "Sachet", "es" to "Saquito Fragante", "fr" to "Sachet Senteur", "de" to "Duftbeutel", "it" to "Bustina aromi", "ja" to "においぶくろ", "ko" to "향기주머니"),
        "scroll-of-darkness" to mapOf("en" to "Scroll of Darkness", "fr" to "Rouleau des Ténèbres", "ja" to "あくのかけじく"),
        "scroll-of-waters" to mapOf("en" to "Scroll of Waters", "fr" to "Rouleau de l'Eau", "ja" to "みずのかけじく"),
        "shiny-stone" to mapOf("en" to "Shiny Stone", "es" to "Piedra Día", "fr" to "Pierre Éclat", "de" to "Leuchtstein", "it" to "Pietrabrillo", "ja" to "ひかりのいし", "ko" to "빛의돌"),
        "sun-stone" to mapOf("en" to "Sun Stone", "es" to "Piedra Solar", "fr" to "Pierre Soleil", "de" to "Sonnenstein", "it" to "Pietrasolare", "ja" to "たいようのいし", "ko" to "태양의돌"),
        "sweet-apple" to mapOf("en" to "Sweet Apple", "es" to "Manzana Dulce", "fr" to "Pomme Sucrée", "de" to "Süßer Apfel", "it" to "Dolcepomo", "ja" to "あまーいりんご", "ko" to "달콤한사과"),
        "syrupy-apple" to mapOf("en" to "Syrupy Apple", "fr" to "Pomme Nectar", "de" to "Saftiger Apfel", "ja" to "みついりりんご"),
        "tart-apple" to mapOf("en" to "Tart Apple", "es" to "Manzana Ácida", "fr" to "Pomme Acidulée", "de" to "Saurer Apfel", "it" to "Aspropomo", "ja" to "すっぱいりんご", "ko" to "새콤한사과"),
        "thunder-stone" to mapOf("en" to "Thunder Stone", "es" to "Piedra Trueno", "fr" to "Pierre Foudre", "de" to "Donnerstein", "it" to "Pietratuono", "ja" to "かみなりのいし", "ko" to "천둥의돌"),
        "unremarkable-teacup" to mapOf("en" to "Unremarkable Teacup", "fr" to "Bol Médiocre", "ja" to "ボンサクのちゃわん"),
        "up-grade" to mapOf("en" to "Upgrade", "es" to "Mejora", "fr" to "Améliorator", "de" to "Up-Grade", "it" to "Upgrade", "ja" to "アップグレード", "ko" to "업그레이드"),
        "water-stone" to mapOf("en" to "Water Stone", "es" to "Piedra Agua", "fr" to "Pierre Eau", "de" to "Wasserstein", "it" to "Pietraidrica", "ja" to "みずのいし", "ko" to "물의돌"),
        "whipped-dream" to mapOf("en" to "Whipped Dream", "es" to "Dulce de Nata", "fr" to "Chantibonbon", "de" to "Sahnehäubchen", "it" to "Dolcespuma", "ja" to "ホイップポップ", "ko" to "휘핑팝"),
    )

    private val moves: Map<String, Map<String, String>> = mapOf(
        "ancient-power" to mapOf("en" to "Ancient Power", "es" to "Poder Pasado", "fr" to "Pouvoir Antique", "de" to "Antik-Kraft", "it" to "Forzantica", "ja" to "げんしのちから", "ko" to "원시의힘"),
        "barb-barrage" to mapOf("en" to "Barb Barrage", "es" to "Mil Púas Tóxicas", "fr" to "Multitoxik", "it" to "Mille Fielespine", "ja" to "どくばりセンボン", "ko" to "독침천발"),
        "double-hit" to mapOf("en" to "Double Hit", "es" to "Doble Golpe", "fr" to "Coup Double", "de" to "Doppelschlag", "it" to "Doppiosmash", "ja" to "ダブルアタック", "ko" to "더블어택"),
        "dragon-cheer" to mapOf("en" to "Dragon Cheer", "es" to "Bramido Dragón", "fr" to "Cri Draconique", "de" to "Drachenschrei", "it" to "Grido del Drago", "ja" to "ドラゴンエール", "ko" to "드래곤옐"),
        "dragon-pulse" to mapOf("en" to "Dragon Pulse", "es" to "Pulso Dragón", "fr" to "Draco-Choc", "de" to "Drachenpuls", "it" to "Dragopulsar", "ja" to "りゅうのはどう", "ko" to "용의파동"),
        "hyper-drill" to mapOf("en" to "Hyper Drill", "es" to "Hipertaladro", "fr" to "Hyperceuse", "it" to "Ipertrapano", "ja" to "ハイパードリル", "ko" to "하이퍼드릴"),
        "mimic" to mapOf("en" to "Mimic", "es" to "Mimético", "fr" to "Copie", "de" to "Mimikry", "it" to "Mimica", "ja" to "ものまね", "ko" to "흉내내기"),
        "rollout" to mapOf("en" to "Rollout", "es" to "Rodar", "fr" to "Roulade", "de" to "Walzer", "it" to "Rotolamento", "ja" to "ころがる", "ko" to "구르기"),
        "stomp" to mapOf("en" to "Stomp", "es" to "Pisotón", "fr" to "Écrasement", "de" to "Stampfer", "it" to "Pestone", "ja" to "ふみつけ", "ko" to "짓밟기"),
        "taunt" to mapOf("en" to "Taunt", "es" to "Mofa", "fr" to "Provoc", "de" to "Verhöhner", "it" to "Provocazione", "ja" to "ちょうはつ", "ko" to "도발"),
        "twin-beam" to mapOf("en" to "Twin Beam", "es" to "Láser Doble", "fr" to "Double Laser", "it" to "Doppioraggio", "ja" to "ツインビーム", "ko" to "트윈빔"),
    )

    private val locations: Map<String, Map<String, String>> = mapOf(
        "blush-mountain" to mapOf("en" to "Blush Mountain", "es" to "Monte Rubor", "fr" to "Mont Ardent", "de" to "Glühberg", "it" to "Monte Tepore", "ja" to "ホテリ山", "ko" to "화끈산"),
        "chargestone-cave" to mapOf("en" to "Chargestone Cave", "fr" to "Grotte Électrolithe", "de" to "Elektrolithhöhle"),
        "eterna-forest" to mapOf("en" to "Eterna Forest", "fr" to "Forêt Vestigion", "de" to "Ewigwald"),
        "frost-cavern" to mapOf("en" to "Frost Cavern", "es" to "Gruta Helada", "fr" to "Caverne Gelée", "de" to "Frosthöhle", "it" to "Caverna Gelata", "ja" to "フロストケイブ", "ko" to "프로스트케이브"),
        "kalos-route-13" to mapOf("en" to "Route 13", "es" to "Ruta 13", "fr" to "Route 13", "de" to "Route 13", "it" to "Percorso 13", "ja" to "１３番道路", "ko" to "13번도로"),
        "kalos-route-20" to mapOf("en" to "Route 20", "es" to "Ruta 20", "fr" to "Route 20", "de" to "Route 20", "it" to "Percorso 20", "ja" to "２０番道路", "ko" to "20번도로"),
        "lush-jungle" to mapOf("en" to "Lush Jungle", "es" to "Jungla Umbría", "fr" to "Jungle Sombrefeuille", "de" to "Schattendschungel", "it" to "Giungla Ombrosa", "ja" to "シェードジャングル", "ko" to "셰이드정글"),
        "mount-lanakila" to mapOf("en" to "Mount Lanakila", "es" to "Monte Lanakila", "fr" to "Mont Lanakila", "de" to "Mount Lanakila", "it" to "Monte Lanakila", "ja" to "ラナキラマウンテン", "ko" to "라나키라마운틴"),
        "mt-coronet" to mapOf("en" to "Mt. Coronet", "fr" to "Mont Couronné", "de" to "Kraterberg"),
        "new-mauville" to mapOf("en" to "New Mauville", "es" to "Malvalanova", "fr" to "New Lavandia", "de" to "Neu Malvenfroh", "it" to "Ciclanova", "ja" to "ニューキンセツ", "ko" to "뉴보라"),
        "petalburg-woods" to mapOf("en" to "Petalburg Woods", "es" to "Bosque Petalia", "fr" to "Bois Clémenti", "de" to "Blütenburgwald", "it" to "Bosco Petalo", "ja" to "トウカの森", "ko" to "등화숲"),
        "pinwheel-forest" to mapOf("en" to "Pinwheel Forest", "fr" to "Forêt d'Empoigne", "de" to "Ewigenwald"),
        "shoal-cave" to mapOf("en" to "Shoal Cave", "es" to "Cueva Cardumen", "fr" to "Grotte Tréfonds", "de" to "Küstenhöhle", "it" to "Grotta Ondosa", "ja" to "浅瀬の洞穴", "ko" to "여울의 동굴"),
        "sinnoh-route-217" to mapOf("en" to "Route 217", "fr" to "Route 217", "de" to "Route 217"),
        "twist-mountain" to mapOf("en" to "Twist Mountain", "fr" to "Mont Foré", "de" to "Wendelberg"),
        "vast-poni-canyon" to mapOf("en" to "Vast Poni Canyon", "es" to "Cañón de Poni", "fr" to "Grand Canyon de Poni", "de" to "Canyon von Poni", "it" to "Canyon di Poni", "ja" to "ポニの大峡谷", "ko" to "포니대협곡"),
    )

    private val species: Map<String, Map<String, String>> = mapOf(
        "karrablast" to mapOf("en" to "Karrablast", "es" to "Karrablast", "fr" to "Carabing", "de" to "Laukaps", "it" to "Karrablast", "ja" to "カブルモ", "ko" to "딱정곤"),
        "remoraid" to mapOf("en" to "Remoraid", "es" to "Remoraid", "fr" to "Rémoraid", "de" to "Remoraid", "it" to "Remoraid", "ja" to "テッポウオ", "ko" to "총어"),
        "shelmet" to mapOf("en" to "Shelmet", "es" to "Shelmet", "fr" to "Escargaume", "de" to "Schnuthelm", "it" to "Shelmet", "ja" to "チョボマキ", "ko" to "쪼마리"),
    )

    private fun of(table: Map<String, Map<String, String>>, slug: String, language: String): String {
        val perLanguage = table[slug] ?: return prettifySlug(slug)
        // Latin American Spanish shares PokeAPI's Spanish, which has no regional split.
        val key = if (language == "es-419") "es" else language
        return perLanguage[key] ?: perLanguage["en"] ?: prettifySlug(slug)
    }

    fun item(slug: String, language: String): String = of(items, slug, language)

    fun move(slug: String, language: String): String = of(moves, slug, language)

    fun location(slug: String, language: String): String = of(locations, slug, language)

    fun species(slug: String, language: String): String = of(species, slug, language)
}
