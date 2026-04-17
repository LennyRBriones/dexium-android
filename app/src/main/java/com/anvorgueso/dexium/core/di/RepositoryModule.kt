package com.anvorgueso.dexium.core.di

import com.anvorgueso.dexium.core.repository.AiChatRepositoryImpl
import com.anvorgueso.dexium.core.repository.GenerationRepositoryImpl
import com.anvorgueso.dexium.core.repository.PokemonRepositoryImpl
import com.anvorgueso.dexium.core.repository.UserPreferencesRepositoryImpl
import com.anvorgueso.dexium.domain.repository.AiChatRepository
import com.anvorgueso.dexium.domain.repository.GenerationRepository
import com.anvorgueso.dexium.domain.repository.PokemonRepository
import com.anvorgueso.dexium.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPokemonRepository(
        impl: PokemonRepositoryImpl
    ): PokemonRepository

    @Binds
    @Singleton
    abstract fun bindGenerationRepository(
        impl: GenerationRepositoryImpl
    ): GenerationRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        impl: UserPreferencesRepositoryImpl
    ): UserPreferencesRepository

    @Binds
    @Singleton
    abstract fun bindAiChatRepository(
        impl: AiChatRepositoryImpl
    ): AiChatRepository
}
