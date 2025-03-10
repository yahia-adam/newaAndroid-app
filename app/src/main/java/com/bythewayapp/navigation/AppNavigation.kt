package com.bythewayapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.bythewayapp.ui.screens.PrivyLoginEmailEndScreen
import com.bythewayapp.ui.screens.PrivyLoginEmailStartScreen
import com.bythewayapp.ui.theme.screens.HomeScreen

enum class Screen {
    HOME,
    PRIVY_LOGIN_EMAIL_START,
    PRIVY_LOGIN_EMAIL_END,
}

sealed class NavigationItem(val route: String) {

    data object Home: NavigationItem(Screen.HOME.name)

    data object PrivyLoginEmailStart: NavigationItem(Screen.PRIVY_LOGIN_EMAIL_START.name)

    data object PrivyLoginEmailEnd: NavigationItem(Screen.PRIVY_LOGIN_EMAIL_END.name)
}


@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavigationItem.PrivyLoginEmailStart.route) {
            PrivyLoginEmailStartScreen(navController = navController)
        }
        composable(
            NavigationItem.PrivyLoginEmailEnd.route,
            arguments = listOf(
                navArgument("userEmail") {type = NavType.StringType}
            )
        ) { backStackEntry ->
            val userEmail = backStackEntry.arguments?.getString("userEmail") ?: ""
            PrivyLoginEmailEndScreen(
                navController = navController,
                email = userEmail
            )
        }
    }
}