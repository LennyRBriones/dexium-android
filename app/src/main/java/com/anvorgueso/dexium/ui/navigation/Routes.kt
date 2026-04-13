package com.anvorgueso.dexium.ui.navigation

object Routes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val DETAIL = "detail/{pokemonId}"
    const val ABOUT = "about"


    const val GUESS_FLOW = "guess_flow"
    const val GUESS_GENERATION_SELECT = "guess/generation_select"
    const val GUESS_CUSTOM_SELECT = "guess/custom_select"
    const val GUESS_GAME = "guess/game/{generationIds}"
    fun detail(pokemonId: Int) = "detail/$pokemonId"
    fun guessGame(generationIds: String) = "guess/game/$generationIds"
}
