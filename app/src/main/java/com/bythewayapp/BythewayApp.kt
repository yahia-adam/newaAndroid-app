package com.bythewayapp

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.bythewayapp.ui.theme.screens.HomeScreen
import com.bythewayapp.ui.viewModels.HomeViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.bythewayapp.navigation.AppNavHost
import com.bythewayapp.navigation.NavigationItem
import com.bythewayapp.ui.screens.PrivyLoginEmailStartScreenContent
import com.bythewayapp.ui.screens.PrivyLoginScreen
import com.bythewayapp.ui.viewModels.PrivyLoginViewModel

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
                modifier = Modifier.padding(innerPadding),
                navController = navController,
                startDestination = NavigationItem.PrivyLogin.route
            )
        }
    }

}