package com.anvorgueso.dexium.core.network.api

import com.anvorgueso.dexium.core.network.dto.EvolutionChainDto
import retrofit2.http.GET
import retrofit2.http.Path

interface EvolutionApiService {

    @GET("evolution-chain/{id}")
    suspend fun getEvolutionChain(
        @Path("id") id: Int
    ): EvolutionChainDto
}
