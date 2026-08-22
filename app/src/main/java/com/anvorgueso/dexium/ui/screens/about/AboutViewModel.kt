package com.anvorgueso.dexium.ui.screens.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anvorgueso.dexium.core.util.AppLanguage
import com.anvorgueso.dexium.domain.repository.PokemonRepository
import com.anvorgueso.dexium.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AboutUiState(
    val useHdImages: Boolean = true,
    val useImperialUnits: Boolean = false,
    val language: AppLanguage = AppLanguage.fromDeviceLocale(),
    val isFollowingDeviceLanguage: Boolean = true,
    val cachedPokemonCount: Int = 0,
    val totalPokemonCount: Int = 0,
    val isSyncing: Boolean = false,
    val syncProgress: String = ""
)

@HiltViewModel
class AboutViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val pokemonRepository: PokemonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AboutUiState())
    val uiState: StateFlow<AboutUiState> = _uiState

    init {
        loadPreferences()
        loadCachedCount()
        loadTotalCount()
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            userPreferencesRepository.userPreferences.collect { prefs ->
                _uiState.update {
                    it.copy(
                        useHdImages = prefs.useHdImages,
                        useImperialUnits = prefs.useImperialUnits,
                        language = prefs.language,
                        isFollowingDeviceLanguage = prefs.isFollowingDevice
                    )
                }
            }
        }
    }

    private fun loadCachedCount() {
        viewModelScope.launch {
            val count = pokemonRepository.getLocalPokemonCount()
            _uiState.update { it.copy(cachedPokemonCount = count) }
        }
    }

    private fun loadTotalCount() {
        viewModelScope.launch {
            val total = pokemonRepository.getTotalPokemonCount()
            if (total > 0) {
                _uiState.update { it.copy(totalPokemonCount = total) }
            }
        }
    }

    fun toggleHdImages(useHd: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setUseHdImages(useHd)
        }
    }

    fun toggleImperialUnits(useImperial: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setUseImperialUnits(useImperial)
        }
    }

    /**
     * Cached details hold descriptions, genera and type names already translated, so changing
     * the language has to drop them — otherwise the detail screen keeps serving the old
     * language until each Pokémon happens to be refetched.
     */
    fun setLanguage(language: AppLanguage?) {
        viewModelScope.launch {
            userPreferencesRepository.setLanguage(language?.code)
            pokemonRepository.clearDetailCache()
        }
    }

    fun syncAllData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, syncProgress = "Starting sync...") }
            try {
                pokemonRepository.syncAllPokemon { current, total ->
                    _uiState.update { it.copy(syncProgress = "Syncing: $current / $total") }
                }
                val count = pokemonRepository.getLocalPokemonCount()
                _uiState.update {
                    it.copy(isSyncing = false, cachedPokemonCount = count, syncProgress = "Sync complete!")
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSyncing = false, syncProgress = "Sync failed: ${e.localizedMessage}")
                }
            }
        }
    }

    fun eraseDownloadedData() {
        viewModelScope.launch {
            try {
                pokemonRepository.refreshPokemonData()
                _uiState.update { it.copy(cachedPokemonCount = 0, syncProgress = "") }
            } catch (e: Exception) {
                _uiState.update { it.copy(syncProgress = "Failed to erase: ${e.localizedMessage}") }
            }
        }
    }
}
