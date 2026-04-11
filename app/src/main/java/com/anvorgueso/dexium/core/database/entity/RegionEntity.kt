package com.anvorgueso.dexium.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "region")
data class RegionEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val displayName: String,
    val generationName: String
)
