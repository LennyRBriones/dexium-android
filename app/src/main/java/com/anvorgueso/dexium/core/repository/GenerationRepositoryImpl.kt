package com.anvorgueso.dexium.core.repository

import com.anvorgueso.dexium.core.database.dao.GenerationDao
import com.anvorgueso.dexium.core.database.entity.GenerationEntity
import com.anvorgueso.dexium.core.mappers.GenerationMapper.toDomainModel
import com.anvorgueso.dexium.core.mappers.GenerationMapper.toEntity
import com.anvorgueso.dexium.core.mappers.GenerationMapper.getSpeciesIds
import com.anvorgueso.dexium.core.mappers.GenerationMapper.toSpeciesJson
import com.anvorgueso.dexium.core.network.api.GenerationApiService
import com.anvorgueso.dexium.core.util.NetworkConnectivityHelper
import com.anvorgueso.dexium.core.util.Resource
import com.anvorgueso.dexium.domain.model.Generation
import com.anvorgueso.dexium.domain.repository.GenerationRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GenerationRepositoryImpl @Inject constructor(
    private val generationApi: GenerationApiService,
    private val generationDao: GenerationDao,
    private val networkHelper: NetworkConnectivityHelper
) : GenerationRepository {

    override fun getGenerations(): Flow<Resource<List<Generation>>> = flow {
        emit(Resource.Loading())

        generationDao.getAll().collect { localGenerations ->
            if (localGenerations.isNotEmpty()) {
                emit(Resource.Success(localGenerations.map { it.toDomainModel() }))
            } else if (networkHelper.isNetworkAvailable()) {
                try {
                    fetchAndCacheGenerations()
                } catch (e: Exception) {
                    emit(Resource.Error("Failed to load generations: ${e.localizedMessage}"))
                }
            } else {
                emit(Resource.Error("No internet connection and no cached data"))
            }
        }
    }

    override suspend fun refreshGenerations() {
        generationDao.deleteAll()
        fetchAndCacheGenerations()
    }

    companion object {
        const val HISUI_VIRTUAL_GEN_ID = 80
        val HISUI_SPECIES_IDS = (899..905).toList()
    }

    private suspend fun fetchAndCacheGenerations() {
        val entities = coroutineScope {
            (1..9).map { id ->
                async {
                    try {
                        generationApi.getGeneration(id).toEntity()
                    } catch (e: Exception) {
                        null
                    }
                }
            }.awaitAll().filterNotNull()
        }

        val finalEntities = entities.flatMap { entity ->
            if (entity.id == 8) {
                val allSpeciesIds = getSpeciesIds(entity)
                val galarIds = allSpeciesIds.filter { it !in HISUI_SPECIES_IDS }
                val hisuiIds = allSpeciesIds.filter { it in HISUI_SPECIES_IDS }

                val galarEntity = entity.copy(
                    pokemonSpeciesIdsJson = toSpeciesJson(galarIds)
                )
                val hisuiEntity = GenerationEntity(
                    id = HISUI_VIRTUAL_GEN_ID,
                    name = "generation-hisui",
                    displayName = "Generation Hisui",
                    regionName = "Hisui",
                    pokemonSpeciesIdsJson = toSpeciesJson(hisuiIds),
                    sortOrder = 85
                )
                listOf(galarEntity, hisuiEntity)
            } else {
                listOf(entity)
            }
        }

        generationDao.insertAll(finalEntities)
    }
}
