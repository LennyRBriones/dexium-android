package com.anvorgueso.dexium.core.repository

import com.anvorgueso.dexium.core.database.dao.PokemonDao
import com.anvorgueso.dexium.core.mappers.PokemonMapper.toDomainModel
import com.anvorgueso.dexium.core.util.NetworkConnectivityHelper
import com.anvorgueso.dexium.core.util.Resource
import com.anvorgueso.dexium.domain.model.Pokemon
import com.anvorgueso.dexium.domain.repository.AiChatRepository
import com.anvorgueso.dexium.domain.repository.UserPreferencesRepository
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.ServerException
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiChatRepositoryImpl @Inject constructor(
    private val generativeModel: GenerativeModel,
    private val pokemonDao: PokemonDao,
    private val networkHelper: NetworkConnectivityHelper,
    private val userPreferencesRepository: UserPreferencesRepository
) : AiChatRepository {

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val stringListAdapter = moshi.adapter<List<String>>(
        Types.newParameterizedType(List::class.java, String::class.java)
    )

    override suspend fun identifyPokemon(userDescription: String): Resource<List<Pokemon>> {
        if (!networkHelper.isNetworkAvailable()) {
            return Resource.Error("NO_INTERNET")
        }

        return try {
            val response = generativeModel.generateContent(userDescription)
            val text = response.text?.trim()
                ?: return Resource.Error("EMPTY_RESPONSE")

            val cleanJson = text
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val pokemonNames = try {
                stringListAdapter.fromJson(cleanJson) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }

            if (pokemonNames.isEmpty()) {
                return Resource.Success(emptyList())
            }

            val useHd = userPreferencesRepository.userPreferences.first().useHdImages
            val results = pokemonNames.mapNotNull { name ->
                val sanitized = name.trim().lowercase()
                pokemonDao.searchByExactName(sanitized)?.toDomainModel(useHd)
            }

            Resource.Success(results)
        } catch (e: ServerException) {
            val message = e.message.orEmpty()
            when {
                message.contains("429") || message.contains("quota", ignoreCase = true) ||
                    message.contains("rate", ignoreCase = true) -> Resource.Error("RATE_LIMIT")
                message.contains("401") || message.contains("403") ||
                    message.contains("API key", ignoreCase = true) -> Resource.Error("INVALID_KEY")
                message.contains("404") || message.contains("not found", ignoreCase = true) ||
                    message.contains("no longer available", ignoreCase = true) -> Resource.Error("MODEL_UNAVAILABLE")
                else -> Resource.Error("SERVER_ERROR")
            }
        } catch (e: Exception) {
            Resource.Error("UNKNOWN_ERROR")
        }
    }
}
