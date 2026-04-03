package com.anvorgueso.dexium.core.network.api

import com.anvorgueso.dexium.core.network.dto.AbilityDetailDto
import retrofit2.http.GET
import retrofit2.http.Path

interface AbilityApiService {

    @GET("ability/{name}")
    suspend fun getAbility(
        @Path("name") name: String
    ): AbilityDetailDto
}
