package com.anvorgueso.dexium.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anvorgueso.dexium.core.database.entity.TeamEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamDao {

    @Query("SELECT * FROM team ORDER BY createdAt DESC")
    fun getAll(): Flow<List<TeamEntity>>

    @Query("SELECT * FROM team WHERE id = :id")
    suspend fun getById(id: Long): TeamEntity?

    @Query("SELECT * FROM team WHERE id = :id")
    fun observeById(id: Long): Flow<TeamEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(team: TeamEntity): Long

    @Query("DELETE FROM team WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM team")
    suspend fun getCount(): Int
}
