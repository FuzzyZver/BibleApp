package data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val author: String,
    val category: String,
    val year: Int,
    val description: String,
    val pageCount: Int,
    val coverResId: Int,
    val quote: String
)

