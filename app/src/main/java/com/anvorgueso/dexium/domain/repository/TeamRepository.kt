package com.anvorgueso.dexium.domain.repository

import com.anvorgueso.dexium.core.util.Resource
import com.anvorgueso.dexium.domain.model.Team
import kotlinx.coroutines.flow.Flow

interface TeamRepository {
    fun observeTeams(): Flow<List<Team>>

    /** Observed rather than fetched, so an edit refreshes the detail screen on its own. */
    fun observeTeam(id: Long): Flow<Team?>

    suspend fun getTeam(id: Long): Team?

    /** [id] of 0 inserts a new team; anything else overwrites that team in place. */
    suspend fun saveTeam(id: Long, name: String, memberIds: List<Int>): Long

    suspend fun deleteTeam(id: Long)

    /** Asks Gemini for coaching notes on [team]. Returns prose in the app's language. */
    suspend fun analyzeTeam(team: Team, languageName: String): Resource<String>
}
