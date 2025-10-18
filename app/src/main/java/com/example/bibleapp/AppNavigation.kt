package com.example.bibleapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.bibleapp.screens.BookScreen
import com.example.bibleapp.screens.HomeScreen
import com.example.bibleapp.screens.CategoryScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            HomeScreen(navController)
        }

        composable("category") {
            CategoryScreen(navController)
        }

        composable ("book") {
            BookScreen("Клещь рояль", navController)
        }
    }
}
