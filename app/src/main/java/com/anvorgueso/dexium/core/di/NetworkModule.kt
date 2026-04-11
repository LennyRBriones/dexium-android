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
}
