package com.yourdomain.freecharades.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.yourdomain.freecharades.ui.screens.categoryDetail.CategoryDetailScreen
import com.yourdomain.freecharades.ui.screens.game.GameScreen
import com.yourdomain.freecharades.ui.screens.menu.MenuScreen
import com.yourdomain.freecharades.ui.screens.privacy.PrivacyPolicyScreen
import com.yourdomain.freecharades.ui.screens.settings.SettingsScreen

@Composable
fun AppNavHost(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Routes.MENU
    ) {
        composable(Routes.MENU) {
            MenuScreen(
                onCategoryClick = { categoryId ->
                    navController.navigate(Routes.categoryDetail(categoryId))
                },
                onSettingsClick = {
                    navController.navigate(Routes.SETTINGS)
                },
                onPrivacyClick = {
                    navController.navigate(Routes.PRIVACY)
                }
            )
        }

        composable(Routes.CATEGORY_DETAIL) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            CategoryDetailScreen(
                categoryId = categoryId,
                onBack = { navController.popBackStack() },
                onPlay = {
                    navController.navigate(Routes.game(categoryId))
                }
            )
        }

        composable(Routes.GAME) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            GameScreen(
                categoryId = categoryId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.PRIVACY) {
            PrivacyPolicyScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}