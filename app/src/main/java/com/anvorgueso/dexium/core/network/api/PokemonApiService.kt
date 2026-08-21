package com.anvorgueso.dexium.core.network.api

import com.anvorgueso.dexium.core.network.dto.LocationAreaEncounterDto
import com.anvorgueso.dexium.core.network.dto.PaginatedResponse
import com.anvorgueso.dexium.core.network.dto.PokemonDetailDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokemonApiService {

    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): PaginatedResponse

    @GET("pokemon/{id}")
    suspend fun getPokemonDetail(
        @Path("id") id: Int
    ): PokemonDetailDto

    @GET("pokemon/{name}")
    suspend fun getPokemonByName(
        @Path("name") name: String
    ): PokemonDetailDto

    @GET("pokemon/{id}/encounters")
    suspend fun getPokemonEncounters(
        @Path("id") id: Int
    ): List<LocationAreaEncounterDto>
}
