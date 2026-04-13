package com.anvorgueso.dexium.domain.model

data class UserPreferences(
    val isOnboardingComplete: Boolean = false,
    val useHdImages: Boolean = true,
    val useImperialUnits: Boolean = false
)
