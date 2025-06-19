package com.example.books_api.controller

import com.example.books_api.model.Book
import com.example.books_api.repository.BookRepository
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/books")
class BookController(private val repository: BookRepository) {

    @GetMapping
    fun getAll(): List<Book> = repository.findAll()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): Book =
        repository.findById(id).orElseThrow { RuntimeException("Book not found") }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody book: Book): Book = repository.save(book)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody book: Book): Book {
        if (!repository.existsById(id)) throw RuntimeException("Book not found")
        return repository.save(book.copy(id = id))
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) {
        if (!repository.existsById(id)) throw RuntimeException("Book not found")
        repository.deleteById(id)
    }
}
