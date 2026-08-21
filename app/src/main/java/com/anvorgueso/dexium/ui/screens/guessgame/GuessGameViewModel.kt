package com.anvorgueso.dexium.ui.screens.guessgame

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anvorgueso.dexium.core.audio.CryPlayer
import com.anvorgueso.dexium.core.database.dao.GenerationDao
import com.anvorgueso.dexium.core.database.dao.PokemonDao
import com.anvorgueso.dexium.core.database.entity.PokemonEntity
import com.anvorgueso.dexium.domain.repository.HighScoreRepository
import com.anvorgueso.dexium.domain.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RoundResult(
    val pokemonId: Int,
    val pokemonName: String,
    val imageUrl: String,
    val selectedAnswer: String,
    val isCorrect: Boolean
)

data class GuessGameUiState(
    val isLoading: Boolean = true,
    val currentRound: Int = 0,
    val totalRounds: Int = 10,
    val pokemonId: Int = 0,
    val pokemonName: String = "",
    val artworkUrl: String = "",
    val cryUrl: String = "",
    val options: List<String> = emptyList(),
    val selectedOption: String? = null,
    val showFeedback: Boolean = false,
    val isCorrect: Boolean = false,
    val score: Int = 0,
    val roundResults: List<RoundResult> = emptyList(),
    val isPlayingCry: Boolean = false,
    val isGameOver: Boolean = false,
    val highScore: Int? = null,
    val isNewRecord: Boolean = false,
    val showExitDialog: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class GuessGameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val pokemonDao: PokemonDao,
    private val generationDao: GenerationDao,
    private val pokemonRepository: PokemonRepository,
    private val highScoreRepository: HighScoreRepository,
    private val cryPlayer: CryPlayer
) : ViewModel() {

    private val generationIdsArg: String = savedStateHandle["generationIds"] ?: "all"

    private val _uiState = MutableStateFlow(GuessGameUiState())
    val uiState: StateFlow<GuessGameUiState> = _uiState.asStateFlow()

    private var pokemonPool: List<PokemonEntity> = emptyList()
    private var gameRounds: List<PokemonEntity> = emptyList()

    init {
        initializeGame()
        observeCryPlayback()
    }

    private fun observeCryPlayback() {
        viewModelScope.launch {
            cryPlayer.isPlaying.collect { playing ->
                _uiState.update { it.copy(isPlayingCry = playing) }
            }
        }
    }

    private fun initializeGame() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }


            val count = pokemonDao.getCountByCategory("NATIONAL")
            if (count < 50) {
                try {
                    pokemonRepository.precachePokemonNames()
                } catch (_: Exception) {}
            }


            val generationIds = parseGenerationIds()
            pokemonPool = if (generationIds.isEmpty()) {
                pokemonDao.getAllByCategory("NATIONAL")
            } else {
                generationIds.flatMap { genId ->
                    pokemonDao.getByGenerationAndCategory(genId, "NATIONAL")
                }
            }.filter { it.name.isNotBlank() }

            if (pokemonPool.size < 3) {
                _uiState.update {
                    it.copy(isLoading = false, error = "no_data")
                }
                return@launch
            }

            val roundCount = minOf(10, pokemonPool.size)
            gameRounds = pokemonPool.shuffled().take(roundCount)

            _uiState.update { it.copy(totalRounds = roundCount) }

            loadRound(0)
        }
    }

    private fun parseGenerationIds(): List<Int> {
        if (generationIdsArg == "all") return emptyList()
        return generationIdsArg.split(",").mapNotNull { it.trim().toIntOrNull() }
    }

    private fun loadRound(roundIndex: Int) {
        if (roundIndex >= gameRounds.size) {
            _uiState.update { it.copy(isLoading = false) }
            finishGame()
            return
        }

        val pokemon = gameRounds[roundIndex]
        val wrongOptions = pokemonPool
            .filter { it.id != pokemon.id }
            .shuffled()
            .take(2)
            .map { it.name }
        val options = (wrongOptions + pokemon.name).shuffled()

        val cryUrl = CryPlayer.cryUrlFor(pokemon.id)
        val artworkUrl = pokemon.hdSpriteUrl
            ?: "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${pokemon.id}.png"

        _uiState.update {
            it.copy(
                isLoading = false,
                currentRound = roundIndex,
                pokemonId = pokemon.id,
                pokemonName = pokemon.name,
                artworkUrl = artworkUrl,
                cryUrl = cryUrl,
                options = options,
                selectedOption = null,
                showFeedback = false,
                isCorrect = false
            )
        }
    }

    fun onOptionSelected(option: String) {
        val state = _uiState.value
        if (state.showFeedback || state.isLoading) return

        val isCorrect = option == state.pokemonName
        val newScore = if (isCorrect) state.score + 1 else state.score
        val result = RoundResult(
            pokemonId = state.pokemonId,
            pokemonName = state.pokemonName,
            imageUrl = state.artworkUrl,
            selectedAnswer = option,
            isCorrect = isCorrect
        )

        _uiState.update {
            it.copy(
                selectedOption = option,
                showFeedback = true,
                isCorrect = isCorrect,
                score = newScore,
                roundResults = it.roundResults + result
            )
        }

        viewModelScope.launch {
            delay(1800)
            advanceToNextRound()
        }
    }

    private fun advanceToNextRound() {
        val nextRound = _uiState.value.currentRound + 1
        if (nextRound >= _uiState.value.totalRounds) {
            finishGame()
        } else {
            loadRound(nextRound)
        }
    }

    /** Records the run before flipping to the result screen, so it can show the standing best. */
    private fun finishGame() {
        val state = _uiState.value
        if (state.isGameOver) return

        viewModelScope.launch {
            // A game that never dealt a round must not overwrite a real record with a zero,
            // so that case only reads the standing best.
            val result = if (state.roundResults.isEmpty()) {
                highScoreRepository.getForMode(generationIdsArg)?.let {
                    HighScoreRepository.SubmitResult(it, isNewRecord = false)
                }
            } else {
                highScoreRepository.submit(
                    rawMode = generationIdsArg,
                    score = state.score,
                    total = state.totalRounds
                )
            }
            _uiState.update {
                it.copy(
                    isGameOver = true,
                    highScore = result?.record?.score,
                    isNewRecord = result?.isNewRecord ?: false
                )
            }
        }
    }

    fun playCry() {
        cryPlayer.play(_uiState.value.cryUrl)
    }

    fun showExitDialog() {
        _uiState.update { it.copy(showExitDialog = true) }
    }

    fun dismissExitDialog() {
        _uiState.update { it.copy(showExitDialog = false) }
    }

    override fun onCleared() {
        super.onCleared()
        cryPlayer.release()
    }
}
