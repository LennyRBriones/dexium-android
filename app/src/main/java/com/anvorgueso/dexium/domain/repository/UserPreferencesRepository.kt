package com.anvorgueso.dexium.domain.repository

import com.anvorgueso.dexium.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val userPreferences: Flow<UserPreferences>
    suspend fun setOnboardingComplete(complete: Boolean)
    suspend fun setUseHdImages(useHd: Boolean)
    suspend fun setUseImperialUnits(useImperial: Boolean)

    /** Pass null to go back to following the device language. */
    suspend fun setLanguage(code: String?)
}
