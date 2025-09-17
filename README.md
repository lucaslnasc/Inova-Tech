# 📋 INOVATECH EVENTOS - Documentação Completa

## 📝 Índice

1. [Visão Geral](#visão-geral)
2. [Arquitetura](#arquitetura)
3. [Tecnologias](#tecnologias)
4. [Instalação e Configuração](#instalação-e-configuração)
5. [Modelo de Dados](#modelo-de-dados)
6. [APIs REST](#apis-rest)
7. [Segurança](#segurança)
8. [Estrutura do Projeto](#estrutura-do-projeto)
9. [Como Usar](#como-usar)
10. [Testes](#testes)
11. [Deployment](#deployment)
12. [Contribuição](#contribuição)

---

## 🎯 Visão Geral

**InovaTech Eventos** é uma API REST completa para gerenciamento de eventos desenvolvida em Spring Boot. O sistema permite que organizadores criem e gerenciem eventos, enquanto participantes podem se inscrever e acompanhar suas inscrições.

### 🌟 Funcionalidades Principais

- 👥 **Gestão de Usuários**: Cadastro, autenticação e gerenciamento de organizadores e participantes
- 🎪 **Gestão de Eventos**: Criação, edição, exclusão e listagem de eventos
- 📝 **Sistema de Inscrições**: Inscrições em eventos com controle de status
- 🔐 **Autenticação JWT**: Sistema de autenticação seguro baseado em tokens
- 📊 **API RESTful**: Interface padronizada seguindo boas práticas REST
- 📚 **Documentação Swagger**: Documentação automática da API

### 🎭 Tipos de Usuário

- **ORGANIZADOR**: Pode criar, editar e gerenciar eventos
- **PARTICIPANTE**: Pode se inscrever em eventos e gerenciar suas inscrições

---

## 🏗️ Arquitetura

O projeto segue uma **arquitetura em camadas (Layered Architecture)** com separação clara de responsabilidades:

```
┌─────────────────────────────────────────┐
│             CONTROLLER LAYER            │
│   (API REST - Interface HTTP)           │
│   - UserController                      │
│   - EventController                     │
│   - EnrollmentController                │
└─────────────┬───────────────────────────┘
              │
              ▼
┌─────────────────────────────────────────┐
│              SERVICE LAYER              │
│   (Lógica de Negócio)                   │
│   - UserService                         │
│   - EventService                        │
│   - EnrollmentService                   │
│   - EmailService                        │
└─────────────┬───────────────────────────┘
              │
              ▼
┌─────────────────────────────────────────┐
│            REPOSITORY LAYER             │
│   (Acesso a Dados)                      │
│   - UserRepository                      │
│   - EventRepository                     │
│   - EnrollmentRepository                │
└─────────────┬───────────────────────────┘
              │
              ▼
┌─────────────────────────────────────────┐
│              DATABASE                   │
│           (PostgreSQL)                  │
└─────────────────────────────────────────┘
```

### 📦 Padrões de Design Utilizados

- **Repository Pattern**: Para abstração do acesso a dados
- **Service Layer Pattern**: Para encapsular lógica de negócio
- **DTO Pattern**: Para transferência segura de dados
- **Builder Pattern**: Para construção de objetos complexos
- **Factory Pattern**: Para criação de responses padronizadas

---

## 🛠️ Tecnologias

### Backend

- **Java 21**: Linguagem de programação
- **Spring Boot 3.5.5**: Framework principal
- **Spring Security**: Autenticação e autorização
- **Spring Data JPA**: Persistência de dados
- **Hibernate**: ORM (Object-Relational Mapping)
- **PostgreSQL**: Banco de dados relacional
- **JWT (JSON Web Token)**: Autenticação stateless
- **Maven**: Gerenciamento de dependências
- **Lombok**: Redução de código boilerplate
- **BCrypt**: Criptografia de senhas

### Database

- **Supabase (PostgreSQL)**: Banco de dados em nuvem
- **HikariCP**: Pool de conexões

### Documentação & Testes

- **Swagger/OpenAPI 3**: Documentação automática da API
- **JUnit**: Framework de testes
- **Spring Boot Test**: Testes integrados

---

## ⚙️ Instalação e Configuração

### 📋 Pré-requisitos

- Java 21 ou superior
- Maven 3.6+
- PostgreSQL (local ou Supabase)
- IDE (IntelliJ IDEA, VS Code, Eclipse)

### 🚀 Instalação

1. **Clone o repositório**:

   ```bash
   git clone https://github.com/lucaslnasc/Inova-Tech.git
   cd inovatech-eventos
   ```

2. **Configure o banco de dados**:

   - Edite `src/main/resources/application.properties`
   - Configure sua string de conexão PostgreSQL

3. **Instale as dependências**:

   ```bash
   ./mvnw clean install
   ```

4. **Execute a aplicação**:
   ```bash
   ./mvnw spring-boot:run
   ```

### 🔧 Configurações

#### application.properties

```properties
# Banco de dados
spring.datasource.url=jdbc:postgresql://localhost:5432/inovatech_eventos
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT
security.token.secret=SUA_CHAVE_SECRETA_MUITO_FORTE
security.token.expiration=3600000

# Pool de conexões
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
```

---

## 📊 Modelo de Dados

### 👤 User (Usuário)

```sql
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('ORGANIZADOR', 'PARTICIPANTE')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 🎪 Event (Evento)

```sql
CREATE TABLE events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    start_date_time TIMESTAMP NOT NULL,
    end_date_time TIMESTAMP NOT NULL,
    location VARCHAR(255) NOT NULL,
    capacity INTEGER NOT NULL,
    is_active BOOLEAN DEFAULT true,
    organizer_id UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 📝 Enrollment (Inscrição)

```sql
CREATE TABLE enrollments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id),
    event_id UUID NOT NULL REFERENCES events(id),
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'CONFIRMED', 'REJECTED', 'CANCELLED')),
    enrollment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, event_id)
);
```

### 🔗 Relacionamentos

- **User → Event**: Um organizador pode criar vários eventos (1:N)
- **User → Enrollment**: Um usuário pode ter várias inscrições (1:N)
- **Event → Enrollment**: Um evento pode ter várias inscrições (1:N)

---

## 🌐 APIs REST

### Base URL

```
http://localhost:8080/api
```

### 🔐 Autenticação

Todas as rotas (exceto login e registro) requerem autenticação via JWT Bearer Token:

```http
Authorization: Bearer {seu_jwt_token}
```

### 👥 User Controller (`/api/users`)

#### POST /users

**Descrição**: Criar novo usuário

```json
{
  "name": "João Silva",
  "email": "joao@email.com",
  "password": "123456",
  "type": "PARTICIPANTE"
}
```

#### POST /users/login

**Descrição**: Login de usuário

```json
{
  "email": "joao@email.com",
  "password": "123456"
}
```

**Resposta**:

```json
{
  "success": true,
  "message": "Login realizado com sucesso",
  "data": {
    "user": {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "name": "João Silva",
      "email": "joao@email.com",
      "type": "PARTICIPANTE"
    },
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "message": "Login realizado com sucesso"
  }
}
```

#### GET /users

**Descrição**: Listar todos os usuários

#### GET /users/{id}

**Descrição**: Buscar usuário por ID

#### GET /users/email/{email}

**Descrição**: Buscar usuário por email

#### PUT /users/{id}

**Descrição**: Atualizar usuário

#### DELETE /users/{id}

**Descrição**: Deletar usuário

#### GET /users/type/{type}

**Descrição**: Buscar usuários por tipo (ORGANIZADOR/PARTICIPANTE)

#### GET /users/search?name={nome}

**Descrição**: Buscar usuários por nome

### 🎪 Event Controller (`/api/v1/events`)

#### POST /events

**Descrição**: Criar novo evento (apenas organizadores)

```json
{
  "title": "Workshop de Spring Boot",
  "description": "Aprenda Spring Boot na prática",
  "startDateTime": "2024-12-01T14:00:00",
  "endDateTime": "2024-12-01T18:00:00",
  "location": "São Paulo - SP",
  "capacity": 50
}
```

#### GET /events

**Descrição**: Listar eventos com paginação

#### GET /events/{id}

**Descrição**: Buscar evento por ID

#### PUT /events/{id}

**Descrição**: Atualizar evento (apenas organizador)

#### DELETE /events/{id}

**Descrição**: Deletar evento (apenas organizador)

#### GET /events/search?query={termo}

**Descrição**: Buscar eventos por título ou localização

#### GET /events/my-events

**Descrição**: Listar eventos do organizador autenticado

### 📝 Enrollment Controller (`/api/v1/enrollments`)

#### POST /enrollments

**Descrição**: Inscrever-se em evento

```json
{
  "eventId": "123e4567-e89b-12d3-a456-426614174000"
}
```

#### GET /enrollments/my-enrollments

**Descrição**: Listar inscrições do usuário

#### GET /enrollments/event/{eventId}

**Descrição**: Listar inscrições de um evento (apenas organizador)

#### DELETE /enrollments/{enrollmentId}

**Descrição**: Cancelar inscrição

#### PUT /enrollments/{enrollmentId}/confirm

**Descrição**: Confirmar inscrição (apenas organizador)

#### PUT /enrollments/{enrollmentId}/reject

**Descrição**: Rejeitar inscrição (apenas organizador)

---

## 🔒 Segurança

### JWT (JSON Web Token)

- **Algoritmo**: HMAC SHA-256
- **Expiração**: 1 hora (configurável)
- **Claims**: user_id, email, type, exp, iat

### Criptografia de Senhas

- **Algoritmo**: BCrypt
- **Rounds**: 10 (padrão)

### Autorização

- **Stateless**: Não mantém sessão no servidor
- **Role-based**: Baseada no tipo de usuário (ORGANIZADOR/PARTICIPANTE)

### Endpoints Públicos

- `POST /api/users` (registro)
- `POST /api/users/login` (login)

### Endpoints Protegidos

Todos os demais endpoints requerem autenticação JWT.

---

## 📁 Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/inovatech/inovatech_eventos/
│   │   ├── InovatechEventosApplication.java     # Classe principal
│   │   ├── config/
│   │   │   └── SecurityConfig.java              # Configuração de segurança
│   │   ├── controller/                          # Controllers REST
│   │   │   ├── UserController.java
│   │   │   ├── EventController.java
│   │   │   └── EnrollmentController.java
│   │   ├── dto/                                 # Data Transfer Objects
│   │   │   ├── ApiResponse.java
│   │   │   ├── UserCreateRequest.java
│   │   │   ├── UserUpdateRequest.java
│   │   │   ├── UserResponse.java
│   │   │   ├── LoginRequest.java
│   │   │   ├── LoginResponse.java
│   │   │   ├── EventCreateRequest.java
│   │   │   ├── EventUpdateRequest.java
│   │   │   ├── EventResponse.java
│   │   │   ├── EnrollmentCreateRequest.java
│   │   │   └── EnrollmentResponse.java
│   │   ├── model/                               # Entidades JPA
│   │   │   ├── User.java
│   │   │   ├── Event.java
│   │   │   ├── Enrollment.java
│   │   │   ├── TypeUser.java
│   │   │   └── EnrollmentStatus.java
│   │   ├── repository/                          # Repositórios Spring Data
│   │   │   ├── UserRepository.java
│   │   │   ├── EventRepository.java
│   │   │   └── EnrollmentRepository.java
│   │   ├── security/                            # Configurações de segurança
│   │   │   ├── JWTProvider.java
│   │   │   └── SecurityFilter.java
│   │   └── service/                             # Camada de serviço
│   │       ├── UserService.java
│   │       ├── EventService.java
│   │       ├── EnrollmentService.java
│   │       └── EmailService.java
│   └── resources/
│       ├── application.properties               # Configurações da aplicação
│       └── META-INF/
│           └── additional-spring-configuration-metadata.json
└── test/
    └── java/com/inovatech/inovatech_eventos/
        └── InovatechEventosApplicationTests.java
```

---

## 🚀 Como Usar

### 1. **Registro de Usuário**

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Maria Silva",
    "email": "maria@email.com",
    "password": "senha123",
    "type": "ORGANIZADOR"
  }'
```

### 2. **Login**

```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "maria@email.com",
    "password": "senha123"
  }'
```

### 3. **Criar Evento** (com token)

```bash
curl -X POST http://localhost:8080/api/v1/events \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_JWT_TOKEN" \
  -d '{
    "title": "Conferência Tech 2024",
    "description": "Evento sobre as últimas tendências em tecnologia",
    "startDateTime": "2024-12-15T09:00:00",
    "endDateTime": "2024-12-15T17:00:00",
    "location": "Centro de Convenções - São Paulo",
    "capacity": 200
  }'
```

### 4. **Inscrever-se em Evento**

```bash
curl -X POST http://localhost:8080/api/v1/enrollments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_JWT_TOKEN" \
  -d '{
    "eventId": "ID_DO_EVENTO"
  }'
```

---

## 🧪 Testes

### Executar Testes

```bash
./mvnw test
```

### Tipos de Teste

- **Testes Unitários**: Testam componentes individuais
- **Testes de Integração**: Testam a integração entre componentes
- **Testes de API**: Testam endpoints REST

---

## 📚 Documentação da API

### Swagger UI

Após executar a aplicação, acesse:

- **URL**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### Exemplos de Uso

A documentação Swagger inclui:

- Descrição detalhada de todos os endpoints
- Esquemas de request/response
- Códigos de status HTTP
- Exemplos práticos
- Interface para testar APIs diretamente

---

## 🚢 Deployment

### Variáveis de Ambiente

```bash
# Banco de dados
DATABASE_URL=jdbc:postgresql://host:port/database
DATABASE_USERNAME=usuario
DATABASE_PASSWORD=senha

# JWT
JWT_SECRET=sua_chave_secreta_super_forte
JWT_EXPIRATION=3600000

# Perfil Spring
SPRING_PROFILES_ACTIVE=production
```

### Docker (Opcional)

```dockerfile
FROM openjdk:21-jdk-slim
COPY target/inovatech-eventos-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Build para Produção

```bash
./mvnw clean package -Pprod
```

---

## 🤝 Contribuição

### Como Contribuir

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-funcionalidade`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`)
4. Push para a branch (`git push origin feature/nova-funcionalidade`)
5. Abra um Pull Request

### Padrões de Código

- Use **Java 21** features quando apropriado
- Siga as convenções do **Spring Boot**
- Mantenha **cobertura de testes** acima de 80%
- Use **Lombok** para reduzir boilerplate
- Documente APIs com **Swagger/OpenAPI**

### Issues e Bugs

- Reporte bugs através das [GitHub Issues](https://github.com/lucaslnasc/Inova-Tech/issues)
- Inclua logs de erro e steps para reproduzir
- Sugira melhorias e novas funcionalidades

---

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

## 👨‍💻 Autor

**Lucas Nascimento**

- GitHub: [@lucaslnasc](https://github.com/lucaslnasc)
- LinkedIn: [Lucas Nascimento](https://linkedin.com/in/lucaslnasc)

---

## 🙏 Agradecimentos

- **Spring Boot Team** pelo excelente framework
- **Supabase** pela infraestrutura de banco de dados
- **Swagger** pela documentação automática
- **InovaTech Team** pelo desenvolvimento e manutenção

---

## 📊 Status do Projeto

- ✅ **Funcional**: Sistema completo e operacional
- 🔄 **Em Desenvolvimento**: Melhorias contínuas
- 📚 **Documentado**: Documentação completa disponível
- 🧪 **Testado**: Cobertura de testes implementada
- 🚀 **Production Ready**: Pronto para produção

---

**Versão**: 1.0.0  
**Última Atualização**: Setembro 2024  
**Spring Boot**: 3.5.5  
**Java**: 21
