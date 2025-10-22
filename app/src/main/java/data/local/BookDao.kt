package data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BookDao {
    @Query("SELECT * FROM books ORDER BY title ASC")
    suspend fun getAllBooks(): List<BookEntity>

    @Query("SELECT * FROM books WHERE category = :category")
    suspend fun getBooksByCategory(category: String): List<BookEntity>

    @Query("SELECT * FROM books WHERE title = :title LIMIT 1")
    suspend fun getBookByTitle(title: String): BookEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Query("SELECT * FROM books ORDER BY RANDOM() LIMIT :count")
    suspend fun getRandomBooks(count: Int): List<BookEntity>

    @Query("SELECT DISTINCT author FROM books ORDER BY author ASC")
    suspend fun getAllAuthors(): List<String>

    @Query("SELECT * FROM books WHERE author = :authorName ORDER BY title ASC")
    suspend fun getBooksByAuthor(authorName: String): List<BookEntity>

    @Query("SELECT * FROM books ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomBook(): BookEntity?
}
