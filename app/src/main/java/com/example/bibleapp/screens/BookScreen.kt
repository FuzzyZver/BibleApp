package com.example.bibleapp.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.bibleapp.R // Заглушка, предполагающая, что у вас есть изображение
import com.example.bibleapp.ui.theme.BibleAppTheme // Предполагаем, что у вас есть тема

// --- МОДЕЛЬ ДАННЫХ КНИГИ (чтобы было что показать) ---
data class BookDetails(
    val title: String,
    val author: String,
    val year: Int,
    val symbolCount: Int,
    val pageCount: Int,
    val description: String,
    val coverResourceId: Int // ID ресурса для изображения обложки
)

// --- ГЛАВНЫЙ КОМПОНЕНТ ЭКРАНА КНИГИ ---

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BookScreen(bookTitle: String, navController: NavHostController) {

    // 1. ЗАГЛУШКА ДЛЯ ДАННЫХ:
    // В реальном приложении здесь будет логика:
    // val book = viewModel.getBookDetails(bookTitle) или запрос к БД/API.
    val bookDetails = getSampleBookDetails(bookTitle)

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(bookDetails.title) }) // Название книги в заголовке
        }
    ) { innerPadding ->
        // Используем verticalScroll, чтобы содержимое можно было прокручивать
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()) // Делает контент прокручиваемым
                .padding(16.dp)
        ) {
            // 2. БЛОК ОБЛОЖКИ И ОСНОВНОЙ ИНФОРМАЦИИ
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Изображение обложки
                // ВАЖНО: Требует наличия изображения в папке res/drawable
                Image(
                    painter = painterResource(id = bookDetails.coverResourceId),
                    contentDescription = "Обложка книги ${bookDetails.title}",
                    modifier = Modifier
                        .size(120.dp, 180.dp) // Размер обложки
                        .padding(end = 16.dp)
                )

                // Колонка с метаданными
                Column {
                    Text(
                        text = bookDetails.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Автор: ${bookDetails.author}", style = MaterialTheme.typography.bodyLarge)
                    Text("Год издания: ${bookDetails.year}", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Divider() // Разделительная линия

            // 3. БЛОК ДОПОЛНИТЕЛЬНОЙ ИНФОРМАЦИИ
            Text("Сводная информация", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            InfoRow(label = "Страниц", value = "${bookDetails.pageCount}")
            InfoRow(label = "Символов", value = "${bookDetails.symbolCount}")

            Spacer(modifier = Modifier.height(24.dp))
            Divider()

            // 4. КРАТКОЕ ОПИСАНИЕ
            Text("Краткое описание", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(bookDetails.description, style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(24.dp))

            // Кнопка для возврата (для наглядности)
            Button(onClick = { navController.popBackStack() }) {
                Text("Назад")
            }
        }
    }
}

// --- ВСПОМОГАТЕЛЬНЫЕ КОМПОНЕНТЫ И ЗАГЛУШКИ ---

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

// ФУНКЦИЯ-ЗАГЛУШКА ДЛЯ ПОЛУЧЕНИЯ ДАННЫХ
// ВАЖНО: Для корректной работы этого кода вам нужно создать в папке res/drawable
// файл с названием 'book_cover_placeholder.png' или аналогичный.
fun getSampleBookDetails(title: String): BookDetails {
    // В реальном приложении здесь был бы поиск по базе данных.
    return BookDetails(
        title = title,
        author = "Нарик Нарёк Андреевич",
        year = 2025,
        symbolCount = 50000,
        pageCount = 30,
        description = "Это краткое описание книги, которое обычно занимает несколько параграфов и дает читателю представление о её содержании, темах и основных идеях. В этом разделе мы подробно описываем, о чем идет речь в книге $title.",
        coverResourceId = android.R.drawable.ic_dialog_info // Временно используем стандартную иконку Android
        // coverResourceId = R.drawable.book_cover_placeholder // Используйте это, когда добавите свою картинку
    )
}

// --- ПРЕДПРОСМОТР (Preview) ---
/*
@Preview(showBackground = true)
@Composable
fun BookScreenPreview() {
    BibleAppTheme {
        // Здесь нужен реальный NavController, но для Preview можно использовать заглушку
        // BookScreen(bookTitle = "Евангелие от Иоанна", navController = rememberNavController())
    }
}
*/