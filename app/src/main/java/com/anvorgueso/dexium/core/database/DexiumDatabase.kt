package com.anvorgueso.dexium.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.anvorgueso.dexium.core.database.converter.Converters
import com.anvorgueso.dexium.core.database.dao.GenerationDao
import com.anvorgueso.dexium.core.database.dao.PokemonDao
import com.anvorgueso.dexium.core.database.dao.PokemonDetailDao
import com.anvorgueso.dexium.core.database.dao.RegionDao
import com.anvorgueso.dexium.core.database.entity.GenerationEntity
import com.anvorgueso.dexium.core.database.entity.PokemonDetailEntity
import com.anvorgueso.dexium.core.database.entity.PokemonEntity
import com.anvorgueso.dexium.core.database.entity.RegionEntity

@Database(
    entities = [
        PokemonEntity::class,
        PokemonDetailEntity::class,
        GenerationEntity::class,
        RegionEntity::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class DexiumDatabase : RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao
    abstract fun pokemonDetailDao(): PokemonDetailDao
    abstract fun generationDao(): GenerationDao
    abstract fun regionDao(): RegionDao
}
