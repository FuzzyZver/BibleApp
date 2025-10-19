package com.example.bibleapp.screens


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import  data.local.AppDatabase
import data.local.BookEntity
import com.example.bibleapp.BookRepository

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current

    val repository = remember {
        val dao = AppDatabase.getDatabase(context).bookDao()
        BookRepository(dao)
    }
    var randomBooks by remember { mutableStateOf<List<BookEntity>>(emptyList()) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Загрузка рандомных книг при открытии экрана
    LaunchedEffect(Unit) {
        // Мы хотим, чтобы при каждом открытии экрана (или при первом запуске)
        // список книг обновлялся
        try {
            // Предполагаем, что эта функция существует в вашем BookRepository
            randomBooks = repository.getNRandomBooks(5) // Загружаем 5 случайных книг
        } catch (e: Exception) {
            // Обработка ошибки загрузки, если нужно
            println("Ошибка загрузки рандомных книг: ${e.message}")
        }
    }

    // Состояние для текста поиска
    var searchText by remember { mutableStateOf("") }

    // Состояние для списка всех книг (понадобится для поиска)
    var allBooks by remember { mutableStateOf<List<BookEntity>>(emptyList()) }

    // Загрузка данных
    LaunchedEffect(Unit) {
        try {
            randomBooks = repository.getNRandomBooks(5)
            // Загружаем все книги для поиска
            allBooks = repository.getAllBooks()
        } catch (e: Exception) {
            println("Ошибка загрузки данных: ${e.message}")
        }
    }

    // Фильтрация списка книг по тексту поиска
    val filteredBooks = remember(searchText, allBooks) {
        if (searchText.isBlank()) {
            emptyList() // Если строка пуста, не показываем результаты поиска
        } else {
            allBooks.filter {
                it.title.contains(searchText, ignoreCase = true)
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Категории",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )

                DrawerButton("Классика") {
                    coroutineScope.launch { drawerState.close() }
                    navController.navigate("category/Классика")
                }

                DrawerButton("История") {
                    coroutineScope.launch { drawerState.close() }
                    navController.navigate("category/История")
                }

                DrawerButton("Наука") {
                    coroutineScope.launch { drawerState.close() }
                    navController.navigate("category/Наука")
                }

                Divider(modifier = Modifier.padding(vertical = 15.dp)) // Разделитель
                Text(
                    text = "Прочее",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )

                DrawerButton("Авторы") {
                    coroutineScope.launch { drawerState.close() }
                    navController.navigate("authors") // Навигация на новый экран
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                HomeTopAppBar(
                    onMenuClick = {
                        coroutineScope.launch {
                            if (drawerState.isClosed) drawerState.open() else drawerState.close()
                        }
                    },
                    searchText = searchText,
                    onSearchTextChange = { searchText = it }
                )
            },
            content = { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {

                    // --- БЛОК РЕЗУЛЬТАТОВ ПОИСКА ---
                    if (searchText.isNotBlank()) {
                        SearchContent(filteredBooks, navController)
                    } else {
                        // --- БЛОК СЛУЧАЙНЫХ КНИГ (Отображается только при пустой строке поиска) ---
                        Text(
                            text = "📖 Советуем к прочтению",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 30.dp)
                        )

                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(randomBooks) { book ->
                                BookRecommendationCard(book = book, navController = navController)
                            }
                        }
                    }
                }
            }
        )
    }
}

// Новый Composable для TopAppBar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    onMenuClick: () -> Unit,
    searchText: String,
    onSearchTextChange: (String) -> Unit
) {
    TopAppBar(
        title = { Text("LibrApp") },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Filled.Menu, contentDescription = "Меню категорий")
            }
        },
        actions = {
            // Поле поиска
            OutlinedTextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                label = { Text("Поиск...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Поиск") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(0.6f) // Занимает 60% доступного места
                    .padding(end = 8.dp)
                    .height(56.dp) // Фиксированная высота для выравнивания
            )
        }
    )
}

// Новый Composable для отображения результатов поиска
@Composable
fun SearchContent(books: List<BookEntity>, navController: NavHostController) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (books.isEmpty()) {
            Text("По вашему запросу ничего не найдено.", style = MaterialTheme.typography.bodyLarge)
        } else {
            Text("Найдено книг: ${books.size}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(Modifier.height(8.dp))

            // Используем LazyColumn для отображения результатов
            // (Вам нужно будет добавить соответствующий импорт: LazyColumn и items)
            // или просто Column с циклом, если список небольшой.
            // Для простоты используем Column, но для больших списков лучше LazyColumn.

            books.forEach { book ->
                ListItem(
                    headlineContent = { Text(book.title) },
                    supportingContent = { Text("Автор: ${book.author}") },
                    modifier = Modifier.clickable {
                        navController.navigate("book/${book.title}")
                    }
                )
                Divider()
            }
        }
    }
}

// Новый Composable для карточки книги в LazyRow
@Composable
fun BookRecommendationCard(book: BookEntity, navController: NavHostController) {
    Card(
        onClick = { navController.navigate("book/${book.title}") }, // Переход на экран книги
        modifier = Modifier.size(width = 100.dp, height = 180.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Изображение обложки
            Image(
                painter = painterResource(id = book.coverResId),
                contentDescription = "Обложка книги ${book.title}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                // Добавьте contentScale, если хотите, чтобы изображение заполняло область
            )
            // Название книги
            Text(
                text = book.title,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(4.dp),
                maxLines = 2
            )
        }
    }
}

@Composable
fun DrawerButton(text: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Text(text)
    }
}
