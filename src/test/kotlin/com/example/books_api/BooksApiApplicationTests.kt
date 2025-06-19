package com.example.books_api

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class BooksApiApplicationTests {

	@Autowired
	private lateinit var applicationContext: ApplicationContext

	@Test
	fun `deve carregar o contexto da aplicacao com sucesso`() {
		// Verifica se o contexto da aplicação foi carregado corretamente
		assertNotNull(applicationContext)
	}

	@Test
	fun `deve ter todos os beans necessarios registrados`() {
		// Verifica se os beans principais estão registrados
		// O nome do bean pode variar, então vamos verificar de forma mais flexível
		val beanNames = applicationContext.beanDefinitionNames
		assertTrue(beanNames.any { it.contains("Controller") || it.contains("controller") })
		assertTrue(beanNames.any { it.contains("Repository") || it.contains("repository") })
		assertTrue(beanNames.any { it.contains("ObjectMapper") || it.contains("objectMapper") })
	}

	@Test
	fun `deve ter configuracao de banco de dados para testes`() {
		// Verifica se o datasource está configurado
		assertTrue(applicationContext.containsBean("dataSource"))
	}

	@Test
	fun `deve ter configuracao JPA ativa`() {
		// Verifica se o EntityManager está configurado
		assertTrue(applicationContext.containsBean("entityManagerFactory"))
	}
}
