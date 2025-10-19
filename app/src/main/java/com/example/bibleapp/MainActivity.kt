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
    // 1. Создаем репозиторий как ленивое свойство
    private val repository by lazy {
        val db = AppDatabase.getDatabase(applicationContext)
        BookRepository(db.bookDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ВАЖНО: Вызываем инициализацию сразу после super.onCreate
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

    /**
     * Проверяет, пуста ли БД, и если да, заполняет ее начальными данными.
     */
    private fun initializeDatabase() {
        lifecycleScope.launch(Dispatchers.IO) {
            val currentBooks = repository.getBooks()

            if (currentBooks.isEmpty()) {

                // 3. Вызываем функцию БЕЗ аргумента
                val initialBooks = getInitialBooks()

                // 4. Вставляем книги в базу данных
                repository.insertBooks(initialBooks)
            }
        }
    }
}
