package com.anvorgueso.dexium.core.util

/**
 * Identity of a guess-game mode, used as the high-score key.
 *
 * The game receives its mode as the raw `generationIds` nav argument, where a custom
 * combination arrives in whatever order the user tapped the generations. Normalizing sorts and
 * de-duplicates the ids so "3,1" and "1,3,3" both resolve to the same mode.
 */
object GameMode {
    const val ALL = "all"

    fun normalize(raw: String): String {
        if (raw.isBlank() || raw == ALL) return ALL
        val ids = raw.split(",")
            .mapNotNull { it.trim().toIntOrNull() }
            .distinct()
            .sorted()
        return if (ids.isEmpty()) ALL else ids.joinToString(",")
    }
}
