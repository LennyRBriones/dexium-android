package com.anvorgueso.dexium.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anvorgueso.dexium.core.database.entity.PokemonDetailEntity

@Dao
interface PokemonDetailDao {

    @Query("SELECT * FROM pokemon_detail WHERE id = :id")
    suspend fun getById(id: Int): PokemonDetailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(detail: PokemonDetailEntity)

    @Query("DELETE FROM pokemon_detail")
    suspend fun deleteAll()
}
