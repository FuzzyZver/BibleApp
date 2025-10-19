package com.example.bibleapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.bibleapp.BookRepository
import data.local.AppDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val repository = remember {
        val dao = AppDatabase.getDatabase(context).bookDao()
        BookRepository(dao)
    }

    var authors by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) {
        authors = repository.getAllAuthors()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Авторы") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            if (authors.isEmpty()) {
                Text("Список авторов пуст.", modifier = Modifier.padding(16.dp))
            } else {
                LazyColumn {
                    items(authors) { author ->
                        ListItem(
                            headlineContent = { Text(author) },
                            modifier = Modifier.clickable {
                                // При клике переходим на экран деталей автора
                                navController.navigate("author/${author}")
                            }
                        )
                        Divider()
                    }
                }
            }
        }
    }
}