package com.anvorgueso.dexium.ui.screens.guessgame

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.anvorgueso.dexium.core.database.dao.GenerationDao
import com.anvorgueso.dexium.core.database.dao.PokemonDao
import com.anvorgueso.dexium.core.database.entity.PokemonEntity
import com.anvorgueso.dexium.domain.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    val showExitDialog: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class GuessGameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val pokemonDao: PokemonDao,
    private val generationDao: GenerationDao,
    private val pokemonRepository: PokemonRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val generationIdsArg: String = savedStateHandle["generationIds"] ?: "all"

    private val _uiState = MutableStateFlow(GuessGameUiState())
    val uiState: StateFlow<GuessGameUiState> = _uiState.asStateFlow()

    private var pokemonPool: List<PokemonEntity> = emptyList()
    private var gameRounds: List<PokemonEntity> = emptyList()
    private var exoPlayer: ExoPlayer? = null

    init {
        initializeGame()
    }

    private fun initializeGame() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Ensure data is available
            val count = pokemonDao.getCountByCategory("NATIONAL")
            if (count < 50) {
                try {
                    pokemonRepository.precachePokemonNames()
                } catch (_: Exception) {}
            }

            // Get pokemon pool based on generation selection
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

            initExoPlayer()
            loadRound(0)
        }
    }

    private fun parseGenerationIds(): List<Int> {
        if (generationIdsArg == "all") return emptyList()
        return generationIdsArg.split(",").mapNotNull { it.trim().toIntOrNull() }
    }

    private fun initExoPlayer() {
        exoPlayer = ExoPlayer.Builder(context).build().apply {
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _uiState.update { it.copy(isPlayingCry = isPlaying) }
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED || playbackState == Player.STATE_IDLE) {
                        _uiState.update { it.copy(isPlayingCry = false) }
                    }
                }
            })
        }
    }

    private fun loadRound(roundIndex: Int) {
        if (roundIndex >= gameRounds.size) {
            _uiState.update { it.copy(isGameOver = true, isLoading = false) }
            return
        }

        val pokemon = gameRounds[roundIndex]
        val wrongOptions = pokemonPool
            .filter { it.id != pokemon.id }
            .shuffled()
            .take(2)
            .map { it.name }
        val options = (wrongOptions + pokemon.name).shuffled()

        val cryUrl = "https://raw.githubusercontent.com/PokeAPI/cries/main/cries/pokemon/latest/${pokemon.id}.ogg"
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
            _uiState.update { it.copy(isGameOver = true) }
        } else {
            loadRound(nextRound)
        }
    }

    fun playCry() {
        val state = _uiState.value
        exoPlayer?.let { player ->
            player.stop()
            player.setMediaItem(MediaItem.fromUri(state.cryUrl))
            player.prepare()
            player.play()
        }
    }

    fun showExitDialog() {
        _uiState.update { it.copy(showExitDialog = true) }
    }

    fun dismissExitDialog() {
        _uiState.update { it.copy(showExitDialog = false) }
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer?.release()
        exoPlayer = null
    }
}
