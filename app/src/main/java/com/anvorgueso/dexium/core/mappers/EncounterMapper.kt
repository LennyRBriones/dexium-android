package com.anvorgueso.dexium.core.mappers

import com.anvorgueso.dexium.core.network.dto.LocationAreaEncounterDto
import com.anvorgueso.dexium.core.util.GameCatalog
import com.anvorgueso.dexium.core.util.Translations
import com.anvorgueso.dexium.core.util.prettifySlug
import com.anvorgueso.dexium.domain.model.EncounterLocation
import com.anvorgueso.dexium.domain.model.GameEncounters

object EncounterMapper {

    /**
     * PokeAPI returns this data area-first (each area lists the games it applies to), but the
     * UI needs it game-first, so this inverts the nesting. One area can also list the same
     * method several times with different conditions — e.g. Pikachu in Trophy Garden appears
     * three times for Diamond — so entries are collapsed per (location, method), keeping the
     * widest level range and the highest chance.
     */
    fun List<LocationAreaEncounterDto>.toGameEncounters(locale: String): List<GameEncounters> {
        val byVersion = mutableMapOf<String, MutableMap<Pair<String, String>, EncounterLocation>>()

        for (area in this) {
            val locationName = prettifySlug(area.locationArea.name)
            for (versionDetail in area.versionDetails) {
                if (versionDetail.encounterDetails.isEmpty()) continue
                val slotsForVersion = byVersion.getOrPut(versionDetail.version.name) { mutableMapOf() }

                for ((methodSlug, details) in versionDetail.encounterDetails.groupBy { it.method.name }) {
                    val method = Translations.translateEncounterMethod(methodSlug, locale)
                    val key = locationName to method
                    val existing = slotsForVersion[key]
                    val merged = EncounterLocation(
                        locationName = locationName,
                        method = method,
                        minLevel = minOf(
                            details.minOf { it.minLevel },
                            existing?.minLevel ?: Int.MAX_VALUE
                        ),
                        maxLevel = maxOf(
                            details.maxOf { it.maxLevel },
                            existing?.maxLevel ?: 0
                        ),
                        chance = maxOf(
                            details.sumOf { it.chance }.coerceAtMost(100),
                            existing?.chance ?: 0
                        )
                    )
                    slotsForVersion[key] = merged
                }
            }
        }

        return byVersion
            .map { (slug, slots) ->
                GameEncounters(
                    versionSlug = slug,
                    gameName = GameCatalog.displayName(slug, locale),
                    locations = slots.values.sortedWith(
                        compareByDescending<EncounterLocation> { it.chance }
                            .thenBy { it.locationName }
                    )
                )
            }
            .sortedBy { GameCatalog.sortOrder(it.versionSlug) }
    }
}
