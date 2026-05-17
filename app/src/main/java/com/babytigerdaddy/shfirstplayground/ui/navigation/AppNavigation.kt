package com.babytigerdaddy.shfirstplayground.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.babytigerdaddy.shfirstplayground.ui.screen.episode.EpisodeDetailScreen
import com.babytigerdaddy.shfirstplayground.ui.screen.episode.EpisodeListScreen
import com.babytigerdaddy.shfirstplayground.ui.screen.onboarding.OnboardingScreen
import com.babytigerdaddy.shfirstplayground.ui.screen.question.QuestionV2Screen

object OnboardingRoute {
    const val PATH = "onboarding"
}

object EpisodeListRoute {
    const val PATH = "episodes"
}

object EpisodeDetailRoute {
    const val ARG_EPISODE_ID = "episodeId"
    const val PATH = "episode/{$ARG_EPISODE_ID}"
    fun build(episodeId: String): String = "episode/$episodeId"
}

object QuestionV2Route {
    const val ARG_EPISODE_ID = "episodeId"
    const val PATH = "questions/{$ARG_EPISODE_ID}"
    fun build(episodeId: String): String = "questions/$episodeId"
}

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = OnboardingRoute.PATH) {
        composable(OnboardingRoute.PATH) {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(EpisodeListRoute.PATH) {
                        popUpTo(OnboardingRoute.PATH) { inclusive = true }
                    }
                },
            )
        }
        composable(EpisodeListRoute.PATH) {
            EpisodeListScreen(
                onEpisodeClick = { episodeId ->
                    navController.navigate(EpisodeDetailRoute.build(episodeId))
                },
            )
        }
        composable(EpisodeDetailRoute.PATH) {
            EpisodeDetailScreen(
                onBack = { navController.popBackStack() },
                onStartQuestions = { episodeId ->
                    navController.navigate(QuestionV2Route.build(episodeId))
                },
            )
        }
        composable(QuestionV2Route.PATH) {
            QuestionV2Screen(onBack = { navController.popBackStack() })
        }
    }
}
