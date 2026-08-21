package com.anvorgueso.dexium.core.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.anvorgueso.dexium.domain.model.UserPreferences
import com.anvorgueso.dexium.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {

    companion object {
        val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        val USE_HD_IMAGES = booleanPreferencesKey("use_hd_images")
        val USE_IMPERIAL_UNITS = booleanPreferencesKey("use_imperial_units")
        val LANGUAGE_CODE = stringPreferencesKey("language_code")
    }

    override val userPreferences: Flow<UserPreferences> = dataStore.data.map { preferences ->
        UserPreferences(
            isOnboardingComplete = preferences[ONBOARDING_COMPLETE] ?: false,
            useHdImages = preferences[USE_HD_IMAGES] ?: true,
            useImperialUnits = preferences[USE_IMPERIAL_UNITS] ?: false,
            languageCode = preferences[LANGUAGE_CODE]
        )
    }

    override suspend fun setOnboardingComplete(complete: Boolean) {
        dataStore.edit { it[ONBOARDING_COMPLETE] = complete }
    }

    override suspend fun setUseHdImages(useHd: Boolean) {
        dataStore.edit { it[USE_HD_IMAGES] = useHd }
    }

    override suspend fun setUseImperialUnits(useImperial: Boolean) {
        dataStore.edit { it[USE_IMPERIAL_UNITS] = useImperial }
    }

    override suspend fun setLanguage(code: String?) {
        dataStore.edit { prefs ->
            if (code == null) prefs.remove(LANGUAGE_CODE) else prefs[LANGUAGE_CODE] = code
        }
    }
}
