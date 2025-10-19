package com.example.bibleapp.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import data.local.BookEntity // <-- Используем нашу Entity вместо BookDetails
import data.local.AppDatabase // <-- Для доступа к базе данных
import com.example.bibleapp.BookRepository // <-- Ваш класс Репозитория
import kotlinx.coroutines.launch
import com.example.bibleapp.R

// УДАЛЯЕМ старую data class BookDetails
// УДАЛЯЕМ старую функцию getSampleBookDetails

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BookScreen(bookTitle: String, navController: NavHostController) {

    val context = LocalContext.current

    // 1. Инициализируем репозиторий (как мы делали в CategoryScreen)
    val repository = remember {
        val dao = AppDatabase.getDatabase(context).bookDao()
        BookRepository(dao)
    }

    // 2. Состояние для хранения данных книги
    var bookEntity by remember { mutableStateOf<BookEntity?>(null) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // 3. Загружаем данные из Room при первом запуске экрана
    LaunchedEffect(key1 = bookTitle) {
        loading = true
        errorMessage = null
        bookEntity = null
        try {
            // Выполняем запрос к БД по названию книги
            val book = repository.getBookByTitle(bookTitle)
            bookEntity = book // Сохраняем результат
        } catch (e: Exception) {
            errorMessage = "Ошибка загрузки данных: ${e.message}"
        } finally {
            loading = false
        }
    }

    // Выходим, если данных нет, чтобы избежать NullPointerException
    val currentBook = bookEntity

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentBook?.title ?: bookTitle) }, // Показываем название или заглушку
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                loading -> {
                    // Показываем индикатор загрузки
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                errorMessage != null -> {
                    // Показываем ошибку
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                currentBook == null -> {
                    // Книга не найдена
                    Text(
                        text = "Книга с названием \"$bookTitle\" не найдена.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    // 4. Основной контент (когда книга успешно загружена)
                    BookDetailsContent(book = currentBook, navController = navController)
                }
            }
        }
    }
}

// Выносим отображение содержимого в отдельную Composable-функцию
@Composable
fun BookDetailsContent(book: BookEntity, navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // 2. БЛОК ОБЛОЖКИ И ОСНОВНОЙ ИНФОРМАЦИИ
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Изображение обложки
            Image(
                // Используем coverResId из BookEntity
                painter = painterResource(id = book.coverResId),
                contentDescription = "Обложка книги ${book.title}",
                modifier = Modifier
                    .size(120.dp, 180.dp)
                    .padding(end = 16.dp)
            )

            // Колонка с метаданными
            Column {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("Автор: ${book.author}", style = MaterialTheme.typography.bodyLarge)
                Text("Год издания: ${book.year}", style = MaterialTheme.typography.bodyMedium)
                Text("Категория: ${book.category}", style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Divider()

        // 3. БЛОК ДОПОЛНИТЕЛЬНОЙ ИНФОРМАЦИИ
        Text("Сводная информация", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        // NOTE: BookEntity не содержит symbolCount, поэтому я его удалил.
        // Если он нужен, добавьте его в BookEntity.
        InfoRow(label = "Страниц", value = "${book.pageCount}")

        Spacer(modifier = Modifier.height(24.dp))
        Divider()

        // 4. КРАТКОЕ ОПИСАНИЕ
        Text("Краткое описание", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(book.description, style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Назад")
        }
    }
}

// ... InfoRow остается прежним ...
@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}