package com.babytigerdaddy.shfirstplayground.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.babytigerdaddy.shfirstplayground.ui.screen.HomeScreen
import com.babytigerdaddy.shfirstplayground.ui.screen.QuestionScreen

object HomeRoute {
    const val PATH = "home"
}

object QuestionRoute {
    const val ARG_CONTENT_ID = "contentId"
    const val PATH = "question/{$ARG_CONTENT_ID}"
    fun build(contentId: String): String = "question/$contentId"
}

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = HomeRoute.PATH) {
        composable(HomeRoute.PATH) {
            HomeScreen(
                onContentClick = { content ->
                    navController.navigate(QuestionRoute.build(content.id))
                },
            )
        }
        composable(QuestionRoute.PATH) {
            QuestionScreen(onBack = { navController.popBackStack() })
        }
    }
}
