package com.anvorgueso.dexium.core.network.api

import com.anvorgueso.dexium.core.network.dto.PaginatedResponse
import com.anvorgueso.dexium.core.network.dto.RegionDto
import retrofit2.http.GET
import retrofit2.http.Path

interface RegionApiService {

    @GET("region")
    suspend fun getRegionList(): PaginatedResponse

    @GET("region/{id}")
    suspend fun getRegion(
        @Path("id") id: Int
    ): RegionDto
}
