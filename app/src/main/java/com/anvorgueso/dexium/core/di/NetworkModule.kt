package com.anvorgueso.dexium.core.di

import android.content.Context
import com.anvorgueso.dexium.BuildConfig
import com.anvorgueso.dexium.core.network.api.AbilityApiService
import com.anvorgueso.dexium.core.network.api.EvolutionApiService
import com.anvorgueso.dexium.core.network.api.GenerationApiService
import com.anvorgueso.dexium.core.network.api.PokemonApiService
import com.anvorgueso.dexium.core.network.api.PokemonSpeciesApiService
import com.anvorgueso.dexium.core.network.api.RegionApiService
import com.anvorgueso.dexium.core.network.api.TypeApiService
import com.anvorgueso.dexium.core.network.interceptor.CacheInterceptor
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerationConfig
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        val cacheSize = 50L * 1024 * 1024
        val cache = Cache(File(context.cacheDir, "http_cache"), cacheSize)

        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(CacheInterceptor(context))
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BASIC
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                }
            )
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun providePokemonApiService(retrofit: Retrofit): PokemonApiService {
        return retrofit.create(PokemonApiService::class.java)
    }

    @Provides
    @Singleton
    fun providePokemonSpeciesApiService(retrofit: Retrofit): PokemonSpeciesApiService {
        return retrofit.create(PokemonSpeciesApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideGenerationApiService(retrofit: Retrofit): GenerationApiService {
        return retrofit.create(GenerationApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideEvolutionApiService(retrofit: Retrofit): EvolutionApiService {
        return retrofit.create(EvolutionApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAbilityApiService(retrofit: Retrofit): AbilityApiService {
        return retrofit.create(AbilityApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRegionApiService(retrofit: Retrofit): RegionApiService {
        return retrofit.create(RegionApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideTypeApiService(retrofit: Retrofit): TypeApiService {
        return retrofit.create(TypeApiService::class.java)
    }

    @Provides
    @Singleton
    @PokemonIdentifierModel
    fun provideGenerativeModel(): GenerativeModel {
        val config: GenerationConfig = generationConfig {
            responseMimeType = "application/json"
            temperature = 0.4f
        }

        return GenerativeModel(
            modelName = "gemini-2.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY,
            generationConfig = config,
            systemInstruction = content {
                text(
                    """
                    You are a Pokemon expert. The user will describe characteristics of a Pokemon (in any language) and you must identify which Pokemon match that description.

                    STRICT RULES:
                    1. Respond ONLY with a JSON array of Pokemon names in English, lowercase.
                    2. Maximum 10 results, ordered from most likely to least likely.
                    3. Consider: type, color, number of legs, size, generation, abilities, appearance, region.
                    4. If unsure, include several possible options.
                    5. Use the base species name only (no forms like "-mega", "-alola"). For example "charizard", not "charizard-mega-x".
                    6. Do NOT include any extra text. ONLY the JSON array.

                    Example response: ["arcanine","flareon","entei","heatmor"]
                    """.trimIndent()
                )
            }
        )
    }

    /**
     * Separate model for team analysis: no JSON response mime type, since this one answers in
     * prose, and a higher temperature because the output is advice rather than a lookup.
     */
    @Provides
    @Singleton
    @TeamAdvisorModel
    fun provideTeamAdvisorModel(): GenerativeModel {
        val config: GenerationConfig = generationConfig {
            temperature = 0.7f
        }

        return GenerativeModel(
            modelName = "gemini-2.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY,
            generationConfig = config,
            systemInstruction = content {
                text(
                    """
                    You are a competitive Pokemon team-building coach. The user gives you a team
                    of up to six Pokemon with their types, plus a precomputed defensive summary
                    of which attacking types the team is collectively weak to.

                    RULES:
                    1. Reply in the language named by the "Language:" line of the prompt. Use
                       that language for everything, including Pokemon type names.
                    2. Plain text only. No markdown, no asterisks, no headings, no code blocks.
                    3. Keep it under 180 words.
                    4. Structure the answer as three short labelled paragraphs, in this order:
                       the team's biggest defensive hole, the single weakest member and why,
                       and one concrete swap suggestion naming a replacement Pokemon.
                    5. Base the analysis on the types given. Do not invent members.
                    6. Be specific and practical. No filler and no disclaimers.
                    """.trimIndent()
                )
            }
        )
    }
}
