package com.anvorgueso.dexium.domain.repository

import com.anvorgueso.dexium.core.util.Resource
import com.anvorgueso.dexium.domain.model.DexCategory
import com.anvorgueso.dexium.domain.model.Pokemon
import com.anvorgueso.dexium.domain.model.PokemonDetail
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {
    fun getPokemonList(page: Int, pageSize: Int, category: DexCategory = DexCategory.NATIONAL): Flow<Resource<List<Pokemon>>>
    fun getPokemonDetail(id: Int): Flow<Resource<PokemonDetail>>
    fun searchPokemon(query: String, category: DexCategory = DexCategory.NATIONAL, generationId: Int? = null, formRegion: String? = null): Flow<Resource<List<Pokemon>>>
    fun getPokemonByGeneration(generationId: Int, category: DexCategory = DexCategory.NATIONAL): Flow<Resource<List<Pokemon>>>
    fun getPokemonByFormRegion(formRegion: String?): Flow<Resource<List<Pokemon>>>
    fun getAllByCategory(category: DexCategory): Flow<Resource<List<Pokemon>>>
    suspend fun precachePokemonNames()
    suspend fun syncAllPokemon(onProgress: (Int, Int) -> Unit = { _, _ -> })
    suspend fun refreshPokemonData()
    suspend fun getLocalPokemonCount(): Int
    suspend fun getTotalPokemonCount(): Int
}
