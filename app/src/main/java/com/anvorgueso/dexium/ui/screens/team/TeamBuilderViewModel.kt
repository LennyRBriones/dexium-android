package com.anvorgueso.dexium.ui.screens.team

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anvorgueso.dexium.core.util.Constants
import com.anvorgueso.dexium.core.util.Resource
import com.anvorgueso.dexium.domain.model.DexCategory
import com.anvorgueso.dexium.domain.model.Pokemon
import com.anvorgueso.dexium.domain.model.Team
import com.anvorgueso.dexium.domain.repository.PokemonRepository
import com.anvorgueso.dexium.domain.repository.TeamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TeamBuilderUiState(
    val teamName: String = "",
    val members: List<Pokemon> = emptyList(),
    val searchQuery: String = "",
    val results: List<Pokemon> = emptyList(),
    val isSearching: Boolean = false,
    val isSaving: Boolean = false,
    val savedId: Long? = null,
    val validationError: String? = null,
    val isEditing: Boolean = false
) {
    val isFull: Boolean get() = members.size >= Team.MAX_MEMBERS
    val canSave: Boolean get() = members.isNotEmpty() && teamName.isNotBlank() && !isSaving
}

@HiltViewModel
class TeamBuilderViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val pokemonRepository: PokemonRepository,
    private val teamRepository: TeamRepository
) : ViewModel() {

    /** 0 means "new team"; anything else is the team being edited. */
    private val editingId: Long = savedStateHandle.get<Long>("teamId") ?: 0L

    private val _uiState = MutableStateFlow(TeamBuilderUiState(isEditing = editingId != 0L))
    val uiState: StateFlow<TeamBuilderUiState> = _uiState

    private val queryFlow = MutableStateFlow("")
    private var searchJob: Job? = null

    init {
        observeQuery()
        if (editingId != 0L) loadExisting()
    }

    private fun loadExisting() {
        viewModelScope.launch {
            val team = teamRepository.getTeam(editingId) ?: return@launch
            _uiState.update {
                it.copy(teamName = team.name, members = team.members)
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeQuery() {
        queryFlow
            .debounce(Constants.SEARCH_DEBOUNCE_MS)
            .drop(1)
            .onEach { runSearch(it) }
            .launchIn(viewModelScope)
    }

    private fun runSearch(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            _uiState.update { it.copy(results = emptyList(), isSearching = false) }
            return
        }
        searchJob = viewModelScope.launch {
            pokemonRepository
                .searchPokemon(query, DexCategory.NATIONAL, null, null)
                .collect { resource ->
                    when (resource) {
                        is Resource.Loading -> _uiState.update { it.copy(isSearching = true) }
                        is Resource.Success -> _uiState.update {
                            it.copy(results = resource.data ?: emptyList(), isSearching = false)
                        }
                        is Resource.Error -> _uiState.update {
                            it.copy(results = emptyList(), isSearching = false)
                        }
                    }
                }
        }
    }

    fun onNameChange(value: String) =
        _uiState.update { it.copy(teamName = value, validationError = null) }

    fun onQueryChange(value: String) {
        _uiState.update { it.copy(searchQuery = value) }
        queryFlow.value = value
    }

    /** Duplicates are allowed in real teams, but the cap is not. */
    fun addMember(pokemon: Pokemon) {
        val state = _uiState.value
        if (state.isFull) {
            _uiState.update { it.copy(validationError = "TEAM_FULL") }
            return
        }
        _uiState.update { it.copy(members = it.members + pokemon, validationError = null) }
    }

    fun removeMemberAt(index: Int) = _uiState.update {
        it.copy(members = it.members.filterIndexed { i, _ -> i != index })
    }

    fun save() {
        val state = _uiState.value
        when {
            state.members.isEmpty() -> {
                _uiState.update { it.copy(validationError = "NEEDS_MEMBER") }
                return
            }
            state.teamName.isBlank() -> {
                _uiState.update { it.copy(validationError = "NEEDS_NAME") }
                return
            }
        }
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val id = teamRepository.saveTeam(
                id = editingId,
                name = state.teamName,
                memberIds = state.members.map { it.id }
            )
            _uiState.update { it.copy(isSaving = false, savedId = id) }
        }
    }
}
