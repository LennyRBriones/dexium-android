package com.anvorgueso.dexium.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "generation")
data class GenerationEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val displayName: String,
    val regionName: String,
    val pokemonSpeciesIdsJson: String,
    val sortOrder: Int = 0
)
