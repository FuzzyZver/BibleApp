package com.example.bibleapp

import data.local.BookDao
import data.local.BookEntity

class BookRepository(private val dao: BookDao) {

    suspend fun getBooks(): List<BookEntity> = dao.getAllBooks()

    suspend fun getBooksByCategory(category: String): List<BookEntity> = dao.getBooksByCategory(category)

    suspend fun getBookByTitle(title: String): BookEntity? = dao.getBookByTitle(title)

    suspend fun insertBooks(books: List<BookEntity>) = dao.insertBooks(books)

    suspend fun getNRandomBooks(count: Int): List<BookEntity> = dao.getRandomBooks(count)

    suspend fun getAllBooks(): List<BookEntity> = dao.getAllBooks()

    suspend fun getAllAuthors(): List<String> = dao.getAllAuthors()

    suspend fun getBooksByAuthor(authorName: String): List<BookEntity> = dao.getBooksByAuthor(authorName)

}

