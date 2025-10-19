package com.example.bibleapp

import data.local.BookDao
import data.local.BookEntity

class BookRepository(private val dao: BookDao) {

    suspend fun getBooks(): List<BookEntity> = dao.getAllBooks()

    suspend fun getBooksByCategory(category: String): List<BookEntity> = dao.getBooksByCategory(category)

    suspend fun getBookByTitle(title: String): BookEntity? = dao.getBookByTitle(title)

    suspend fun insertBooks(books: List<BookEntity>) = dao.insertBooks(books)
}

