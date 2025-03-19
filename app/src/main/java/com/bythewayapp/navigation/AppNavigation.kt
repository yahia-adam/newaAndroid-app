package com.bythewayapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bythewayapp.ui.screens.PrivyLoginScreen
import com.bythewayapp.ui.screens.HomeScreen

enum class Screen {
    HOME,
    PRIVY_LOGIN,
}

sealed class NavigationItem(val route: String) {
    data object Home: NavigationItem(Screen.HOME.name)
    data object PrivyLogin: NavigationItem(Screen.PRIVY_LOGIN.name)
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
        composable(NavigationItem.Home.route) {
            HomeScreen()
        }
        composable(NavigationItem.PrivyLogin.route) {
            PrivyLoginScreen(
                onLoginSuccess = {
                    navController.navigate(NavigationItem.Home.route)
                }
            )
        }

    }
}