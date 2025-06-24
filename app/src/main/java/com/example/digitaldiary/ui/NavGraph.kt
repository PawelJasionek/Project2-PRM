package com.example.digitaldiary.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.digitaldiary.ui.home.HomeScreen
import com.example.digitaldiary.viewmodel.HomeViewModel

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            val viewModel = HomeViewModel() // tymczasowo bez Hilt
            HomeScreen(viewModel = viewModel)
        }

        // Dodasz później np.:
        // composable("create") { CreateEntryScreen(...) }
        // composable("detail/{entryId}") { ... }
    }
}
