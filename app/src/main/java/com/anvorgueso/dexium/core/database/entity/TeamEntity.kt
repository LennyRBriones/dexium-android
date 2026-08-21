package com.anvorgueso.dexium.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A user-built team. Unlike the other entities, the id is generated: every other table keys
 * off a PokeAPI id, but teams have no natural key.
 *
 * Members are stored as a JSON array of Pokémon ids, matching the `...Json: String` idiom the
 * other entities use for collections, so no new TypeConverter is needed.
 */
@Entity(tableName = "team")
data class TeamEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val memberIdsJson: String,
    val createdAt: Long
)
