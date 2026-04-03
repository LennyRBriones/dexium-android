package com.anvorgueso.dexium.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon_detail")
data class PokemonDetailEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val baseExperience: Int,
    val spriteUrl: String?,
    val hdSpriteUrl: String?,
    val animatedSpriteUrl: String?,
    val shinySpriteUrl: String?,
    val shinyAnimatedSpriteUrl: String?,
    val typePrimary: String,
    val typeSecondary: String?,
    val statsJson: String,
    val abilitiesJson: String,
    val description: String,
    val genus: String,
    val color: String,
    val habitat: String?,
    val isLegendary: Boolean,
    val isMythical: Boolean,
    val evolutionChainJson: String,
    val generationId: Int
)
