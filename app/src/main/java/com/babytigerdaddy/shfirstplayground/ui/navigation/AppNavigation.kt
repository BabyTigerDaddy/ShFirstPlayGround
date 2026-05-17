package com.babytigerdaddy.shfirstplayground.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.babytigerdaddy.shfirstplayground.ui.screen.v3.HomeV3Screen
import com.babytigerdaddy.shfirstplayground.ui.screen.v3.OnboardingV3Screen

object OnboardingV3Route {
    const val PATH = "v3/onboarding"
}

object HomeV3Route {
    const val PATH = "v3/home"
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
