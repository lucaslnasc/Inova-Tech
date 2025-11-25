# 🚀 InovaTech Eventos - Backend

Backend da aplicação de gerenciamento de eventos desenvolvido em Java Spring Boot.

## 📋 Pré-requisitos

- Java 21+
- Maven 3.6+
- PostgreSQL (ou Supabase)

## ⚙️ Instalação

1. **Configure o banco de dados** em `src/main/resources/application.properties`

2. **Instale as dependências**:

   ```bash
   ./mvnw clean install
   ```

3. **Execute a aplicação**:
   ```bash
   ./mvnw spring-boot:run
   ```

A API estará disponível em: **http://localhost:8080**

## 📚 Documentação da API

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## 🛠️ Tecnologias

- Java 21
- Spring Boot 3.5.5
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL
- Maven
- Lombok

## 📁 Estrutura

```
src/
├── main/
│   ├── java/com/inovatech/inovatech_eventos/
│   │   ├── config/          # Configurações (Security, CORS)
│   │   ├── controller/      # Controllers REST
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── model/           # Entidades JPA
│   │   ├── repository/      # Repositórios Spring Data
│   │   ├── security/        # JWT e Filtros de Segurança
│   │   └── service/         # Lógica de Negócio
│   └── resources/
│       └── application.properties
└── test/
```

## 🔐 Autenticação

Todas as rotas (exceto `/api/users/login` e `/api/users`) requerem JWT Token:

```
Authorization: Bearer {token}
```

## 📊 Endpoints Principais

- **Users**: `/api/users`
- **Events**: `/api/v1/events`
- **Enrollments**: `/api/v1/enrollments`

Para documentação completa, acesse o Swagger após iniciar a aplicação.
