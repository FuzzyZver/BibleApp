package com.example.bibleapp.screens


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val itemsList = listOf("Клещь рояль", "BКлещь рояль", "Клещь рояль", "Клещь рояль", "Клещь рояль")

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
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("LibraryApp") })
            },
            content = { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                if (drawerState.isClosed) drawerState.open()
                                else drawerState.close()
                            }
                        },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("Открыть меню категорий")
                    }

                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(itemsList) { item ->
                            Card(
                                modifier = Modifier
                                    .width(150.dp)
                                    .height(100.dp)
                            ) {
                                DrawerButton(item) {
                                    coroutineScope.launch { drawerState.close() }
                                    navController.navigate("book/$item")
                                }
                            }
                        }
                    }
                }
            }
        )
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
