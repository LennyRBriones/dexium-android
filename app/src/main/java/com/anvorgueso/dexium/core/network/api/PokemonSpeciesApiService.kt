package com.anvorgueso.dexium.core.network.api

import com.anvorgueso.dexium.core.network.dto.PokemonSpeciesDto
import retrofit2.http.GET
import retrofit2.http.Path

interface PokemonSpeciesApiService {

    @GET("pokemon-species/{id}")
    suspend fun getPokemonSpecies(
        @Path("id") id: Int
    ): PokemonSpeciesDto
}
