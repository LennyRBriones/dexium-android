package com.anvorgueso.dexium.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anvorgueso.dexium.core.database.entity.GenerationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GenerationDao {

    @Query("SELECT * FROM generation ORDER BY sortOrder ASC, id ASC")
    fun getAll(): Flow<List<GenerationEntity>>

    @Query("SELECT * FROM generation ORDER BY sortOrder ASC, id ASC")
    suspend fun getAllSync(): List<GenerationEntity>

    @Query("SELECT * FROM generation WHERE id = :id")
    suspend fun getById(id: Int): GenerationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(generations: List<GenerationEntity>)

    @Query("DELETE FROM generation")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM generation")
    suspend fun getCount(): Int
}
