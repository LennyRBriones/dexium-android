package com.anvorgueso.dexium.core.repository

import com.anvorgueso.dexium.core.database.dao.GenerationDao
import com.anvorgueso.dexium.core.database.dao.PokemonDao
import com.anvorgueso.dexium.core.database.dao.PokemonDetailDao
import com.anvorgueso.dexium.core.mappers.EvolutionMapper.toEvolutionStages
import com.anvorgueso.dexium.core.mappers.PokemonMapper.toDomainModel
import com.anvorgueso.dexium.core.mappers.PokemonMapper.toPokemonDetailEntity
import com.anvorgueso.dexium.core.mappers.PokemonMapper.toPokemonEntity
import com.anvorgueso.dexium.core.network.api.EvolutionApiService
import com.anvorgueso.dexium.core.network.api.PokemonApiService
import com.anvorgueso.dexium.core.network.api.PokemonSpeciesApiService
import com.anvorgueso.dexium.core.database.entity.PokemonEntity
import com.anvorgueso.dexium.core.util.Constants
import com.anvorgueso.dexium.domain.model.DexCategory
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

        val localPokemon = pokemonDao.getPaginatedByCategory(category.name, pageSize, offset)
        if (localPokemon.isNotEmpty()) {
            emit(Resource.Success(localPokemon.map { it.toDomainModel(useHd) }))
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
                    val filtered = entities.filter { it.dexCategory == category.name }
                    emit(Resource.Success(filtered.map { it.toDomainModel(useHd) }))
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
                pokemonDao.searchByNameCategoryAndGeneration(query, category.name, generationId)
            }
            else -> {
                pokemonDao.searchByNameAndCategory(query, category.name)
            }
        }
        searchFlow.collect { results ->
            emit(Resource.Success(results.map { it.toDomainModel(useHd) }))
        }
    }

    override fun getPokemonByGeneration(generationId: Int, category: DexCategory): Flow<Resource<List<Pokemon>>> = flow {
        emit(Resource.Loading())
        val useHd = userPreferencesRepository.userPreferences.first().useHdImages

        val localResults = pokemonDao.getByGenerationAndCategory(generationId, category.name)
        if (localResults.isNotEmpty()) {
            emit(Resource.Success(localResults.map { it.toDomainModel(useHd) }))
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
                    emit(Resource.Success(allEntities.map { it.toDomainModel(useHd) }))
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
        val results = pokemonDao.getAllByCategory(category.name)
        emit(Resource.Success(results.map { it.toDomainModel(useHd) }))
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
                    typePrimary = "Unknown",
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
        } catch (_: Exception) {
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
}
