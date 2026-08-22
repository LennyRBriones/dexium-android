package com.anvorgueso.dexium.core.repository

import com.anvorgueso.dexium.core.database.dao.HighScoreDao
import com.anvorgueso.dexium.core.database.entity.HighScoreEntity
import com.anvorgueso.dexium.core.util.GameMode
import com.anvorgueso.dexium.domain.model.HighScore
import com.anvorgueso.dexium.domain.repository.HighScoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HighScoreRepositoryImpl @Inject constructor(
    private val highScoreDao: HighScoreDao
) : HighScoreRepository {

    override fun observeAll(): Flow<Map<String, HighScore>> =
        highScoreDao.getAll().map { entities ->
            entities.associate { it.mode to it.toDomainModel() }
        }

    override suspend fun getForMode(rawMode: String): HighScore? =
        highScoreDao.getByMode(GameMode.normalize(rawMode))?.toDomainModel()

    override suspend fun submit(
        rawMode: String,
        score: Int,
        total: Int
    ): HighScoreRepository.SubmitResult {
        val mode = GameMode.normalize(rawMode)
        val existing = highScoreDao.getByMode(mode)

        // Compared on raw score, which is what a player means by "my record is 9". Rounds only
        // drop below ten when a generation has almost no cached data, so a short run simply
        // cannot beat a full one — deliberate, rather than ranking 5/5 above 9/10.
        val beatsExisting = existing == null || score > existing.score
        if (!beatsExisting) {
            return HighScoreRepository.SubmitResult(
                record = existing!!.toDomainModel(),
                isNewRecord = false
            )
        }

        val entity = HighScoreEntity(
            mode = mode,
            score = score,
            total = total,
            achievedAt = System.currentTimeMillis()
        )
        highScoreDao.insert(entity)
        return HighScoreRepository.SubmitResult(
            record = entity.toDomainModel(),
            isNewRecord = true
        )
    }

    private fun HighScoreEntity.toDomainModel() =
        HighScore(mode = mode, score = score, total = total, achievedAt = achievedAt)
}
