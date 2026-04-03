package com.anvorgueso.dexium.core.mappers

import com.anvorgueso.dexium.core.database.entity.GenerationEntity
import com.anvorgueso.dexium.core.network.dto.GenerationDto
import com.anvorgueso.dexium.core.util.capitalizeFirst
import com.anvorgueso.dexium.core.util.extractIdFromUrl
import com.anvorgueso.dexium.domain.model.Generation
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object GenerationMapper {

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val intListAdapter = moshi.adapter<List<Int>>(
        Types.newParameterizedType(List::class.java, Integer::class.java)
    )

    fun GenerationDto.toEntity(): GenerationEntity {
        val displayName = names.firstOrNull { it.language.name == "en" }?.name
            ?: name.capitalizeFirst()
        val speciesIds = pokemonSpecies.map { it.url.extractIdFromUrl() }

        return GenerationEntity(
            id = id,
            name = name,
            displayName = displayName,
            regionName = mainRegion.name.capitalizeFirst(),
            pokemonSpeciesIdsJson = intListAdapter.toJson(speciesIds),
            sortOrder = id * 10
        )
    }

    fun GenerationEntity.toDomainModel(): Generation {
        val speciesIds = getSpeciesIds(this)

        return Generation(
            id = id,
            name = name,
            displayName = displayName,
            regionName = regionName,
            pokemonCount = speciesIds.size
        )
    }

    fun getSpeciesIds(entity: GenerationEntity): List<Int> {
        return try {
            intListAdapter.fromJson(entity.pokemonSpeciesIdsJson) ?: emptyList()
        } catch (e: Exception) { emptyList() }
    }

    fun toSpeciesJson(ids: List<Int>): String {
        return intListAdapter.toJson(ids)
    }
}
