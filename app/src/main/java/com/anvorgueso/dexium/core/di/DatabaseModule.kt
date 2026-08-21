package com.anvorgueso.dexium.core.di

import android.content.Context
import androidx.room.Room
import com.anvorgueso.dexium.core.database.DexiumDatabase
import com.anvorgueso.dexium.core.database.MIGRATION_5_6
import com.anvorgueso.dexium.core.database.MIGRATION_6_7
import com.anvorgueso.dexium.core.database.dao.GenerationDao
import com.anvorgueso.dexium.core.database.dao.HighScoreDao
import com.anvorgueso.dexium.core.database.dao.PokemonDao
import com.anvorgueso.dexium.core.database.dao.PokemonDetailDao
import com.anvorgueso.dexium.core.database.dao.RegionDao
import com.anvorgueso.dexium.core.database.dao.TeamDao
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
        )
            // Runs instead of the destructive fallback when the stored version is 5, so
            // existing installs gain the team table without losing their cached dex.
            .addMigrations(MIGRATION_5_6, MIGRATION_6_7)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideTeamDao(database: DexiumDatabase): TeamDao {
        return database.teamDao()
    }

    @Provides
    fun provideHighScoreDao(database: DexiumDatabase): HighScoreDao {
        return database.highScoreDao()
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
