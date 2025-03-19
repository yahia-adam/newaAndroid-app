package com.bythewayapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.compose.rememberNavController
import com.bythewayapp.navigation.AppNavHost
import com.bythewayapp.navigation.NavigationItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BythewayApp() {
    val navController = rememberNavController()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->

        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            AppNavHost(
                modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),                navController = navController,
                startDestination = NavigationItem.PrivyLogin.route
            )
        }
    }
}