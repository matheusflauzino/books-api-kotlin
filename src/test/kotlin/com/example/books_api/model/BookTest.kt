package com.example.books_api.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class BookTest {

    @Test
    fun `should create book with default values`() {
        // When
        val book = Book(title = "Dom Casmurro", author = "Machado de Assis")

        // Then
        assertEquals(0L, book.id)
        assertEquals("Dom Casmurro", book.title)
        assertEquals("Machado de Assis", book.author)
    }

    @Test
    fun `should create book with specific id`() {
        // When
        val book = Book(id = 1L, title = "O Senhor dos Anéis", author = "J.R.R. Tolkien")

        // Then
        assertEquals(1L, book.id)
        assertEquals("O Senhor dos Anéis", book.title)
        assertEquals("J.R.R. Tolkien", book.author)
    }

    @Test
    fun `should create copy of book with new values`() {
        // Given
        val originalBook = Book(id = 1L, title = "Dom Casmurro", author = "Machado de Assis")

        // When
        val updatedBook = originalBook.copy(
            title = "Dom Casmurro - Edição Especial",
            author = "Machado de Assis"
        )

        // Then
        assertEquals(1L, updatedBook.id)
        assertEquals("Dom Casmurro - Edição Especial", updatedBook.title)
        assertEquals("Machado de Assis", updatedBook.author)
    }

    @Test
    fun `should create copy of book keeping original values`() {
        // Given
        val originalBook = Book(id = 1L, title = "Dom Casmurro", author = "Machado de Assis")

        // When
        val copiedBook = originalBook.copy()

        // Then
        assertEquals(originalBook.id, copiedBook.id)
        assertEquals(originalBook.title, copiedBook.title)
        assertEquals(originalBook.author, copiedBook.author)
    }

    @Test
    fun `should create copy of book changing only id`() {
        // Given
        val originalBook = Book(id = 1L, title = "Dom Casmurro", author = "Machado de Assis")

        // When
        val copiedBook = originalBook.copy(id = 2L)

        // Then
        assertEquals(2L, copiedBook.id)
        assertEquals(originalBook.title, copiedBook.title)
        assertEquals(originalBook.author, copiedBook.author)
    }

    @Test
    fun `should create copy of book changing only title`() {
        // Given
        val originalBook = Book(id = 1L, title = "Dom Casmurro", author = "Machado de Assis")

        // When
        val copiedBook = originalBook.copy(title = "Dom Casmurro - Versão Digital")

        // Then
        assertEquals(originalBook.id, copiedBook.id)
        assertEquals("Dom Casmurro - Versão Digital", copiedBook.title)
        assertEquals(originalBook.author, copiedBook.author)
    }

    @Test
    fun `should create copy of book changing only author`() {
        // Given
        val originalBook = Book(id = 1L, title = "Dom Casmurro", author = "Machado de Assis")

        // When
        val copiedBook = originalBook.copy(author = "Joaquim Maria Machado de Assis")

        // Then
        assertEquals(originalBook.id, copiedBook.id)
        assertEquals(originalBook.title, copiedBook.title)
        assertEquals("Joaquim Maria Machado de Assis", copiedBook.author)
    }

    @Test
    fun `should have equality based on all fields`() {
        // Given
        val book1 = Book(id = 1L, title = "Dom Casmurro", author = "Machado de Assis")
        val book2 = Book(id = 1L, title = "Dom Casmurro", author = "Machado de Assis")
        val book3 = Book(id = 2L, title = "Dom Casmurro", author = "Machado de Assis")

        // Then
        assertEquals(book1, book2)
        assertNotEquals(book1, book3)
    }

    @Test
    fun `should have consistent hashCode`() {
        // Given
        val book1 = Book(id = 1L, title = "Dom Casmurro", author = "Machado de Assis")
        val book2 = Book(id = 1L, title = "Dom Casmurro", author = "Machado de Assis")

        // Then
        assertEquals(book1.hashCode(), book2.hashCode())
    }

    @Test
    fun `should have readable toString`() {
        // Given
        val book = Book(id = 1L, title = "Dom Casmurro", author = "Machado de Assis")

        // When
        val toString = book.toString()

        // Then
        assertTrue(toString.contains("id=1"))
        assertTrue(toString.contains("title=Dom Casmurro"))
        assertTrue(toString.contains("author=Machado de Assis"))
    }

    @Test
    fun `should handle empty strings`() {
        // When
        val book = Book(title = "", author = "")

        // Then
        assertEquals("", book.title)
        assertEquals("", book.author)
    }

    @Test
    fun `should handle long strings`() {
        // Given
        val longTitle = "A".repeat(255)
        val longAuthor = "B".repeat(255)

        // When
        val book = Book(title = longTitle, author = longAuthor)

        // Then
        assertEquals(longTitle, book.title)
        assertEquals(longAuthor, book.author)
    }

    @Test
    fun `should handle special characters`() {
        // Given
        val titleWithSpecialChars = "Dom Casmurro: Memórias Póstumas de Brás Cubas"
        val authorWithSpecialChars = "Machado de Assis (1839-1908)"

        // When
        val book = Book(title = titleWithSpecialChars, author = authorWithSpecialChars)

        // Then
        assertEquals(titleWithSpecialChars, book.title)
        assertEquals(authorWithSpecialChars, book.author)
    }
} 