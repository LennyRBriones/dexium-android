package com.anvorgueso.dexium.domain.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.anvorgueso.dexium.R

enum class DexCategory(
    @StringRes val displayNameRes: Int,
    val accentColor: Color
) {
    NATIONAL(R.string.dex_national, Color(0xFF4FC3F7)),
    SHINY(R.string.dex_shiny, Color(0xFFBA68C8)),
    MEGA(R.string.dex_mega, Color(0xFFFF6F61)),
    GIGANTAMAX(R.string.dex_gigantamax, Color(0xFFFFD54F)),
    FORMS(R.string.dex_forms, Color(0xFF81C784));

    /**
     * SHINY is a lens over the national dex rather than its own partition — no row is ever
     * stored with `dexCategory = "SHINY"` (see [classify]), so queries must run against this
     * category instead of the selected one, and the mapper swaps in the shiny sprite.
     */
    val queryCategory: DexCategory
        get() = if (this == SHINY) NATIONAL else this

    val isShiny: Boolean
        get() = this == SHINY

    /** Dexes big enough to page through instead of loading in one shot. */
    val isPaginated: Boolean
        get() = this == NATIONAL || this == SHINY

    companion object {
        /** Never returns SHINY: it is a view over NATIONAL, not a classification of a row. */
        fun classify(id: Int, name: String): DexCategory {
            val lower = name.lowercase()
            return when {
                lower.contains("-mega") -> MEGA
                lower.contains("-gmax") -> GIGANTAMAX
                id <= 1025 -> NATIONAL
                else -> FORMS
            }
        }

        fun extractFormRegion(name: String): String? {
            val lower = name.lowercase()
            return when {
                lower.contains("-alola") -> "alola"
                lower.contains("-galar") -> "galar"
                lower.contains("-hisui") -> "hisui"
                lower.contains("-paldea") -> "paldea"
                else -> null
            }
        }

        val FORM_REGIONS = listOf("alola", "galar", "hisui", "paldea")
    }
}
