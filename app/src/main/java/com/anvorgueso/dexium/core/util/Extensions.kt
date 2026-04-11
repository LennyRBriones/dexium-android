package com.anvorgueso.dexium.core.util

import java.util.Locale

fun String.capitalizeFirst(): String {
    return replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
    }
}

fun String.extractIdFromUrl(): Int {
    return trimEnd('/').substringAfterLast('/').toIntOrNull() ?: 0
}

fun Int.formatPokemonId(): String {
    return "#${toString().padStart(3, '0')}"
}

fun Int.toHeightString(imperial: Boolean = false): String {
    return if (imperial) {
        val totalInches = this * 3.937
        val feet = (totalInches / 12).toInt()
        val inches = (totalInches % 12).toInt()
        "${feet}'${inches}\""
    } else {
        val meters = this / 10.0
        String.format(Locale.US, "%.1f m", meters)
    }
}

fun Int.toWeightString(imperial: Boolean = false): String {
    return if (imperial) {
        val lbs = this * 0.2205
        String.format(Locale.US, "%.1f lbs", lbs)
    } else {
        val kg = this / 10.0
        String.format(Locale.US, "%.1f kg", kg)
    }
}

fun String.cleanFlavorText(): String {
    return replace("\n", " ")
        .replace("\u000c", " ")
        .replace("\\s+".toRegex(), " ")
        .trim()
}
