package com.yourdomain.freecharades.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.yourdomain.freecharades.ui.screens.categoryDetail.CategoryDetailScreen
import com.yourdomain.freecharades.ui.screens.game.GameScreen
import com.yourdomain.freecharades.ui.screens.menu.MenuScreen
import com.yourdomain.freecharades.ui.screens.privacy.PrivacyPolicyScreen
import com.yourdomain.freecharades.ui.screens.privacy.CreditsScreen
import com.yourdomain.freecharades.ui.screens.settings.SettingsScreen
import java.util.UUID

@Composable
fun AppNavHost(navController:NavHostController) {
 NavHost(navController,startDestination=Routes.MENU) {
  composable(Routes.MENU) {
   MenuScreen({navController.navigate(Routes.categoryDetail(it))},{navController.navigate(Routes.SETTINGS)},{navController.navigate(Routes.PRIVACY)})
  }
  composable(Routes.CATEGORY_DETAIL) {entry ->
   val id=entry.arguments?.getString("categoryId").orEmpty()
   CategoryDetailScreen(id,{navController.popBackStack()},{navController.navigate(Routes.game(id,UUID.randomUUID().toString()))})
  }
  composable(Routes.GAME) {entry ->
   GameScreen(entry.arguments?.getString("categoryId").orEmpty(),entry.arguments?.getString("sessionId").orEmpty(),{navController.popBackStack()})
  }
  composable(Routes.SETTINGS) {SettingsScreen({navController.popBackStack()},{navController.navigate("document/$it")},{navController.navigate(Routes.CREDITS)})}
  composable(Routes.PRIVACY) {PrivacyPolicyScreen({navController.popBackStack()})}
  composable(Routes.DOCUMENT) {entry ->PrivacyPolicyScreen({navController.popBackStack()},entry.arguments?.getString("document").orEmpty())}
  composable(Routes.CREDITS) {CreditsScreen({navController.popBackStack()})}
 }
}
