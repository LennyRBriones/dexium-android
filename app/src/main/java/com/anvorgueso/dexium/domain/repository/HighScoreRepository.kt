package com.anvorgueso.dexium.domain.repository

import com.anvorgueso.dexium.domain.model.HighScore
import kotlinx.coroutines.flow.Flow

interface HighScoreRepository {
    /** Every stored record, keyed by normalized mode. */
    fun observeAll(): Flow<Map<String, HighScore>>

    suspend fun getForMode(rawMode: String): HighScore?

    /**
     * Stores [score] if it beats the stored record for that mode.
     * Returns the record that now stands, plus whether this run set it.
     */
    suspend fun submit(rawMode: String, score: Int, total: Int): SubmitResult

    data class SubmitResult(val record: HighScore, val isNewRecord: Boolean)
}
