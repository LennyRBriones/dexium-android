package com.anvorgueso.dexium.core.di

import android.content.Context
import androidx.room.Room
import com.anvorgueso.dexium.core.database.DexiumDatabase
import com.anvorgueso.dexium.core.database.dao.GenerationDao
import com.anvorgueso.dexium.core.database.dao.PokemonDao
import com.anvorgueso.dexium.core.database.dao.PokemonDetailDao
import com.anvorgueso.dexium.core.database.dao.RegionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DexiumDatabase {
        return Room.databaseBuilder(
            context,
            DexiumDatabase::class.java,
            "dexium_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun providePokemonDao(database: DexiumDatabase): PokemonDao {
        return database.pokemonDao()
    }

    @Provides
    fun providePokemonDetailDao(database: DexiumDatabase): PokemonDetailDao {
        return database.pokemonDetailDao()
    }

    @Provides
    fun provideGenerationDao(database: DexiumDatabase): GenerationDao {
        return database.generationDao()
    }

    @Provides
    fun provideRegionDao(database: DexiumDatabase): RegionDao {
        return database.regionDao()
    }
}
