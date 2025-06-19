package com.example.books_api.controller

import com.example.books_api.model.Book
import com.example.books_api.repository.BookRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

@WebMvcTest(BookController::class)
class BookControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var bookRepository: BookRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var sampleBook: Book
    private lateinit var sampleBookList: List<Book>

    @BeforeEach
    fun setUp() {
        sampleBook = Book(id = 1L, title = "Dom Casmurro", author = "Machado de Assis")
        sampleBookList = listOf(
            Book(id = 1L, title = "Dom Casmurro", author = "Machado de Assis"),
            Book(id = 2L, title = "O Senhor dos Anéis", author = "J.R.R. Tolkien"),
            Book(id = 3L, title = "1984", author = "George Orwell")
        )
    }

    @Test
    fun `should return all books`() {
        // Given
        `when`(bookRepository.findAll()).thenReturn(sampleBookList)

        // When & Then
        mockMvc.perform(get("/books"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].title").value("Dom Casmurro"))
            .andExpect(jsonPath("$[0].author").value("Machado de Assis"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].title").value("O Senhor dos Anéis"))
            .andExpect(jsonPath("$[1].author").value("J.R.R. Tolkien"))
            .andExpect(jsonPath("$[2].id").value(3))
            .andExpect(jsonPath("$[2].title").value("1984"))
            .andExpect(jsonPath("$[2].author").value("George Orwell"))

        verify(bookRepository, times(1)).findAll()
    }

    @Test
    fun `should return empty list when no books`() {
        // Given
        `when`(bookRepository.findAll()).thenReturn(emptyList())

        // When & Then
        mockMvc.perform(get("/books"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isEmpty)

        verify(bookRepository, times(1)).findAll()
    }

    @Test
    fun `should return book by id when found`() {
        // Given
        val bookId = 1L
        `when`(bookRepository.findById(bookId)).thenReturn(Optional.of(sampleBook))

        // When & Then
        mockMvc.perform(get("/books/$bookId"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Dom Casmurro"))
            .andExpect(jsonPath("$.author").value("Machado de Assis"))

        verify(bookRepository, times(1)).findById(bookId)
    }

    @Test
    fun `should throw exception when book not found`() {
        // Given
        val bookId = 999L
        `when`(bookRepository.findById(bookId)).thenReturn(Optional.empty())

        // When & Then
        mockMvc.perform(get("/books/$bookId"))
            .andExpect(status().isInternalServerError)

        verify(bookRepository, times(1)).findById(bookId)
    }

    @Test
    fun `should create new book successfully`() {
        // Given
        val newBook = Book(title = "Grande Sertão: Veredas", author = "Guimarães Rosa")
        val savedBook = newBook.copy(id = 4L)
        `when`(bookRepository.save(any(Book::class.java))).thenReturn(savedBook)

        // When & Then
        mockMvc.perform(
            post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newBook))
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(4))
            .andExpect(jsonPath("$.title").value("Grande Sertão: Veredas"))
            .andExpect(jsonPath("$.author").value("Guimarães Rosa"))

        verify(bookRepository, times(1)).save(any(Book::class.java))
    }

    @Test
    fun `should accept empty fields (no validation)`() {
        // Given
        val invalidBook = Book(title = "", author = "")

        // When & Then
        mockMvc.perform(
            post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidBook))
        )
            .andExpect(status().isCreated) // Spring Boot não valida automaticamente, então retorna 201

        verify(bookRepository, times(1)).save(any(Book::class.java))
    }

    @Test
    fun `should update existing book successfully`() {
        // Given
        val bookId = 1L
        val updatedBook = Book(title = "Dom Casmurro - Edição Especial", author = "Machado de Assis")
        val savedBook = updatedBook.copy(id = bookId)
        
        `when`(bookRepository.existsById(bookId)).thenReturn(true)
        `when`(bookRepository.save(any(Book::class.java))).thenReturn(savedBook)

        // When & Then
        mockMvc.perform(
            put("/books/$bookId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedBook))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Dom Casmurro - Edição Especial"))
            .andExpect(jsonPath("$.author").value("Machado de Assis"))

        verify(bookRepository, times(1)).existsById(bookId)
        verify(bookRepository, times(1)).save(any(Book::class.java))
    }

    @Test
    fun `should throw exception when updating non-existent book`() {
        // Given
        val bookId = 999L
        val updatedBook = Book(title = "Livro Inexistente", author = "Autor Desconhecido")
        
        `when`(bookRepository.existsById(bookId)).thenReturn(false)

        // When & Then
        mockMvc.perform(
            put("/books/$bookId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedBook))
        )
            .andExpect(status().isInternalServerError)

        verify(bookRepository, times(1)).existsById(bookId)
        verify(bookRepository, never()).save(any(Book::class.java))
    }

    @Test
    fun `should delete existing book successfully`() {
        // Given
        val bookId = 1L
        `when`(bookRepository.existsById(bookId)).thenReturn(true)
        doNothing().`when`(bookRepository).deleteById(bookId)

        // When & Then
        mockMvc.perform(delete("/books/$bookId"))
            .andExpect(status().isNoContent)

        verify(bookRepository, times(1)).existsById(bookId)
        verify(bookRepository, times(1)).deleteById(bookId)
    }

    @Test
    fun `should throw exception when deleting non-existent book`() {
        // Given
        val bookId = 999L
        `when`(bookRepository.existsById(bookId)).thenReturn(false)

        // When & Then
        mockMvc.perform(delete("/books/$bookId"))
            .andExpect(status().isInternalServerError)

        verify(bookRepository, times(1)).existsById(bookId)
        verify(bookRepository, never()).deleteById(any())
    }

    @Test
    fun `should handle invalid JSON`() {
        // Given
        val invalidJson = "{ invalid json }"

        // When & Then
        mockMvc.perform(
            post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson)
        )
            .andExpect(status().isBadRequest)

        verify(bookRepository, never()).save(any(Book::class.java))
    }
} 