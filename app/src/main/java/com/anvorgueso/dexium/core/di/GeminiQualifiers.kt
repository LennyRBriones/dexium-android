package com.anvorgueso.dexium.core.di

import javax.inject.Qualifier

/**
 * The AI chat and the team advisor need different Gemini configurations — the chat forces a
 * JSON response mime type, the advisor must return prose — so the two models are injected
 * under separate qualifiers instead of sharing one singleton.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PokemonIdentifierModel

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TeamAdvisorModel
