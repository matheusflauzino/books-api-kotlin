package com.example.books_api.repository

import com.example.books_api.model.Book
import org.springframework.data.jpa.repository.JpaRepository

interface BookRepository : JpaRepository<Book, Long>
