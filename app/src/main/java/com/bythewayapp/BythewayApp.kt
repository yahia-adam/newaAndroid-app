package com.bythewayapp

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.bythewayapp.navigation.AppNavHost
import com.bythewayapp.navigation.NavigationItem

@Composable
fun BythewayApp() {
    val navController = rememberNavController()
    
    AppNavHost(
        navController = navController,
        startDestination = NavigationItem.PrivyLogin.route
    )
}