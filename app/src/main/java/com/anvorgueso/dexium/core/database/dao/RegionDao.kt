package com.anvorgueso.dexium.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anvorgueso.dexium.core.database.entity.RegionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RegionDao {

    @Query("SELECT * FROM region ORDER BY id ASC")
    fun getAll(): Flow<List<RegionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(regions: List<RegionEntity>)

    @Query("DELETE FROM region")
    suspend fun deleteAll()
}
