package com.anvorgueso.dexium.ui.screens.team

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anvorgueso.dexium.domain.model.Team
import com.anvorgueso.dexium.domain.repository.TeamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TeamListUiState(
    val teams: List<Team> = emptyList(),
    val isLoading: Boolean = true,
    val pendingDeleteId: Long? = null
)

@HiltViewModel
class TeamListViewModel @Inject constructor(
    private val teamRepository: TeamRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TeamListUiState())
    val uiState: StateFlow<TeamListUiState> = _uiState

    init {
        viewModelScope.launch {
            teamRepository.observeTeams().collect { teams ->
                _uiState.update { it.copy(teams = teams, isLoading = false) }
            }
        }
    }

    fun askDelete(id: Long) = _uiState.update { it.copy(pendingDeleteId = id) }

    fun dismissDelete() = _uiState.update { it.copy(pendingDeleteId = null) }

    fun confirmDelete() {
        val id = _uiState.value.pendingDeleteId ?: return
        viewModelScope.launch {
            teamRepository.deleteTeam(id)
            _uiState.update { it.copy(pendingDeleteId = null) }
        }
    }
}
