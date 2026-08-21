package com.anvorgueso.dexium.ui.navigation

object Routes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val DETAIL = "detail/{pokemonId}?shiny={shiny}"
    const val ABOUT = "about"
    const val AI_CHAT = "ai_chat"

    const val TEAM_LIST = "team/list"
    const val TEAM_BUILDER = "team/builder?teamId={teamId}"
    /** 0 opens an empty builder; a real id opens that team for editing. */
    fun teamBuilder(teamId: Long = 0L) = "team/builder?teamId=$teamId"
    // Distinct segment on purpose: "team/{teamId}" would compete with "team/list" in the
    // route matcher.
    const val TEAM_DETAIL = "team/detail/{teamId}"
    fun teamDetail(teamId: Long) = "team/detail/$teamId"

    const val GUESS_GENERATION_SELECT = "guess/generation_select"
    const val GUESS_CUSTOM_SELECT = "guess/custom_select"
    const val GUESS_GAME = "guess/game/{generationIds}"
    /** [shiny] carries the Shiny Dex through to the detail screen so its sprites match the card. */
    fun detail(pokemonId: Int, shiny: Boolean = false) = "detail/$pokemonId?shiny=$shiny"
    fun guessGame(generationIds: String) = "guess/game/$generationIds"
}
