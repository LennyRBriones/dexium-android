package com.anvorgueso.dexium.core.network.api

import com.anvorgueso.dexium.core.network.dto.GenerationDto
import com.anvorgueso.dexium.core.network.dto.PaginatedResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface GenerationApiService {

    @GET("generation")
    suspend fun getGenerationList(): PaginatedResponse

    @GET("generation/{id}")
    suspend fun getGeneration(
        @Path("id") id: Int
    ): GenerationDto
}
