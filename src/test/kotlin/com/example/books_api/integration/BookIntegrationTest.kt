package com.example.books_api.integration

import com.example.books_api.model.Book
import com.example.books_api.repository.BookRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.junit.jupiter.api.Assertions.*

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookIntegrationTest {

    @Autowired
    private lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    private lateinit var bookRepository: BookRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
        bookRepository.deleteAll()
    }

    @Test
    fun `deve criar, buscar, atualizar e deletar livro através da API completa`() {
        // 1. Criar livro
        val newBook = Book(title = "Grande Sertão: Veredas", author = "Guimarães Rosa")
        
        val createResponse = mockMvc.perform(
            post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newBook))
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.title").value("Grande Sertão: Veredas"))
            .andExpect(jsonPath("$.author").value("Guimarães Rosa"))
            .andReturn()

        val createdBookJson = createResponse.response.contentAsString
        val createdBook = objectMapper.readValue(createdBookJson, Book::class.java)
        val bookId = createdBook.id

        // 2. Buscar livro criado
        mockMvc.perform(get("/books/$bookId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(bookId))
            .andExpect(jsonPath("$.title").value("Grande Sertão: Veredas"))
            .andExpect(jsonPath("$.author").value("Guimarães Rosa"))

        // 3. Atualizar livro
        val updatedBook = Book(title = "Grande Sertão: Veredas - Edição Comentada", author = "Guimarães Rosa")
        
        mockMvc.perform(
            put("/books/$bookId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedBook))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(bookId))
            .andExpect(jsonPath("$.title").value("Grande Sertão: Veredas - Edição Comentada"))
            .andExpect(jsonPath("$.author").value("Guimarães Rosa"))

        // 4. Verificar se foi atualizado
        mockMvc.perform(get("/books/$bookId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("Grande Sertão: Veredas - Edição Comentada"))

        // 5. Deletar livro
        mockMvc.perform(delete("/books/$bookId"))
            .andExpect(status().isNoContent)

        // 6. Verificar se foi deletado
        mockMvc.perform(get("/books/$bookId"))
            .andExpect(status().isInternalServerError)
    }

    @Test
    fun `deve listar todos os livros criados`() {
        // Given
        val books = listOf(
            Book(title = "Dom Casmurro", author = "Machado de Assis"),
            Book(title = "O Senhor dos Anéis", author = "J.R.R. Tolkien"),
            Book(title = "1984", author = "George Orwell")
        )

        // Criar livros
        books.forEach { book ->
            mockMvc.perform(
                post("/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(book))
            )
                .andExpect(status().isCreated)
        }

        // When & Then
        mockMvc.perform(get("/books"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(3))
            .andExpect(jsonPath("$[0].title").value("Dom Casmurro"))
            .andExpect(jsonPath("$[1].title").value("O Senhor dos Anéis"))
            .andExpect(jsonPath("$[2].title").value("1984"))
    }

    @Test
    fun `deve retornar erro ao buscar livro inexistente`() {
        mockMvc.perform(get("/books/999"))
            .andExpect(status().isInternalServerError)
    }

    @Test
    fun `deve retornar erro ao atualizar livro inexistente`() {
        val book = Book(title = "Livro Inexistente", author = "Autor Desconhecido")
        
        mockMvc.perform(
            put("/books/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book))
        )
            .andExpect(status().isInternalServerError)
    }

    @Test
    fun `deve retornar erro ao deletar livro inexistente`() {
        mockMvc.perform(delete("/books/999"))
            .andExpect(status().isInternalServerError)
    }

    @Test
    fun `deve lidar com JSON inválido`() {
        mockMvc.perform(
            post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `deve lidar com requisições sem Content-Type`() {
        val book = Book(title = "Teste", author = "Autor")
        
        mockMvc.perform(
            post("/books")
                .content(objectMapper.writeValueAsString(book))
        )
            .andExpect(status().isUnsupportedMediaType)
    }

    @Test
    fun `deve lidar com método HTTP não suportado`() {
        mockMvc.perform(patch("/books/1"))
            .andExpect(status().isMethodNotAllowed)
    }

    @Test
    fun `deve lidar com endpoint inexistente`() {
        mockMvc.perform(get("/books/invalid/endpoint"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `deve manter consistência dos dados após operações CRUD`() {
        // 1. Criar livro
        val book = Book(title = "Teste de Consistência", author = "Autor Teste")
        
        val createResponse = mockMvc.perform(
            post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book))
        )
            .andExpect(status().isCreated)
            .andReturn()

        val createdBook = objectMapper.readValue(createResponse.response.contentAsString, Book::class.java)
        val bookId = createdBook.id

        // 2. Verificar se está no banco
        assertTrue(bookRepository.existsById(bookId))
        assertEquals(1, bookRepository.count())

        // 3. Atualizar livro
        val updatedBook = book.copy(title = "Teste de Consistência - Atualizado")
        
        mockMvc.perform(
            put("/books/$bookId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedBook))
        )
            .andExpect(status().isOk)

        // 4. Verificar se foi atualizado no banco
        val foundBook = bookRepository.findById(bookId)
        assertTrue(foundBook.isPresent)
        assertEquals("Teste de Consistência - Atualizado", foundBook.get().title)

        // 5. Deletar livro
        mockMvc.perform(delete("/books/$bookId"))
            .andExpect(status().isNoContent)

        // 6. Verificar se foi removido do banco
        assertFalse(bookRepository.existsById(bookId))
        assertEquals(0, bookRepository.count())
    }

    @Test
    fun `deve lidar com múltiplas operações simultâneas`() {
        // Criar múltiplos livros
        val books = (1..5).map { 
            Book(title = "Livro $it", author = "Autor $it") 
        }

        books.forEach { book ->
            mockMvc.perform(
                post("/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(book))
            )
                .andExpect(status().isCreated)
        }

        // Verificar se todos foram criados
        mockMvc.perform(get("/books"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(5))

        // Verificar no banco
        assertEquals(5, bookRepository.count())
    }

    @Test
    fun `deve lidar com caracteres especiais nos dados`() {
        val bookWithSpecialChars = Book(
            title = "Dom Casmurro: Memórias Póstumas de Brás Cubas",
            author = "Machado de Assis (1839-1908)"
        )

        val createResponse = mockMvc.perform(
            post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookWithSpecialChars))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.title").value("Dom Casmurro: Memórias Póstumas de Brás Cubas"))
            .andExpect(jsonPath("$.author").value("Machado de Assis (1839-1908)"))
            .andReturn()

        val createdBook = objectMapper.readValue(createResponse.response.contentAsString, Book::class.java)
        
        // Verificar se foi salvo corretamente
        mockMvc.perform(get("/books/${createdBook.id}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("Dom Casmurro: Memórias Póstumas de Brás Cubas"))
            .andExpect(jsonPath("$.author").value("Machado de Assis (1839-1908)"))
    }
} 