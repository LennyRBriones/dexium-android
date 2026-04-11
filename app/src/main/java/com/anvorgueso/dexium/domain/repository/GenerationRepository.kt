package com.anvorgueso.dexium.domain.repository

import com.anvorgueso.dexium.core.util.Resource
import com.anvorgueso.dexium.domain.model.Generation
import kotlinx.coroutines.flow.Flow

interface GenerationRepository {
    fun getGenerations(): Flow<Resource<List<Generation>>>
    suspend fun refreshGenerations()
}
