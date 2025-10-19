package com.example.bibleapp.screens


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import data.local.BookEntity
import com.example.bibleapp.BookRepository
import data.local.BookDao
import kotlinx.coroutines.launch
import data.local.AppDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    navController: NavHostController,
    category: String
) {
    val context = LocalContext.current

    // ИСПРАВЛЕННАЯ СТРОКА: Правильно получаем DAO через AppDatabase
    val repository = remember {
        val dao = AppDatabase.getDatabase(context).bookDao() // <--- Создали БД и получили DAO
        BookRepository(dao)                                 // <--- Передали DAO в Репозиторий
    }

    var books by remember { mutableStateOf<List<BookEntity>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Загружаем книги в LaunchedEffect (т.к. getBooksByCategory — suspend)
    LaunchedEffect(key1 = category) {
        loading = true
        errorMessage = null
        try {
            val list = repository.getBooksByCategory(category)
            books = list
        } catch (e: Exception) {
            errorMessage = e.message ?: "Ошибка загрузки"
        } finally {
            loading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = category) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp)
        ) {
            when {
                loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
                }
                errorMessage != null -> {
                    Text(
                        text = errorMessage ?: "Неизвестная ошибка",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
                    )
                }
                books.isEmpty() -> {
                    Text("В этой категории пока нет книг", modifier = Modifier.align(androidx.compose.ui.Alignment.TopStart))
                }
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(books) { book ->
                            Card(modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(book.title, style = MaterialTheme.typography.titleMedium)
                                    Text(book.description, style = MaterialTheme.typography.bodyMedium)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Автор: ${book.author}", style = MaterialTheme.typography.bodySmall)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Button(onClick = { navController.navigate("book/${book.title}") }) {
                                        Text("Открыть")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

