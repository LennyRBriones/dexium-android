package com.anvorgueso.dexium.domain.repository

import com.anvorgueso.dexium.core.util.Resource
import com.anvorgueso.dexium.domain.model.DexCategory
import com.anvorgueso.dexium.domain.model.GameEncounters
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

    /** Re-reads already-displayed rows from the cache, e.g. after the type backfill lands. */
    suspend fun getPokemonByIds(ids: List<Int>): List<Pokemon>

    /** Where this Pokémon can be caught, grouped by game. Empty when PokeAPI has no data. */
    suspend fun getPokemonEncounters(id: Int): Resource<List<GameEncounters>>
    suspend fun syncAllPokemon(onProgress: (Int, Int) -> Unit = { _, _ -> })
    suspend fun refreshPokemonData()

    /**
     * Drops cached details. Needed when the language changes: descriptions, genera, type and
     * ability names are stored already translated, so old rows would keep the old language.
     */
    suspend fun clearDetailCache()
    suspend fun getLocalPokemonCount(): Int
    suspend fun getTotalPokemonCount(): Int
}
