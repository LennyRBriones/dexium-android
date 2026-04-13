package com.anvorgueso.dexium.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anvorgueso.dexium.core.database.entity.PokemonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDao {

    @Query("SELECT * FROM pokemon ORDER BY id ASC")
    fun getAll(): Flow<List<PokemonEntity>>

    @Query("SELECT * FROM pokemon WHERE dexCategory = :category ORDER BY id ASC LIMIT :limit OFFSET :offset")
    suspend fun getPaginatedByCategory(category: String, limit: Int, offset: Int): List<PokemonEntity>

    @Query("SELECT * FROM pokemon WHERE id = :id")
    suspend fun getById(id: Int): PokemonEntity?

    @Query("SELECT * FROM pokemon WHERE dexCategory = :category ORDER BY id ASC")
    suspend fun getAllByCategory(category: String): List<PokemonEntity>

    @Query("SELECT * FROM pokemon WHERE generationId = :generationId AND dexCategory = :category ORDER BY id ASC")
    suspend fun getByGenerationAndCategory(generationId: Int, category: String): List<PokemonEntity>


    @Query("SELECT * FROM pokemon WHERE name LIKE '%' || :query || '%' AND dexCategory = :category ORDER BY id ASC")
    fun searchByNameAndCategory(query: String, category: String): Flow<List<PokemonEntity>>

    @Query("SELECT * FROM pokemon WHERE name LIKE '%' || :query || '%' AND dexCategory = :category AND generationId = :generationId ORDER BY id ASC")
    fun searchByNameCategoryAndGeneration(query: String, category: String, generationId: Int): Flow<List<PokemonEntity>>


    @Query("SELECT * FROM pokemon WHERE dexCategory = 'FORMS' AND (:formRegion IS NULL OR formRegion = :formRegion) ORDER BY id ASC")
    suspend fun getFormsByRegion(formRegion: String?): List<PokemonEntity>

    @Query("SELECT * FROM pokemon WHERE dexCategory = 'FORMS' AND formRegion IS NULL ORDER BY id ASC")
    suspend fun getFormsOtherRegion(): List<PokemonEntity>

    @Query("SELECT * FROM pokemon WHERE name LIKE '%' || :query || '%' AND dexCategory = 'FORMS' AND (:formRegion IS NULL OR formRegion = :formRegion) ORDER BY id ASC")
    fun searchFormsByNameAndRegion(query: String, formRegion: String?): Flow<List<PokemonEntity>>

    @Query("SELECT * FROM pokemon WHERE name LIKE '%' || :query || '%' AND dexCategory = 'FORMS' AND formRegion IS NULL ORDER BY id ASC")
    fun searchFormsOtherByName(query: String): Flow<List<PokemonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pokemon: List<PokemonEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllIgnore(pokemon: List<PokemonEntity>)

    @Query("UPDATE pokemon SET spriteUrl = :spriteUrl, hdSpriteUrl = :hdSpriteUrl, animatedSpriteUrl = :animatedSpriteUrl, dexCategory = :dexCategory, formRegion = :formRegion WHERE id = :id AND hdSpriteUrl IS NULL")
    suspend fun updateStubUrls(id: Int, spriteUrl: String?, hdSpriteUrl: String?, animatedSpriteUrl: String?, dexCategory: String, formRegion: String?)

    @Query("DELETE FROM pokemon")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM pokemon")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM pokemon WHERE dexCategory = :category")
    suspend fun getCountByCategory(category: String): Int
}
