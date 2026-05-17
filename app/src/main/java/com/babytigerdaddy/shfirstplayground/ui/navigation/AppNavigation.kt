package com.babytigerdaddy.shfirstplayground.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.babytigerdaddy.shfirstplayground.ui.screen.v3.HomeV3Screen
import com.babytigerdaddy.shfirstplayground.ui.screen.v3.OnboardingV3Screen

// ---- v3 routes (active) ----

object OnboardingV3Route {
    const val PATH = "v3/onboarding"
}

object HomeV3Route {
    const val PATH = "v3/home"
}

// ---- v2 legacy routes (NavHost reference 끊김. v2 ViewModel SavedStateHandle 호환용으로 보존.
//      다음 cycle 형 v2 일괄 cleanup에서 같이 제거 예정) ----

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
    NavHost(navController = navController, startDestination = OnboardingV3Route.PATH) {
        composable(OnboardingV3Route.PATH) {
            OnboardingV3Screen(
                onFinish = {
                    navController.navigate(HomeV3Route.PATH) {
                        popUpTo(OnboardingV3Route.PATH) { inclusive = true }
                    }
                },
            )
        }
        composable(HomeV3Route.PATH) {
            HomeV3Screen()
        }
    }
}
