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
import com.example.bibleapp.screens.AuthorsScreen
import com.example.bibleapp.screens.AuthorDetailsScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            HomeScreen(navController)
        }

        composable(
            route = "category/{categoryName}",
            arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("categoryName") ?: "Все"
            CategoryScreen(navController, category)
        }

        composable(
            route = "book/{bookTitle}",
            arguments = listOf(navArgument("bookTitle") { type = NavType.StringType })
        ) { backStackEntry ->
            val bookTitle = backStackEntry.arguments?.getString("bookTitle") ?: "Неизвестная книга"
            BookScreen(bookTitle, navController)
        }

        composable("authors") {
            AuthorsScreen(navController = navController)
        }

        composable("author/{authorName}",
            arguments = listOf(navArgument("authorName") { type = NavType.StringType })
        ) { backStackEntry ->
            val authorNameEncoded = backStackEntry.arguments?.getString("authorName") ?: return@composable
            AuthorDetailsScreen(navController = navController, authorNameEncoded = authorNameEncoded)
        }
    }
}