package com.bythewayapp

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.bythewayapp.navigation.AppNavHost
import com.bythewayapp.navigation.NavigationItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BythewayApp() {
    val navController = rememberNavController()
/*
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->
        Surface(
            modifier = Modifier.fillMaxSize()
        ) { */
    AppNavHost(
        //modifier = Modifier.padding(innerPadding),
        navController = navController,
        startDestination = NavigationItem.Home.route
    )
        //}
    // }
}