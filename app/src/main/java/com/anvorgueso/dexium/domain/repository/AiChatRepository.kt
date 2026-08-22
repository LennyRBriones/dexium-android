package com.anvorgueso.dexium.domain.repository

import com.anvorgueso.dexium.core.util.Resource
import com.anvorgueso.dexium.domain.model.Pokemon

interface AiChatRepository {
    suspend fun identifyPokemon(userDescription: String): Resource<List<Pokemon>>
}
