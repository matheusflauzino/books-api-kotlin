package com.example.books_api.repository

import com.example.books_api.model.Book
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
class BookRepositoryTest {

    @Autowired
    private lateinit var bookRepository: BookRepository

    @Autowired
    private lateinit var entityManager: TestEntityManager

    private lateinit var book1: Book
    private lateinit var book2: Book
    private lateinit var book3: Book

    @BeforeEach
    fun setUp() {
        // Limpar o banco antes de cada teste
        bookRepository.deleteAll()
        entityManager.flush()
        entityManager.clear()

        // Criar dados de teste
        book1 = Book(title = "Dom Casmurro", author = "Machado de Assis")
        book2 = Book(title = "O Senhor dos Anéis", author = "J.R.R. Tolkien")
        book3 = Book(title = "1984", author = "George Orwell")
    }

    @Test
    fun `should save book successfully`() {
        // When
        val savedBook = bookRepository.save(book1)

        // Then
        assertNotNull(savedBook.id)
        assertEquals(book1.title, savedBook.title)
        assertEquals(book1.author, savedBook.author)
        assertTrue(savedBook.id > 0)
    }

    @Test
    fun `should find book by id`() {
        // Given
        val savedBook = bookRepository.save(book1)
        val bookId = savedBook.id

        // When
        val foundBook = bookRepository.findById(bookId)

        // Then
        assertTrue(foundBook.isPresent)
        assertEquals(book1.title, foundBook.get().title)
        assertEquals(book1.author, foundBook.get().author)
    }

    @Test
    fun `should return empty optional when book not found`() {
        // When
        val foundBook = bookRepository.findById(999L)

        // Then
        assertFalse(foundBook.isPresent)
    }

    @Test
    fun `should find all books`() {
        // Given
        bookRepository.save(book1)
        bookRepository.save(book2)
        bookRepository.save(book3)

        // When
        val allBooks = bookRepository.findAll()

        // Then
        assertEquals(3, allBooks.size)
        assertTrue(allBooks.any { it.title == "Dom Casmurro" })
        assertTrue(allBooks.any { it.title == "O Senhor dos Anéis" })
        assertTrue(allBooks.any { it.title == "1984" })
    }

    @Test
    fun `should return empty list when no books`() {
        // When
        val allBooks = bookRepository.findAll()

        // Then
        assertTrue(allBooks.isEmpty())
    }

    @Test
    fun `should update existing book`() {
        // Given
        val savedBook = bookRepository.save(book1)
        val updatedBook = savedBook.copy(title = "Dom Casmurro - Edição Especial")

        // When
        val result = bookRepository.save(updatedBook)

        // Then
        assertEquals(savedBook.id, result.id)
        assertEquals("Dom Casmurro - Edição Especial", result.title)
        assertEquals(book1.author, result.author)
    }

    @Test
    fun `should delete book by id`() {
        // Given
        val savedBook = bookRepository.save(book1)
        val bookId = savedBook.id

        // When
        bookRepository.deleteById(bookId)

        // Then
        assertFalse(bookRepository.existsById(bookId))
        assertEquals(0, bookRepository.count())
    }

    @Test
    fun `should check if book exists`() {
        // Given
        val savedBook = bookRepository.save(book1)
        val bookId = savedBook.id

        // When & Then
        assertTrue(bookRepository.existsById(bookId))
        assertFalse(bookRepository.existsById(999L))
    }

    @Test
    fun `should count total books`() {
        // Given
        bookRepository.save(book1)
        bookRepository.save(book2)

        // When
        val count = bookRepository.count()

        // Then
        assertEquals(2, count)
    }

    @Test
    fun `should delete all books`() {
        // Given
        bookRepository.save(book1)
        bookRepository.save(book2)
        bookRepository.save(book3)

        // When
        bookRepository.deleteAll()

        // Then
        assertEquals(0, bookRepository.count())
        assertTrue(bookRepository.findAll().isEmpty())
    }

    @Test
    fun `should save multiple books`() {
        // Given
        val books = listOf(book1, book2, book3)

        // When
        val savedBooks = bookRepository.saveAll(books)

        // Then
        assertEquals(3, savedBooks.size)
        assertEquals(3, bookRepository.count())
        savedBooks.forEach { assertTrue(it.id > 0) }
    }

    @Test
    fun `should handle books with same title`() {
        // Given
        val book1 = Book(title = "Dom Casmurro", author = "Machado de Assis")
        val book2 = Book(title = "Dom Casmurro", author = "Outro Autor")

        // When
        val savedBook1 = bookRepository.save(book1)
        val savedBook2 = bookRepository.save(book2)

        // Then
        assertNotEquals(savedBook1.id, savedBook2.id)
        assertEquals(2, bookRepository.count())
    }

    @Test
    fun `should handle books with same author`() {
        // Given
        val book1 = Book(title = "Dom Casmurro", author = "Machado de Assis")
        val book2 = Book(title = "Memórias Póstumas", author = "Machado de Assis")

        // When
        val savedBook1 = bookRepository.save(book1)
        val savedBook2 = bookRepository.save(book2)

        // Then
        assertNotEquals(savedBook1.id, savedBook2.id)
        assertEquals(2, bookRepository.count())
    }

    @Test
    fun `should persist data after flush`() {
        // Given
        val book = bookRepository.save(book1)

        // When
        entityManager.flush()
        entityManager.clear()

        // Then
        val foundBook = bookRepository.findById(book.id)
        assertTrue(foundBook.isPresent)
        assertEquals(book.title, foundBook.get().title)
    }

    @Test
    fun `should handle empty strings`() {
        // Given
        val bookWithEmptyFields = Book(title = "", author = "")

        // When
        val savedBook = bookRepository.save(bookWithEmptyFields)

        // Then
        assertNotNull(savedBook.id)
        assertEquals("", savedBook.title)
        assertEquals("", savedBook.author)
    }

    @Test
    fun `should handle long strings`() {
        // Given
        val longTitle = "A".repeat(255)
        val longAuthor = "B".repeat(255)
        val bookWithLongFields = Book(title = longTitle, author = longAuthor)

        // When
        val savedBook = bookRepository.save(bookWithLongFields)

        // Then
        assertNotNull(savedBook.id)
        assertEquals(longTitle, savedBook.title)
        assertEquals(longAuthor, savedBook.author)
    }
} 