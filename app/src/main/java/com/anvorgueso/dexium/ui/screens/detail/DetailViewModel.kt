package com.anvorgueso.dexium.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anvorgueso.dexium.core.audio.CryPlayer
import com.anvorgueso.dexium.core.util.Resource
import com.anvorgueso.dexium.domain.model.GameEncounters
import com.anvorgueso.dexium.domain.model.PokemonDetail
import com.anvorgueso.dexium.domain.repository.PokemonRepository
import com.anvorgueso.dexium.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailUiState(
    val pokemonDetail: PokemonDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val useImperialUnits: Boolean = false,
    /** Set when the user arrived from the Shiny Dex, so the sprites here match the card. */
    val isShiny: Boolean = false,
    val isPlayingCry: Boolean = false,
    val encounters: List<GameEncounters> = emptyList(),
    val selectedGameSlug: String? = null,
    val isLoadingEncounters: Boolean = false,
    val encountersError: String? = null
) {
    val selectedGame: GameEncounters?
        get() = encounters.firstOrNull { it.versionSlug == selectedGameSlug } ?: encounters.lastOrNull()
}

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val pokemonRepository: PokemonRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val cryPlayer: CryPlayer
) : ViewModel() {

    private val pokemonId: Int = savedStateHandle.get<Int>("pokemonId") ?: 0
    private val isShiny: Boolean = savedStateHandle.get<Boolean>("shiny") ?: false

    private val _uiState = MutableStateFlow(DetailUiState(isShiny = isShiny))
    val uiState: StateFlow<DetailUiState> = _uiState

    init {
        loadPokemonDetail()
        observePreferences()
        observeCryPlayback()
        loadEncounters()
    }

    private fun observePreferences() {
        viewModelScope.launch {
            userPreferencesRepository.userPreferences.collect { prefs ->
                _uiState.update { it.copy(useImperialUnits = prefs.useImperialUnits) }
            }
        }
    }

    private fun observeCryPlayback() {
        viewModelScope.launch {
            cryPlayer.isPlaying.collect { playing ->
                _uiState.update { it.copy(isPlayingCry = playing) }
            }
        }
    }

    private fun loadPokemonDetail() {
        viewModelScope.launch {
            pokemonRepository.getPokemonDetail(pokemonId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true, error = null) }
                    is Resource.Success -> _uiState.update {
                        it.copy(pokemonDetail = resource.data, isLoading = false, error = null)
                    }
                    is Resource.Error -> _uiState.update {
                        it.copy(isLoading = false, error = resource.message)
                    }
                }
            }
        }
    }

    /**
     * Loaded separately from the main detail so a slow or missing encounters response never
     * blocks the screen. PokeAPI has no encounter data at all for generation 9, so an empty
     * success is a normal outcome, not an error.
     */
    private fun loadEncounters() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingEncounters = true, encountersError = null) }
            when (val result = pokemonRepository.getPokemonEncounters(pokemonId)) {
                is Resource.Success -> {
                    val games = result.data ?: emptyList()
                    _uiState.update {
                        it.copy(
                            encounters = games,
                            // Games are release-ordered, so default to the newest one this
                            // Pokémon appears in — a player is far likelier to be on a recent
                            // game than on the 1996 Japan-only release that sorts first.
                            selectedGameSlug = it.selectedGameSlug ?: games.lastOrNull()?.versionSlug,
                            isLoadingEncounters = false,
                            encountersError = null
                        )
                    }
                }
                is Resource.Error -> _uiState.update {
                    it.copy(isLoadingEncounters = false, encountersError = result.message)
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun onGameSelected(versionSlug: String) {
        _uiState.update { it.copy(selectedGameSlug = versionSlug) }
    }

    fun playCry() {
        cryPlayer.play(CryPlayer.cryUrlFor(pokemonId))
    }

    fun retry() {
        loadPokemonDetail()
        loadEncounters()
    }

    override fun onCleared() {
        super.onCleared()
        cryPlayer.release()
    }
}
