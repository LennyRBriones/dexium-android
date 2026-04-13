package com.anvorgueso.dexium.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.anvorgueso.dexium.ui.screens.about.AboutScreen
import com.anvorgueso.dexium.ui.screens.detail.DetailScreen
import com.anvorgueso.dexium.ui.screens.guessgame.CustomGenerationSelectScreen
import com.anvorgueso.dexium.ui.screens.guessgame.GameResultScreen
import com.anvorgueso.dexium.ui.screens.guessgame.GenerationSelectScreen
import com.anvorgueso.dexium.ui.screens.guessgame.GuessGameScreen
import com.anvorgueso.dexium.ui.screens.guessgame.GuessGameViewModel
import com.anvorgueso.dexium.ui.screens.home.HomeScreen
import com.anvorgueso.dexium.ui.screens.onboarding.OnboardingScreen
import com.anvorgueso.dexium.ui.screens.splash.SplashScreen

@Composable
fun DexiumNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        }
    ) {
        composable(
            route = Routes.SPLASH,
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Routes.ONBOARDING) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onPokemonClick = { pokemonId ->
                    navController.navigate(Routes.detail(pokemonId))
                },
                onAboutClick = {
                    navController.navigate(Routes.ABOUT)
                },
                onGuessGameClick = {
                    navController.navigate(Routes.GUESS_GENERATION_SELECT)
                }
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("pokemonId") { type = NavType.IntType })
        ) {
            DetailScreen(
                onBackClick = { navController.popBackStack() },
                onPokemonClick = { pokemonId ->
                    navController.navigate(Routes.detail(pokemonId))
                }
            )
        }

        composable(Routes.ABOUT) {
            AboutScreen(
                onBackClick = { navController.popBackStack() }
            )
        }


        composable(Routes.GUESS_GENERATION_SELECT) {
            GenerationSelectScreen(
                onBackClick = { navController.popBackStack() },
                onGenerationSelected = { generationIds ->
                    navController.navigate(Routes.guessGame(generationIds))
                },
                onCustomClick = {
                    navController.navigate(Routes.GUESS_CUSTOM_SELECT)
                }
            )
        }

        composable(Routes.GUESS_CUSTOM_SELECT) {
            CustomGenerationSelectScreen(
                onBackClick = { navController.popBackStack() },
                onStartGame = { generationIds ->
                    navController.navigate(Routes.guessGame(generationIds))
                }
            )
        }

        composable(
            route = Routes.GUESS_GAME,
            arguments = listOf(navArgument("generationIds") { type = NavType.StringType })
        ) { backStackEntry ->
            val viewModel: GuessGameViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()

            if (uiState.isGameOver) {
                val cachedResults = remember(uiState.score) { uiState.roundResults }

                GameResultScreen(
                    score = uiState.score,
                    total = uiState.totalRounds,
                    roundResults = cachedResults,
                    onPlayAgain = {
                        navController.popBackStack(Routes.GUESS_GENERATION_SELECT, false)
                    },
                    onBackToHome = {
                        navController.popBackStack(Routes.HOME, false)
                    }
                )
            } else {
                GuessGameScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    viewModel = viewModel
                )
            }
        }
    }
}
