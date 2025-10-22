package com.example.bibleapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.bibleapp.BookRepository
import data.local.AppDatabase
import data.local.BookEntity
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorDetailsScreen(navController: NavHostController, authorNameEncoded: String) {
    val authorName = remember { URLDecoder.decode(authorNameEncoded, StandardCharsets.UTF_8.toString()) }

    val context = LocalContext.current
    val repository = remember {
        val dao = AppDatabase.getDatabase(context).bookDao()
        BookRepository(dao)
    }

    var books by remember { mutableStateOf<List<BookEntity>>(emptyList()) }

    LaunchedEffect(authorName) {
        books = repository.getBooksByAuthor(authorName)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(authorName, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Книги автора",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (books.isEmpty()) {
                Text("Книги этого автора не найдены.")
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(books) { book ->
                        BookRecommendationCard(book = book, navController = navController)
                    }
                }
            }
        }
    }
}