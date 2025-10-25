package com.example.curanova.ui.theme.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.curanova.ui.theme.screens.HomeScreen
import com.example.curanova.ui.theme.screens.UploadScreen
import com.example.curanova.ui.theme.screens.ResultsScreen
import com.example.curanova.ui.theme.screens.HistoryScreen
import com.example.curanova.ui.theme.screens.AboutScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Upload : Screen("upload")
    object Results : Screen("results/{reportId}") {
        fun createRoute(reportId: String) = "results/$reportId"
    }
    object History : Screen("history")
    object About : Screen("about")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(Screen.Upload.route) {
            UploadScreen(navController = navController)
        }

        composable(
            route = Screen.Results.route,
            arguments = listOf(navArgument("reportId") { type = NavType.StringType })
        ) { backStackEntry ->
            val reportId = backStackEntry.arguments?.getString("reportId") ?: ""
            ResultsScreen(
                navController = navController,
                reportId = reportId
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(navController = navController)
        }

        composable(Screen.About.route) {
            AboutScreen(navController = navController)
        }
    }
}