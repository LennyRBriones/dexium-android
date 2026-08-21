package com.anvorgueso.dexium.core.repository

import com.anvorgueso.dexium.core.database.dao.PokemonDao
import com.anvorgueso.dexium.core.database.dao.TeamDao
import com.anvorgueso.dexium.core.database.entity.TeamEntity
import com.anvorgueso.dexium.core.di.TeamAdvisorModel
import com.anvorgueso.dexium.core.mappers.PokemonMapper.toDomainModel
import com.anvorgueso.dexium.core.util.NetworkConnectivityHelper
import com.anvorgueso.dexium.core.util.Resource
import com.anvorgueso.dexium.core.util.TypeChart
import com.anvorgueso.dexium.domain.model.Team
import com.anvorgueso.dexium.domain.repository.TeamRepository
import com.anvorgueso.dexium.domain.repository.UserPreferencesRepository
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.ServerException
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeamRepositoryImpl @Inject constructor(
    private val teamDao: TeamDao,
    private val pokemonDao: PokemonDao,
    @TeamAdvisorModel private val advisorModel: GenerativeModel,
    private val networkHelper: NetworkConnectivityHelper,
    private val userPreferencesRepository: UserPreferencesRepository
) : TeamRepository {

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val intListAdapter = moshi.adapter<List<Int>>(
        Types.newParameterizedType(List::class.java, Integer::class.java)
    )

    override fun observeTeams(): Flow<List<Team>> =
        teamDao.getAll().map { entities ->
            val useHd = userPreferencesRepository.userPreferences.first().useHdImages
            entities.map { it.toTeam(useHd) }
        }

    override fun observeTeam(id: Long): Flow<Team?> =
        teamDao.observeById(id).map { entity ->
            entity?.toTeam(userPreferencesRepository.userPreferences.first().useHdImages)
        }

    override suspend fun getTeam(id: Long): Team? {
        val entity = teamDao.getById(id) ?: return null
        val useHd = userPreferencesRepository.userPreferences.first().useHdImages
        return entity.toTeam(useHd)
    }

    override suspend fun saveTeam(id: Long, name: String, memberIds: List<Int>): Long =
        teamDao.insert(
            TeamEntity(
                id = id,
                name = name.trim(),
                memberIdsJson = intListAdapter.toJson(memberIds),
                // Keep the original timestamp when editing, or the team would jump to the top
                // of a list that is ordered by creation date.
                createdAt = if (id == 0L) {
                    System.currentTimeMillis()
                } else {
                    teamDao.getById(id)?.createdAt ?: System.currentTimeMillis()
                }
            )
        )

    override suspend fun deleteTeam(id: Long) = teamDao.deleteById(id)

    override suspend fun analyzeTeam(team: Team, languageName: String): Resource<String> {
        if (!networkHelper.isNetworkAvailable()) return Resource.Error("NO_INTERNET")
        if (team.members.isEmpty()) return Resource.Error("EMPTY_TEAM")

        return try {
            val response = advisorModel.generateContent(buildPrompt(team, languageName))
            val text = response.text?.trim()
            if (text.isNullOrEmpty()) Resource.Error("EMPTY_RESPONSE") else Resource.Success(text)
        } catch (e: ServerException) {
            val message = e.message.orEmpty()
            when {
                message.contains("429") || message.contains("quota", ignoreCase = true) ||
                    message.contains("rate", ignoreCase = true) -> Resource.Error("RATE_LIMIT")
                message.contains("401") || message.contains("403") ||
                    message.contains("API key", ignoreCase = true) -> Resource.Error("INVALID_KEY")
                message.contains("404") || message.contains("not found", ignoreCase = true) ||
                    message.contains("no longer available", ignoreCase = true) ->
                    Resource.Error("MODEL_UNAVAILABLE")
                else -> Resource.Error("SERVER_ERROR")
            }
        } catch (e: Exception) {
            Resource.Error("UNKNOWN_ERROR")
        }
    }

    /**
     * The defensive summary is computed locally from [TypeChart] and handed to the model, so
     * the advice is grounded in the real chart instead of the model's recollection of it.
     */
    private fun buildPrompt(team: Team, languageName: String): String {
        val roster = team.members.joinToString("\n") { member ->
            val types = listOfNotNull(member.typePrimary, member.typeSecondary).joinToString("/")
            "- ${member.name} ($types)"
        }

        val coverage = TypeChart.teamCoverage(team.members)
        val holes = coverage.filter { it.weakCount > 0 }.joinToString("\n") { entry ->
            val hit = entry.weakMembers.joinToString(", ") { "${it.pokemon.name} x${it.multiplier}" }
            "- ${entry.type}: hits $hit; ${entry.resistCount} resist, ${entry.immuneCount} immune"
        }.ifEmpty { "- none: no attacking type hits any member for extra damage" }

        return """
            Language: $languageName

            Team "${team.name}" (${team.members.size}/${Team.MAX_MEMBERS} members):
            $roster

            Defensive summary against each attacking type:
            $holes
        """.trimIndent()
    }

    private suspend fun TeamEntity.toTeam(useHd: Boolean): Team {
        val ids = try {
            intListAdapter.fromJson(memberIdsJson) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
        // Re-read in the stored order: getByIds sorts by id, which is not the roster order.
        val byId = pokemonDao.getByIds(ids).associateBy { it.id }
        return Team(
            id = id,
            name = name,
            members = ids.mapNotNull { byId[it]?.toDomainModel(useHd) }
        )
    }
}
