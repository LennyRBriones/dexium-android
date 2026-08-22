package com.anvorgueso.dexium.ui.screens.aichat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anvorgueso.dexium.core.util.Resource
import com.anvorgueso.dexium.domain.model.ChatMessage
import com.anvorgueso.dexium.domain.repository.AiChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AiChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isProcessing: Boolean = false,
    val hasWelcomeInitialized: Boolean = false
)

@HiltViewModel
class AiChatViewModel @Inject constructor(
    private val aiChatRepository: AiChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiChatUiState())
    val uiState: StateFlow<AiChatUiState> = _uiState.asStateFlow()

    fun initWelcomeMessage(welcomeText: String) {
        if (_uiState.value.hasWelcomeInitialized) return
        _uiState.update {
            it.copy(
                hasWelcomeInitialized = true,
                messages = listOf(
                    ChatMessage(
                        content = welcomeText,
                        isFromUser = false
                    )
                )
            )
        }
    }

    fun onInputChange(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun sendMessage() {
        val text = _uiState.value.inputText.trim()
        if (text.isEmpty() || _uiState.value.isProcessing) return

        val userMessage = ChatMessage(content = text, isFromUser = true)
        val loadingMessage = ChatMessage(content = "", isFromUser = false, isLoading = true)

        _uiState.update {
            it.copy(
                messages = it.messages + userMessage + loadingMessage,
                inputText = "",
                isProcessing = true
            )
        }

        viewModelScope.launch {
            val result = aiChatRepository.identifyPokemon(text)
            _uiState.update { state ->
                val withoutLoading = state.messages.dropLast(1)
                val responseMessage = when (result) {
                    is Resource.Success -> {
                        val pokemon = result.data ?: emptyList()
                        ChatMessage(
                            content = "",
                            isFromUser = false,
                            pokemonResults = pokemon,
                            isError = pokemon.isEmpty()
                        )
                    }
                    is Resource.Error -> ChatMessage(
                        content = result.message ?: "UNKNOWN_ERROR",
                        isFromUser = false,
                        isError = true
                    )
                    is Resource.Loading -> ChatMessage(
                        content = "",
                        isFromUser = false,
                        isLoading = true
                    )
                }
                state.copy(
                    messages = withoutLoading + responseMessage,
                    isProcessing = false
                )
            }
        }
    }
}
