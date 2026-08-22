package com.anvorgueso.dexium.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anvorgueso.dexium.core.database.entity.HighScoreEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HighScoreDao {

    @Query("SELECT * FROM high_score")
    fun getAll(): Flow<List<HighScoreEntity>>

    @Query("SELECT * FROM high_score WHERE mode = :mode")
    suspend fun getByMode(mode: String): HighScoreEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(highScore: HighScoreEntity)

    @Query("DELETE FROM high_score")
    suspend fun deleteAll()
}
