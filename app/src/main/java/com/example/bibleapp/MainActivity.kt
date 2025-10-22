package com.example.bibleapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.navigation.compose.rememberNavController
import com.example.bibleapp.ui.theme.BibleAppTheme
import data.local.AppDatabase
import data.local.getInitialBooks
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val repository by lazy {
        val db = AppDatabase.getDatabase(applicationContext)
        BookRepository(db.bookDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeDatabase()

        setContent {
            BibleAppTheme {
                val navController = rememberNavController()
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(navController)
                }
            }
        }
    }

    private fun initializeDatabase() {
        lifecycleScope.launch(Dispatchers.IO) {
            val currentBooks = repository.getBooks()

            if (currentBooks.isEmpty()) {

                val initialBooks = getInitialBooks()

                repository.insertBooks(initialBooks)
            }
        }
    }
}
