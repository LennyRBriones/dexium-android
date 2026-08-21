package com.anvorgueso.dexium.domain.model

data class HighScore(
    val mode: String,
    val score: Int,
    val total: Int,
    val achievedAt: Long
)
