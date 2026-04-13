package com.anvorgueso.dexium.core.mappers

import com.anvorgueso.dexium.core.database.entity.PokemonDetailEntity
import com.anvorgueso.dexium.core.database.entity.PokemonEntity
import com.anvorgueso.dexium.core.network.dto.PokemonDetailDto
import com.anvorgueso.dexium.domain.model.DexCategory
import com.anvorgueso.dexium.core.network.dto.PokemonSpeciesDto
import com.anvorgueso.dexium.core.util.Translations
import com.anvorgueso.dexium.core.util.capitalizeFirst
import com.anvorgueso.dexium.core.util.cleanFlavorText
import com.anvorgueso.dexium.core.util.extractIdFromUrl
import com.anvorgueso.dexium.domain.model.Ability
import com.anvorgueso.dexium.domain.model.EvolutionStage
import com.anvorgueso.dexium.domain.model.Pokemon
import com.anvorgueso.dexium.domain.model.PokemonDetail
import com.anvorgueso.dexium.domain.model.Stat
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object PokemonMapper {

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    private val statListAdapter = moshi.adapter<List<StatJson>>(
        Types.newParameterizedType(List::class.java, StatJson::class.java)
    )
    private val abilityListAdapter = moshi.adapter<List<AbilityJson>>(
        Types.newParameterizedType(List::class.java, AbilityJson::class.java)
    )
    private val evolutionListAdapter = moshi.adapter<List<EvolutionJson>>(
        Types.newParameterizedType(List::class.java, EvolutionJson::class.java)
    )

    data class StatJson(val name: String, val baseStat: Int)
    data class AbilityJson(val name: String, val isHidden: Boolean)
    data class EvolutionJson(
        val pokemonId: Int,
        val pokemonName: String,
        val isBaby: Boolean,
        val minLevel: Int?,
        val trigger: String?
    )

    fun PokemonDetailDto.toPokemonEntity(generationId: Int = 0): PokemonEntity {
        val category = DexCategory.classify(id, name)
        val region = DexCategory.extractFormRegion(name)
        return PokemonEntity(
            id = id,
            name = name.capitalizeFirst(),
            spriteUrl = sprites.frontDefault,
            hdSpriteUrl = sprites.other?.officialArtwork?.frontDefault
                ?: sprites.other?.home?.frontDefault,
            animatedSpriteUrl = sprites.other?.showdown?.frontDefault
                ?: buildAnimatedSpriteUrl(id),
            typePrimary = types.firstOrNull()?.type?.name?.capitalizeFirst() ?: "Unknown",
            typeSecondary = types.getOrNull(1)?.type?.name?.capitalizeFirst(),
            generationId = generationId,
            dexCategory = category.name,
            formRegion = region
        )
    }

    fun PokemonDetailDto.toPokemonDetailEntity(
        species: PokemonSpeciesDto,
        evolutionStages: List<EvolutionStage>,
        locale: String = "en",
        translatedAbilities: Map<String, String> = emptyMap()
    ): PokemonDetailEntity {
        val flavorText = (species.flavorTextEntries
            .lastOrNull { it.language.name == locale }
            ?: species.flavorTextEntries.lastOrNull { it.language.name == "en" })
            ?.flavorText?.cleanFlavorText() ?: ""

        val genus = (species.genera
            .firstOrNull { it.language.name == locale }
            ?: species.genera.firstOrNull { it.language.name == "en" })
            ?.genus ?: ""

        val statsJson = statListAdapter.toJson(
            stats.map { StatJson(it.stat.name, it.baseStat) }
        )

        val abilitiesJson = abilityListAdapter.toJson(
            abilities.map { slot ->
                val translatedName = translatedAbilities[slot.ability.name]
                    ?: slot.ability.name
                AbilityJson(translatedName, slot.isHidden)
            }
        )

        val evolutionJson = evolutionListAdapter.toJson(
            evolutionStages.map {
                EvolutionJson(it.pokemonId, it.pokemonName, it.isBaby, it.minLevel, it.trigger)
            }
        )

        return PokemonDetailEntity(
            id = id,
            name = name.capitalizeFirst(),
            height = height,
            weight = weight,
            baseExperience = baseExperience ?: 0,
            spriteUrl = sprites.frontDefault,
            hdSpriteUrl = sprites.other?.officialArtwork?.frontDefault
                ?: sprites.other?.home?.frontDefault,
            animatedSpriteUrl = sprites.other?.showdown?.frontDefault
                ?: buildAnimatedSpriteUrl(id),
            shinySpriteUrl = sprites.other?.officialArtwork?.frontShiny
                ?: sprites.other?.home?.frontShiny
                ?: sprites.frontShiny,
            shinyAnimatedSpriteUrl = sprites.other?.showdown?.frontShiny
                ?: buildShinyAnimatedSpriteUrl(id),
            typePrimary = Translations.translateType(
                types.firstOrNull()?.type?.name?.capitalizeFirst() ?: "Unknown", locale
            ),
            typeSecondary = types.getOrNull(1)?.type?.name?.capitalizeFirst()?.let {
                Translations.translateType(it, locale)
            },
            statsJson = statsJson,
            abilitiesJson = abilitiesJson,
            description = flavorText,
            genus = genus,
            color = species.color.name.capitalizeFirst(),
            habitat = species.habitat?.name?.capitalizeFirst()?.let {
                Translations.translateHabitat(it, locale)
            },
            isLegendary = species.isLegendary,
            isMythical = species.isMythical,
            evolutionChainJson = evolutionJson,
            generationId = species.generation.url.extractIdFromUrl()
        )
    }

    fun PokemonEntity.toDomainModel(useHdImages: Boolean): Pokemon {
        return Pokemon(
            id = id,
            name = name,
            imageUrl = hdSpriteUrl ?: spriteUrl ?: "",
            animatedImageUrl = if (useHdImages) animatedSpriteUrl else null,
            typePrimary = typePrimary,
            typeSecondary = typeSecondary
        )
    }

    fun PokemonDetailEntity.toDomainModel(useHdImages: Boolean): PokemonDetail {
        val stats = try {
            statListAdapter.fromJson(statsJson)?.map { Stat(it.name, it.baseStat) } ?: emptyList()
        } catch (e: Exception) { emptyList() }

        val abilities = try {
            abilityListAdapter.fromJson(abilitiesJson)?.map { Ability(it.name.capitalizeFirst(), it.isHidden) } ?: emptyList()
        } catch (e: Exception) { emptyList() }

        val evolution = try {
            evolutionListAdapter.fromJson(evolutionChainJson)?.map {
                EvolutionStage(
                    pokemonId = it.pokemonId,
                    pokemonName = it.pokemonName.capitalizeFirst(),
                    imageUrl = buildSpriteUrl(it.pokemonId, useHdImages),
                    isBaby = it.isBaby,
                    minLevel = it.minLevel,
                    trigger = it.trigger
                )
            } ?: emptyList()
        } catch (e: Exception) { emptyList() }

        val types = mutableListOf(typePrimary).apply {
            typeSecondary?.let { add(it) }
        }

        return PokemonDetail(
            id = id,
            name = name,
            imageUrl = hdSpriteUrl ?: spriteUrl ?: "",
            animatedImageUrl = if (useHdImages) animatedSpriteUrl else null,
            shinySpriteUrl = shinySpriteUrl,
            shinyAnimatedSpriteUrl = if (useHdImages) shinyAnimatedSpriteUrl else null,
            height = height,
            weight = weight,
            baseExperience = baseExperience,
            stats = stats,
            types = types,
            abilities = abilities,
            description = description,
            genus = genus,
            color = color,
            habitat = habitat,
            isLegendary = isLegendary,
            isMythical = isMythical,
            evolutionChain = evolution
        )
    }

    private fun buildSpriteUrl(pokemonId: Int, useHd: Boolean): String {
        return if (useHd) {
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$pokemonId.png"
        } else {
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$pokemonId.png"
        }
    }

    private fun buildAnimatedSpriteUrl(pokemonId: Int): String {
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/showdown/$pokemonId.gif"
    }

    private fun buildShinyAnimatedSpriteUrl(pokemonId: Int): String {
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/showdown/shiny/$pokemonId.gif"
    }
}
