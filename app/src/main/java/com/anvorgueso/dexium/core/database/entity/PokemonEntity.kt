package com.anvorgueso.dexium.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon")
data class PokemonEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val spriteUrl: String?,
    val hdSpriteUrl: String?,
    val animatedSpriteUrl: String?,
    val typePrimary: String,
    val typeSecondary: String?,
    val generationId: Int,
    val dexCategory: String = "NATIONAL",
    val formRegion: String? = null
)
