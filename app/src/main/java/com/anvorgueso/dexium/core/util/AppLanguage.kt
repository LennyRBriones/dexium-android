package com.anvorgueso.dexium.core.util

import java.util.Locale

/**
 * A language the app can show PokeAPI content in.
 *
 * Only languages that actually carry data are listed. PokeAPI advertises fourteen, but `cs`
 * and `pt-br` have no flavor text or genera at all, and `ja-roma` has names only — offering
 * them would label English content with someone else's language.
 *
 * Labels are endonyms, since a language picker is read by the person who speaks it.
 */
enum class AppLanguage(
    /** The `language.name` value PokeAPI matches on. */
    val code: String,
    val label: String
) {
    ENGLISH("en", "English"),
    SPANISH("es", "Español"),
    SPANISH_LATAM("es-419", "Español (Latinoamérica)"),
    FRENCH("fr", "Français"),
    GERMAN("de", "Deutsch"),
    ITALIAN("it", "Italiano"),
    JAPANESE("ja", "日本語 (漢字)"),
    JAPANESE_KANA("ja-hrkt", "日本語 (かな)"),
    KOREAN("ko", "한국어"),
    CHINESE_SIMPLIFIED("zh-hans", "简体中文"),
    CHINESE_TRADITIONAL("zh-hant", "繁體中文");

    companion object {
        val DEFAULT = ENGLISH

        fun fromCode(code: String?): AppLanguage? =
            entries.firstOrNull { it.code.equals(code, ignoreCase = true) }

        /**
         * Best PokeAPI language for the device's current locale, used until the user picks one.
         * Chinese needs the script to choose between Hans and Hant; everything unsupported
         * lands on English rather than showing empty fields.
         */
        fun fromDeviceLocale(locale: Locale = Locale.getDefault()): AppLanguage {
            val language = locale.language.lowercase()
            val script = locale.script.lowercase()
            val country = locale.country.uppercase()

            return when (language) {
                "es" -> if (country in LATAM_COUNTRIES) SPANISH_LATAM else SPANISH
                "fr" -> FRENCH
                "de" -> GERMAN
                "it" -> ITALIAN
                "ja" -> JAPANESE
                "ko" -> KOREAN
                "zh" -> when {
                    script == "hant" -> CHINESE_TRADITIONAL
                    country in TRADITIONAL_CHINESE_REGIONS -> CHINESE_TRADITIONAL
                    else -> CHINESE_SIMPLIFIED
                }
                else -> ENGLISH
            }
        }

        private val LATAM_COUNTRIES = setOf(
            "MX", "AR", "CO", "CL", "PE", "VE", "EC", "GT", "CU", "BO", "DO", "HN",
            "PY", "SV", "NI", "CR", "PA", "UY", "PR"
        )

        private val TRADITIONAL_CHINESE_REGIONS = setOf("TW", "HK", "MO")
    }
}
