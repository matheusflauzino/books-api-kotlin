package com.example.books_api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BooksApiApplication

fun main(args: Array<String>) {
	runApplication<BooksApiApplication>(*args)
}
