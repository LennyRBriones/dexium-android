package com.anvorgueso.dexium.domain.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.anvorgueso.dexium.R

enum class DexCategory(
    @StringRes val displayNameRes: Int,
    val accentColor: Color
) {
    NATIONAL(R.string.dex_national, Color(0xFF4FC3F7)),
    MEGA(R.string.dex_mega, Color(0xFFFF6F61)),
    GIGANTAMAX(R.string.dex_gigantamax, Color(0xFFFFD54F)),
    FORMS(R.string.dex_forms, Color(0xFF81C784));

    companion object {
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
