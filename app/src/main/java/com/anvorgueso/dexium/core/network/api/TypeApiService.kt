package com.anvorgueso.dexium.core.network.api

import com.anvorgueso.dexium.core.network.dto.TypeDto
import retrofit2.http.GET
import retrofit2.http.Path

interface TypeApiService {

    @GET("type/{id}")
    suspend fun getType(
        @Path("id") id: Int
    ): TypeDto
}
