package com.anvorgueso.dexium

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anvorgueso.dexium.core.util.AppLanguage
import com.anvorgueso.dexium.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Holds the app-wide language so the whole UI can be wrapped in it before anything composes.
 *
 * Seeded from the device locale rather than a fixed default, so the first frame is already in
 * the right language instead of flashing English while DataStore is read.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val language: StateFlow<AppLanguage> = userPreferencesRepository.userPreferences
        .map { it.language }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = AppLanguage.fromDeviceLocale()
        )
}
