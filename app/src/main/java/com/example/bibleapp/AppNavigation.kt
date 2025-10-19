package com.example.bibleapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.bibleapp.screens.BookScreen
import com.example.bibleapp.screens.HomeScreen
import com.example.bibleapp.screens.CategoryScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            HomeScreen(navController)
        }

        // 1. ПЕРЕДАЧА ПАРАМЕТРА КАТЕГОРИИ
        composable(
            route = "category/{categoryName}",
            arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("categoryName") ?: "Все"
            CategoryScreen(navController, category)
        }

        // 2. ПЕРЕДАЧА ПАРАМЕТРА НАЗВАНИЯ КНИГИ
        composable(
            route = "book/{bookTitle}",
            arguments = listOf(navArgument("bookTitle") { type = NavType.StringType })
        ) { backStackEntry ->
            val bookTitle = backStackEntry.arguments?.getString("bookTitle") ?: "Неизвестная книга"
            BookScreen(bookTitle, navController)
        }
    }
}