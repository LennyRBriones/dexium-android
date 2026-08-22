package com.anvorgueso.dexium.ui.screens.team

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anvorgueso.dexium.core.util.AppLanguage
import com.anvorgueso.dexium.core.util.Resource
import com.anvorgueso.dexium.core.util.TypeChart
import com.anvorgueso.dexium.domain.model.Team
import com.anvorgueso.dexium.domain.model.TeamTypeCoverage
import com.anvorgueso.dexium.domain.repository.TeamRepository
import com.anvorgueso.dexium.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

data class TeamDetailUiState(
    val team: Team? = null,
    val coverage: List<TeamTypeCoverage> = emptyList(),
    val isLoading: Boolean = true,
    val advice: String? = null,
    val isAsking: Boolean = false,
    val adviceError: String? = null,
    val languageCode: String = AppLanguage.DEFAULT.code
)

@HiltViewModel
class TeamDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val teamRepository: TeamRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val teamId: Long = savedStateHandle.get<Long>("teamId") ?: 0L

    private val _uiState = MutableStateFlow(TeamDetailUiState())
    val uiState: StateFlow<TeamDetailUiState> = _uiState

    init {
        load()
        observeLanguage()
    }

    private fun observeLanguage() {
        viewModelScope.launch {
            userPreferencesRepository.userPreferences.collect { prefs ->
                _uiState.update { it.copy(languageCode = prefs.language.code) }
            }
        }
    }

    /** Observed, so returning from the editor shows the updated roster without a reload. */
    private fun load() {
        viewModelScope.launch {
            teamRepository.observeTeam(teamId).collect { team ->
                _uiState.update {
                    it.copy(
                        team = team,
                        coverage = team?.let { TypeChart.teamCoverage(it.members) } ?: emptyList(),
                        isLoading = false,
                        // Advice describes the old roster, so drop it when the team changes.
                        advice = if (team?.members == it.team?.members) it.advice else null
                    )
                }
            }
        }
    }

    fun askGemini() {
        val team = _uiState.value.team ?: return
        if (_uiState.value.isAsking) return

        _uiState.update { it.copy(isAsking = true, adviceError = null, advice = null) }
        viewModelScope.launch {
            // The model is told which language to answer in rather than being left to infer it
            // from the prompt, which is mostly English type names. Follows the app's content
            // language, not the device, so the advice matches everything around it.
            val language = Locale.forLanguageTag(_uiState.value.languageCode)
                .getDisplayLanguage(Locale.ENGLISH)
                .ifBlank { "English" }
            when (val result = teamRepository.analyzeTeam(team, language)) {
                is Resource.Success -> _uiState.update {
                    it.copy(isAsking = false, advice = result.data, adviceError = null)
                }
                is Resource.Error -> _uiState.update {
                    it.copy(isAsking = false, adviceError = result.message ?: "UNKNOWN_ERROR")
                }
                is Resource.Loading -> Unit
            }
        }
    }
}
