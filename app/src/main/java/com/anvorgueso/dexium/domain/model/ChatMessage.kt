package com.anvorgueso.dexium.domain.model

import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val isFromUser: Boolean,
    val pokemonResults: List<Pokemon> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false
)
