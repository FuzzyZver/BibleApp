package com.example.bibleapp.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import data.local.AppDatabase
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
    var allBooks by remember { mutableStateOf<List<BookEntity>>(emptyList()) }
    var searchText by remember { mutableStateOf("") }
    var quoteBook by remember { mutableStateOf<BookEntity?>(null) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        try {
            randomBooks = repository.getNRandomBooks(5)
            allBooks = repository.getAllBooks()
            quoteBook = repository.getRandomBook()
        } catch (e: Exception) {
            println("Ошибка загрузки данных: ${e.message}")
        }
    }

    val filteredBooks = remember(searchText, allBooks) {
        if (searchText.isBlank()) emptyList()
        else allBooks.filter { it.title.contains(searchText, ignoreCase = true) }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxHeight(),
                drawerContainerColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = "Категории",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(20.dp)
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

                Divider(modifier = Modifier.padding(vertical = 15.dp))
                Text(
                    text = "Прочее",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(20.dp)
                )
                DrawerButton("Авторы") {
                    coroutineScope.launch { drawerState.close() }
                    navController.navigate("authors")
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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.surface,
                                    MaterialTheme.colorScheme.background
                                )
                            )
                        )
                        .padding(innerPadding)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {


                        if (searchText.isNotBlank()) {
                            SearchContent(filteredBooks, navController)
                        } else {
                            Text(
                                text = "📖 Советуем к прочтению",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.padding(vertical = 24.dp)
                            )

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) {
                                items(randomBooks) { book ->
                                    BookRecommendationCard(book = book, navController = navController)
                                }
                            }
                        }
                        if (searchText.isNotBlank()) {
                            SearchContent(filteredBooks, navController)
                        } else {
                            val currentQuoteBook = quoteBook
                            if (currentQuoteBook != null) {
                                QuoteOfTheDay(
                                    quoteText = currentQuoteBook.quote,
                                    bookTitle = currentQuoteBook.title,
                                    onBookClick = {
                                        navController.navigate("book/${currentQuoteBook.title}")
                                    }
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }

                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    onMenuClick: () -> Unit,
    searchText: String,
    onSearchTextChange: (String) -> Unit
) {
    TopAppBar(
        title = {
            Text(
                "LibrApp",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Filled.Menu, contentDescription = "Меню категорий")
            }
        },
        actions = {
            OutlinedTextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                placeholder = { Text("Поиск книг...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Поиск") },
                singleLine = true,
                modifier = Modifier
                    .width(230.dp)
                    .padding(end = 12.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp)
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@Composable
fun SearchContent(books: List<BookEntity>, navController: NavHostController) {
    Column(modifier = Modifier.fillMaxSize().padding(vertical = 16.dp)) {
        if (books.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("По вашему запросу ничего не найдено.",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray))
            }
        } else {
            Text(
                "Найдено книг: ${books.size}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            books.forEach { book ->
                ListItem(
                    headlineContent = { Text(book.title, fontWeight = FontWeight.Medium) },
                    supportingContent = { Text("Автор: ${book.author}") },
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { navController.navigate("book/${book.title}") }
                        .padding(vertical = 4.dp)
                )
                Divider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            }
        }
    }
}

@Composable
fun BookRecommendationCard(book: BookEntity, navController: NavHostController) {
    Card(
        onClick = { navController.navigate("book/${book.title}") },
        modifier = Modifier
            .size(width = 120.dp, height = 200.dp)
            .clip(RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = book.coverResId),
                contentDescription = "Обложка книги ${book.title}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            )
            Text(
                text = book.title,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.padding(6.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun QuoteOfTheDay(
    quoteText: String,
    bookTitle: String,
    onBookClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Цитата дня",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.padding(top = 8.dp))

            Text(
                text = "$quoteText",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Spacer(modifier = Modifier.padding(top = 12.dp))

            TextButton(
                onClick = onBookClick
            ) {
                Text(
                    text = "Открыть книгу: $bookTitle",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}


@Composable
fun DrawerButton(text: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f))
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
