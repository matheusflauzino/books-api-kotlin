# Books API

Uma API REST simples para gerenciamento de livros desenvolvida em Kotlin com Spring Boot.

## 📋 Descrição

Esta API permite realizar operações CRUD (Create, Read, Update, Delete) em uma coleção de livros. A aplicação utiliza:

- **Kotlin** como linguagem de programação
- **Spring Boot 3.5.0** como framework
- **Spring Data JPA** para persistência de dados
- **H2 Database** como banco de dados em memória
- **Gradle** como gerenciador de dependências

## 🚀 Tecnologias

- Kotlin 1.9.25
- Spring Boot 3.5.0
- Spring Data JPA
- H2 Database
- Gradle

## 📦 Pré-requisitos

- Java 17 ou superior
- Gradle (opcional, o projeto inclui o Gradle Wrapper)

## 🔧 Instalação e Execução

### 1. Clone o repositório
```bash
git clone <url-do-repositorio>
cd books-api-kotlin
```

### 2. Execute a aplicação
```bash
./gradlew bootRun
```

A aplicação estará disponível em: `http://localhost:8080`

### 3. Console H2 (Opcional)
Para acessar o console do banco de dados H2:
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:booksdb`
- Username: `sa`
- Password: (deixe em branco)

## 📚 Endpoints da API

### Base URL
```
http://localhost:8080/books
```

### 1. Listar Todos os Livros
**GET** `/books`

Retorna uma lista com todos os livros cadastrados.

**Exemplo de resposta:**
```json
[
  {
    "id": 1,
    "title": "O Senhor dos Anéis",
    "author": "J.R.R. Tolkien"
  },
  {
    "id": 2,
    "title": "1984",
    "author": "George Orwell"
  }
]
```

**cURL:**
```bash
curl -X GET http://localhost:8080/books
```

### 2. Buscar Livro por ID
**GET** `/books/{id}`

Retorna um livro específico pelo seu ID.

**Exemplo de resposta:**
```json
{
  "id": 1,
  "title": "O Senhor dos Anéis",
  "author": "J.R.R. Tolkien"
}
```

**cURL:**
```bash
curl -X GET http://localhost:8080/books/1
```

### 3. Criar Novo Livro
**POST** `/books`

Cria um novo livro na base de dados.

**Corpo da requisição:**
```json
{
  "title": "Dom Casmurro",
  "author": "Machado de Assis"
}
```

**Exemplo de resposta (201 Created):**
```json
{
  "id": 3,
  "title": "Dom Casmurro",
  "author": "Machado de Assis"
}
```

**cURL:**
```bash
curl -X POST http://localhost:8080/books \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Dom Casmurro",
    "author": "Machado de Assis"
  }'
```

### 4. Atualizar Livro
**PUT** `/books/{id}`

Atualiza um livro existente pelo seu ID.

**Corpo da requisição:**
```json
{
  "title": "Dom Casmurro - Edição Especial",
  "author": "Machado de Assis"
}
```

**Exemplo de resposta:**
```json
{
  "id": 3,
  "title": "Dom Casmurro - Edição Especial",
  "author": "Machado de Assis"
}
```

**cURL:**
```bash
curl -X PUT http://localhost:8080/books/3 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Dom Casmurro - Edição Especial",
    "author": "Machado de Assis"
  }'
```

### 5. Deletar Livro
**DELETE** `/books/{id}`

Remove um livro da base de dados pelo seu ID.

**Exemplo de resposta:** `204 No Content`

**cURL:**
```bash
curl -X DELETE http://localhost:8080/books/3
```

## 🏗️ Estrutura do Projeto

```
src/
├── main/
│   ├── kotlin/com/example/books_api/
│   │   ├── BooksApiApplication.kt      # Classe principal da aplicação
│   │   ├── controller/
│   │   │   └── BookController.kt       # Controlador REST
│   │   ├── model/
│   │   │   └── Book.kt                 # Modelo de dados
│   │   └── repository/
│   │       └── BookRepository.kt       # Repositório JPA
│   └── resources/
│       └── application.properties      # Configurações da aplicação
└── test/
    └── kotlin/com/example/books_api/
        └── BooksApiApplicationTests.kt # Testes da aplicação
```

## 📝 Modelo de Dados

```kotlin
data class Book(
    val id: Long = 0,        // ID único do livro (auto-gerado)
    val title: String,       // Título do livro
    val author: String       // Autor do livro
)
```

## 🧪 Testes

Para executar os testes:

```bash
./gradlew test
```

## 📊 Banco de Dados

A aplicação utiliza o H2 Database em memória, o que significa que:
- Os dados são perdidos quando a aplicação é reiniciada
- Não é necessário configuração adicional de banco de dados
- Ideal para desenvolvimento e testes

## 🔍 Códigos de Status HTTP

- `200 OK` - Requisição bem-sucedida
- `201 Created` - Recurso criado com sucesso
- `204 No Content` - Recurso deletado com sucesso
- `404 Not Found` - Recurso não encontrado
- `500 Internal Server Error` - Erro interno do servidor

## 🤝 Contribuição

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes. 