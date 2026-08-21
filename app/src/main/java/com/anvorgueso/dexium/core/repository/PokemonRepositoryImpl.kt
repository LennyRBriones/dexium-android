package com.anvorgueso.dexium.core.repository

import com.anvorgueso.dexium.core.database.dao.GenerationDao
import com.anvorgueso.dexium.core.database.dao.PokemonDao
import com.anvorgueso.dexium.core.database.dao.PokemonDetailDao
import com.anvorgueso.dexium.core.mappers.EncounterMapper.toGameEncounters
import com.anvorgueso.dexium.core.mappers.EvolutionMapper.toEvolutionStages
import com.anvorgueso.dexium.core.mappers.PokemonMapper.toDomainModel
import com.anvorgueso.dexium.core.mappers.PokemonMapper.toPokemonDetailEntity
import com.anvorgueso.dexium.core.mappers.PokemonMapper.toPokemonEntity
import com.anvorgueso.dexium.core.network.api.EvolutionApiService
import com.anvorgueso.dexium.core.network.api.PokemonApiService
import com.anvorgueso.dexium.core.network.api.PokemonSpeciesApiService
import com.anvorgueso.dexium.core.network.api.TypeApiService
import com.anvorgueso.dexium.core.database.entity.PokemonEntity
import com.anvorgueso.dexium.core.util.Constants
import com.anvorgueso.dexium.domain.model.DexCategory
import com.anvorgueso.dexium.domain.model.GameEncounters
import com.anvorgueso.dexium.core.util.NetworkConnectivityHelper
import com.anvorgueso.dexium.core.util.Resource
import com.anvorgueso.dexium.core.util.capitalizeFirst
import com.anvorgueso.dexium.core.util.extractIdFromUrl
import com.anvorgueso.dexium.domain.model.Pokemon
import com.anvorgueso.dexium.domain.model.PokemonDetail
import com.anvorgueso.dexium.domain.repository.PokemonRepository
import com.anvorgueso.dexium.domain.repository.UserPreferencesRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PokemonRepositoryImpl @Inject constructor(
    private val pokemonApi: PokemonApiService,
    private val speciesApi: PokemonSpeciesApiService,
    private val evolutionApi: EvolutionApiService,
    private val abilityApi: com.anvorgueso.dexium.core.network.api.AbilityApiService,
    private val typeApi: TypeApiService,
    private val pokemonDao: PokemonDao,
    private val pokemonDetailDao: PokemonDetailDao,
    private val generationDao: GenerationDao,
    private val networkHelper: NetworkConnectivityHelper,
    private val userPreferencesRepository: UserPreferencesRepository
) : PokemonRepository {

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val intListAdapter = moshi.adapter<List<Int>>(
        Types.newParameterizedType(List::class.java, Integer::class.java)
    )

    private suspend fun buildGenerationLookup(): Map<Int, Int> {
        val lookup = mutableMapOf<Int, Int>()
        val generations = generationDao.getAllSync()
        for (gen in generations) {
            val speciesIds = try {
                intListAdapter.fromJson(gen.pokemonSpeciesIdsJson) ?: emptyList()
            } catch (e: Exception) { emptyList() }
            for (speciesId in speciesIds) {
                lookup[speciesId] = gen.id
            }
        }
        return lookup
    }

    private suspend fun getSpeciesIdsForGeneration(generationId: Int): List<Int> {
        val gen = generationDao.getById(generationId) ?: return emptyList()
        return try {
            intListAdapter.fromJson(gen.pokemonSpeciesIdsJson) ?: emptyList()
        } catch (e: Exception) { emptyList() }
    }

    override fun getPokemonList(page: Int, pageSize: Int, category: DexCategory): Flow<Resource<List<Pokemon>>> = flow {
        emit(Resource.Loading())

        val useHd = userPreferencesRepository.userPreferences.first().useHdImages
        val offset = page * pageSize

        val queryName = category.queryCategory.name
        val localPokemon = pokemonDao.getPaginatedByCategory(queryName, pageSize, offset)
        if (localPokemon.isNotEmpty()) {
            emit(Resource.Success(localPokemon.map { it.toDomainModel(useHd, category.isShiny) }))
            return@flow
        }

        if (networkHelper.isNetworkAvailable()) {
            try {
                val response = pokemonApi.getPokemonList(pageSize, offset)
                val pokemonIds = response.results
                    .map { it.url.extractIdFromUrl() }
                    .filter { it > 0 }

                val genLookup = buildGenerationLookup()

                val entities = coroutineScope {
                    pokemonIds.map { id ->
                        async {
                            try {
                                pokemonApi.getPokemonDetail(id)
                                    .toPokemonEntity(generationId = genLookup[id] ?: 0)
                            } catch (e: Exception) {
                                null
                            }
                        }
                    }.awaitAll().filterNotNull()
                }

                if (entities.isNotEmpty()) {
                    pokemonDao.insertAll(entities)
                    val filtered = entities.filter { it.dexCategory == queryName }
                    emit(Resource.Success(filtered.map { it.toDomainModel(useHd, category.isShiny) }))
                }
            } catch (e: Exception) {
                emit(Resource.Error("Failed to load Pok\u00e9mon: ${e.localizedMessage}"))
            }
        } else {
            emit(Resource.Error("No internet connection and no cached data available"))
        }
    }

    override fun getPokemonDetail(id: Int): Flow<Resource<PokemonDetail>> = flow {
        emit(Resource.Loading())

        val useHd = userPreferencesRepository.userPreferences.first().useHdImages

        val cached = pokemonDetailDao.getById(id)
        if (cached != null) {
            emit(Resource.Success(cached.toDomainModel(useHd)))
        }

        if (networkHelper.isNetworkAvailable()) {
            try {
                val pokemonDetail = pokemonApi.getPokemonDetail(id)
                val speciesId = pokemonDetail.species.url.extractIdFromUrl()
                val species = speciesApi.getPokemonSpecies(speciesId)
                val evolutionChainId = species.evolutionChain?.url?.extractIdFromUrl()
                val evolutionStages = if (evolutionChainId != null && evolutionChainId > 0) {
                    try {
                        val chain = evolutionApi.getEvolutionChain(evolutionChainId)
                        chain.chain.toEvolutionStages(useHd)
                    } catch (e: Exception) {
                        emptyList()
                    }
                } else {
                    emptyList()
                }

                val deviceLocale = java.util.Locale.getDefault().language

                val translatedAbilities = if (deviceLocale != "en") {
                    coroutineScope {
                        pokemonDetail.abilities.associate { slot ->
                            val abilityName = slot.ability.name
                            abilityName to async {
                                try {
                                    val abilityDetail = abilityApi.getAbility(abilityName)
                                    (abilityDetail.names.firstOrNull { it.language.name == deviceLocale }
                                        ?: abilityDetail.names.firstOrNull { it.language.name == "en" })
                                        ?.name ?: abilityName.capitalizeFirst()
                                } catch (e: Exception) {
                                    abilityName.capitalizeFirst()
                                }
                            }
                        }.mapValues { it.value.await() }
                    }
                } else emptyMap()

                val entity = pokemonDetail.toPokemonDetailEntity(species, evolutionStages, deviceLocale, translatedAbilities)
                pokemonDetailDao.insert(entity)
                emit(Resource.Success(entity.toDomainModel(useHd)))
            } catch (e: Exception) {
                if (cached == null) {
                    emit(Resource.Error("Failed to load details: ${e.localizedMessage}"))
                }
            }
        } else if (cached == null) {
            emit(Resource.Error("No internet connection and no cached data"))
        }
    }

    override fun searchPokemon(query: String, category: DexCategory, generationId: Int?, formRegion: String?): Flow<Resource<List<Pokemon>>> = flow {
        emit(Resource.Loading())
        val useHd = userPreferencesRepository.userPreferences.first().useHdImages

        val searchFlow = when {
            category == DexCategory.FORMS && formRegion == "other" -> {
                pokemonDao.searchFormsOtherByName(query)
            }
            category == DexCategory.FORMS -> {
                pokemonDao.searchFormsByNameAndRegion(query, formRegion)
            }
            generationId != null -> {
                pokemonDao.searchByNameCategoryAndGeneration(query, category.queryCategory.name, generationId)
            }
            else -> {
                pokemonDao.searchByNameAndCategory(query, category.queryCategory.name)
            }
        }
        searchFlow.collect { results ->
            emit(Resource.Success(results.map { it.toDomainModel(useHd, category.isShiny) }))
        }
    }

    override fun getPokemonByGeneration(generationId: Int, category: DexCategory): Flow<Resource<List<Pokemon>>> = flow {
        emit(Resource.Loading())
        val useHd = userPreferencesRepository.userPreferences.first().useHdImages

        val localResults = pokemonDao.getByGenerationAndCategory(generationId, category.queryCategory.name)
        if (localResults.isNotEmpty()) {
            emit(Resource.Success(localResults.map { it.toDomainModel(useHd, category.isShiny) }))
            return@flow
        }

        if (networkHelper.isNetworkAvailable()) {
            try {
                val speciesIds = getSpeciesIdsForGeneration(generationId)
                if (speciesIds.isNotEmpty()) {
                    val allEntities = mutableListOf<com.anvorgueso.dexium.core.database.entity.PokemonEntity>()
                    speciesIds.chunked(20).forEach { batch ->
                        val entities = coroutineScope {
                            batch.map { id ->
                                async {
                                    try {
                                        pokemonApi.getPokemonDetail(id)
                                            .toPokemonEntity(generationId = generationId)
                                    } catch (e: Exception) {
                                        null
                                    }
                                }
                            }.awaitAll().filterNotNull()
                        }
                        if (entities.isNotEmpty()) {
                            pokemonDao.insertAll(entities)
                            allEntities.addAll(entities)
                        }
                    }
                    emit(Resource.Success(allEntities.map { it.toDomainModel(useHd, category.isShiny) }))
                } else {
                    emit(Resource.Error("No generation data available. Try refreshing."))
                }
            } catch (e: Exception) {
                emit(Resource.Error("Failed to load generation: ${e.localizedMessage}"))
            }
        } else {
            emit(Resource.Error("No internet connection and no cached data for this generation"))
        }
    }

    override fun getAllByCategory(category: DexCategory): Flow<Resource<List<Pokemon>>> = flow {
        emit(Resource.Loading())
        val useHd = userPreferencesRepository.userPreferences.first().useHdImages
        val results = pokemonDao.getAllByCategory(category.queryCategory.name)
        emit(Resource.Success(results.map { it.toDomainModel(useHd, category.isShiny) }))
    }

    override fun getPokemonByFormRegion(formRegion: String?): Flow<Resource<List<Pokemon>>> = flow {
        emit(Resource.Loading())
        val useHd = userPreferencesRepository.userPreferences.first().useHdImages
        val results = if (formRegion == "other") {
            pokemonDao.getFormsOtherRegion()
        } else {
            pokemonDao.getFormsByRegion(formRegion)
        }
        emit(Resource.Success(results.map { it.toDomainModel(useHd) }))
    }

    override suspend fun precachePokemonNames() {
        if (!networkHelper.isNetworkAvailable()) return

        try {
            val response = pokemonApi.getPokemonList(2000, 0)
            val genLookup = buildGenerationLookup()

            val stubs = response.results.mapNotNull { resource ->
                val id = resource.url.extractIdFromUrl()
                if (id <= 0) return@mapNotNull null
                val category = DexCategory.classify(id, resource.name)
                val region = DexCategory.extractFormRegion(resource.name)
                PokemonEntity(
                    id = id,
                    name = resource.name.capitalizeFirst(),
                    spriteUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png",
                    hdSpriteUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png",
                    animatedSpriteUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/showdown/$id.gif",
                    typePrimary = Pokemon.UNKNOWN_TYPE,
                    typeSecondary = null,
                    generationId = genLookup[id] ?: 0,
                    dexCategory = category.name,
                    formRegion = region
                )
            }

            pokemonDao.insertAllIgnore(stubs)

            stubs.forEach { stub ->
                pokemonDao.updateStubUrls(
                    id = stub.id,
                    spriteUrl = stub.spriteUrl,
                    hdSpriteUrl = stub.hdSpriteUrl,
                    animatedSpriteUrl = stub.animatedSpriteUrl,
                    dexCategory = stub.dexCategory,
                    formRegion = stub.formRegion
                )
            }

            // The stubs above carry Pokemon.UNKNOWN_TYPE, which is the same sentinel the cards
            // read before drawing badges, so this guard and the UI can never disagree. It counts
            // placeholders instead of probing a single row on purpose: page 0 can land real data
            // before this runs, so a row-1 probe would see a real type and skip the other ~1300.
            // Counting also self-heals a partial backfill and settles at ~0 once it succeeds, so
            // later launches issue no /type requests at all.
            if (pokemonDao.getCountByTypePrimary(Pokemon.UNKNOWN_TYPE) > TYPE_BACKFILL_THRESHOLD) {
                precacheTypes()
            }
        } catch (_: Exception) {
        }
    }

    /**
     * Backfills types for the whole dex. Asking for each Pokémon's detail would be ~1300
     * requests, but /type/{id} lists every Pokémon carrying that type, so the 18 canonical
     * types cover the national dex plus its forms in 18 concurrent requests.
     */
    private suspend fun precacheTypes() {
        val responses = coroutineScope {
            (1..CANONICAL_TYPE_COUNT).map { typeId ->
                async {
                    try {
                        typeApi.getType(typeId)
                    } catch (e: Exception) {
                        null
                    }
                }
            }.awaitAll().filterNotNull()
        }

        if (responses.isEmpty()) return

        // PokemonTypeColors keys are capitalized ("Fire"), and getColor does an exact lookup,
        // so an uncapitalized name here would silently paint every card with the Normal color.
        val typeMap = mutableMapOf<Int, Pair<String?, String?>>()
        for (response in responses) {
            val typeName = response.name.capitalizeFirst()
            for (entry in response.pokemon) {
                val id = entry.pokemon.url.extractIdFromUrl()
                if (id <= 0) continue
                val existing = typeMap[id] ?: (null to null)
                typeMap[id] = when (entry.slot) {
                    1 -> typeName to existing.second
                    2 -> existing.first to typeName
                    else -> existing
                }
            }
        }

        if (typeMap.isEmpty()) return

        // One read, one write. Rows the map does not cover keep their placeholder so the badge
        // sentinel still hides them, and unchanged rows are dropped so a re-run writes nothing.
        val updated = pokemonDao.getAllSync().mapNotNull { entity ->
            val resolved = typeMap[entity.id] ?: return@mapNotNull null
            val primary = resolved.first ?: entity.typePrimary
            val secondary = resolved.second
            if (primary == entity.typePrimary && secondary == entity.typeSecondary) {
                return@mapNotNull null
            }
            entity.copy(typePrimary = primary, typeSecondary = secondary)
        }

        if (updated.isNotEmpty()) {
            pokemonDao.insertAll(updated)
        }
    }

    override suspend fun getPokemonByIds(ids: List<Int>): List<Pokemon> {
        if (ids.isEmpty()) return emptyList()
        val useHd = userPreferencesRepository.userPreferences.first().useHdImages
        return pokemonDao.getByIds(ids).map { it.toDomainModel(useHd) }
    }

    // Not cached in Room on purpose: the OkHttp cache already keeps this for 5 minutes fresh
    // and serves it for 7 days while offline (see CacheInterceptor), and a new Room column
    // would trip fallbackToDestructiveMigration and wipe every installed user's cache.
    override suspend fun getPokemonEncounters(id: Int): Resource<List<GameEncounters>> {
        val locale = java.util.Locale.getDefault().language
        return try {
            Resource.Success(pokemonApi.getPokemonEncounters(id).toGameEncounters(locale))
        } catch (e: Exception) {
            if (!networkHelper.isNetworkAvailable()) {
                Resource.Error("NO_INTERNET")
            } else {
                Resource.Error("LOAD_FAILED")
            }
        }
    }

    override suspend fun syncAllPokemon(onProgress: (Int, Int) -> Unit) {
        val firstPage = pokemonApi.getPokemonList(1, 0)
        val totalCount = firstPage.count
        val genLookup = buildGenerationLookup()

        val totalPages = (totalCount + Constants.PAGE_SIZE - 1) / Constants.PAGE_SIZE
        var downloaded = 0

        for (page in 0 until totalPages) {
            val offset = page * Constants.PAGE_SIZE
            try {
                val response = pokemonApi.getPokemonList(Constants.PAGE_SIZE, offset)
                val pokemonIds = response.results
                    .map { it.url.extractIdFromUrl() }
                    .filter { it > 0 }

                val entities = coroutineScope {
                    pokemonIds.map { id ->
                        async {
                            try {
                                pokemonApi.getPokemonDetail(id)
                                    .toPokemonEntity(generationId = genLookup[id] ?: 0)
                            } catch (e: Exception) {
                                null
                            }
                        }
                    }.awaitAll().filterNotNull()
                }

                if (entities.isNotEmpty()) {
                    pokemonDao.insertAll(entities)
                }
                downloaded += entities.size
                onProgress(downloaded, totalCount)
            } catch (e: Exception) {
            }
        }
    }

    override suspend fun refreshPokemonData() {
        pokemonDao.deleteAll()
        pokemonDetailDao.deleteAll()
    }

    override suspend fun getLocalPokemonCount(): Int {
        return pokemonDao.getCount()
    }

    override suspend fun getTotalPokemonCount(): Int {
        return try {
            val response = pokemonApi.getPokemonList(1, 0)
            response.count
        } catch (e: Exception) {
            0
        }
    }

    private companion object {
        /** PokeAPI type ids 1..18 are the canonical types; 10000+ are shadow/unknown. */
        const val CANONICAL_TYPE_COUNT = 18

        /**
         * A handful of very new forms may not appear under any /type endpoint, so requiring
         * exactly zero placeholders would re-run the backfill on every launch. This tolerance
         * is far below the ~1300 placeholders a fresh install starts with.
         */
        const val TYPE_BACKFILL_THRESHOLD = 50
    }
}
