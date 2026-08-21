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
import com.anvorgueso.dexium.ui.screens.aichat.AiChatScreen
import com.anvorgueso.dexium.ui.screens.detail.DetailScreen
import com.anvorgueso.dexium.ui.screens.guessgame.CustomGenerationSelectScreen
import com.anvorgueso.dexium.ui.screens.guessgame.GameResultScreen
import com.anvorgueso.dexium.ui.screens.guessgame.GenerationSelectScreen
import com.anvorgueso.dexium.ui.screens.guessgame.GuessGameScreen
import com.anvorgueso.dexium.ui.screens.guessgame.GuessGameViewModel
import com.anvorgueso.dexium.ui.screens.team.TeamBuilderScreen
import com.anvorgueso.dexium.ui.screens.team.TeamDetailScreen
import com.anvorgueso.dexium.ui.screens.team.TeamListScreen
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
                onPokemonClick = { pokemonId, shiny ->
                    navController.navigate(Routes.detail(pokemonId, shiny))
                },
                onAboutClick = {
                    navController.navigate(Routes.ABOUT)
                },
                onGuessGameClick = {
                    navController.navigate(Routes.GUESS_GENERATION_SELECT)
                },
                onAiChatClick = {
                    navController.navigate(Routes.AI_CHAT)
                },
                onTeamBuilderClick = {
                    navController.navigate(Routes.TEAM_LIST)
                }
            )
        }

        composable(Routes.TEAM_LIST) {
            TeamListScreen(
                onBackClick = { navController.popBackStack() },
                onCreateClick = { navController.navigate(Routes.teamBuilder()) },
                onTeamClick = { teamId -> navController.navigate(Routes.teamDetail(teamId)) }
            )
        }

        composable(
            route = Routes.TEAM_BUILDER,
            arguments = listOf(
                navArgument("teamId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) {
            TeamBuilderScreen(
                onBackClick = { navController.popBackStack() },
                onSaved = { teamId ->
                    // Replace the builder so Back from the new team lands on the list.
                    navController.navigate(Routes.teamDetail(teamId)) {
                        popUpTo(Routes.TEAM_BUILDER) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Routes.TEAM_DETAIL,
            arguments = listOf(navArgument("teamId") { type = NavType.LongType })
        ) {
            TeamDetailScreen(
                onBackClick = { navController.popBackStack() },
                onEditClick = { teamId -> navController.navigate(Routes.teamBuilder(teamId)) },
                onPokemonClick = { pokemonId, shiny ->
                    navController.navigate(Routes.detail(pokemonId, shiny))
                }
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(
                navArgument("pokemonId") { type = NavType.IntType },
                navArgument("shiny") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) {
            DetailScreen(
                onBackClick = { navController.popBackStack() },
                // Walking the evolution chain keeps the shiny lens, so the whole line stays
                // consistent with the dex the user came from.
                onPokemonClick = { pokemonId, shiny ->
                    navController.navigate(Routes.detail(pokemonId, shiny))
                }
            )
        }

        composable(Routes.ABOUT) {
            AboutScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.AI_CHAT) {
            AiChatScreen(
                onBackClick = { navController.popBackStack() },
                onPokemonClick = { pokemonId ->
                    navController.navigate(Routes.detail(pokemonId, shiny = false))
                }
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
                    highScore = uiState.highScore,
                    isNewRecord = uiState.isNewRecord,
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
