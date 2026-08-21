package com.anvorgueso.dexium.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Best result for one guess-game mode. The key is the normalized mode string produced by
 * [com.anvorgueso.dexium.core.util.GameMode.normalize] — "all", a single generation id, or a
 * sorted comma list for custom combinations.
 */
@Entity(tableName = "high_score")
data class HighScoreEntity(
    @PrimaryKey val mode: String,
    val score: Int,
    val total: Int,
    val achievedAt: Long
)
