package com.anvorgueso.dexium.ui.navigation

import kotlinx.serialization.Serializable

object Routes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val DETAIL = "detail/{pokemonId}"
    const val ABOUT = "about"

    fun detail(pokemonId: Int) = "detail/$pokemonId"
}
