# ✈️ API de Gerenciamento de Destinos - Agência de Viagens

API RESTful desenvolvida em **Spring Boot 4** e **Java 25**, com persistência de dados relacional via **PostgreSQL / Spring Data JPA** e controle de autenticação e autorização por perfis com **Spring Security** (HTTP Basic + BCrypt).

---

## 📌 Sumário
- [Visão Geral e Evolução do Projeto](#-visão-geral-e-evolução-do-projeto)
- [Arquitetura e Camadas](#-arquitetura-e-camadas)
- [Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [Perfis de Acesso e Segurança](#-perfis-de-acesso-e-segurança)
- [Credenciais Padrão (Carga Inicial)](#-credenciais-padrão-carga-inicial)
- [Como Configurar e Executar](#-como-configurar-e-executar)
  - [Pré-requisitos](#pré-requisitos)
  - [Configuração do Banco de Dados PostgreSQL](#configuração-do-banco-de-dados-postgresql)
  - [Executando a Aplicação](#executando-a-aplicação)
  - [Executando a Suíte de Testes](#executando-a-suíte-de-testes)
- [Documentação Completa de Endpoints](#-documentação-completa-de-endpoints)
- [Exemplos de Uso com cURL](#-exemplos-de-uso-com-curl)

---

## 📖 Visão Geral e Evolução do Projeto

O projeto evoluiu de um MVP em memória para uma solução corporativa robusta e escalável para uma agência de viagens, cumprindo todos os requisitos modernos de Desenvolvimento de Sistemas Web (DSW):
1. **Persistência Relacional**: Migração do armazenamento em memória (`ConcurrentHashMap`) para **PostgreSQL** através do **Spring Data JPA / Hibernate**, com tabelas normalizadas e mapeamento de entidades.
2. **Segurança e Controle de Acesso**: Autenticação stateless via **Spring Security** com senhas criptografadas usando algoritmo **BCrypt**, suporte a múltiplos perfis de acesso (`ROLE_ADMIN` e `ROLE_USER`) e controle granular de permissões por rotas e verbos HTTP.
3. **Validação e Tratamento de Exceções**: Validação declarativa com `Bean Validation` (Jakarta Validation) e tratamento global uniforme de erros via `@RestControllerAdvice`.
4. **Carga Automática de Dados (Data Seeder)**: Inicialização inteligente do banco com usuários e destinos de exemplo caso as tabelas estejam vazias.

---

## 🏗️ Arquitetura e Camadas

A aplicação implementa uma **Arquitetura em Camadas (Layered Architecture)** seguindo as melhores práticas do ecossistema Spring:

```
src/main/java/com/agencia/viagens/
 ├── config/             # Configurações de Segurança (SecurityConfig) e Carga Inicial (DataInitializer)
 ├── controller/         # Camada REST (DestinoController, UsuarioController)
 ├── dto/                # Data Transfer Objects (Requests, Responses, Validações)
 ├── exception/          # Exceções customizadas e GlobalExceptionHandler
 ├── model/              # Entidades JPA (Destino, Usuario) e Enums (Role)
 ├── repository/         # Interfaces Spring Data JPA (DestinoRepository, UsuarioRepository)
 └── service/            # Regras de Negócio e Autenticação (DestinoService, UsuarioService)
```

---

## 💻 Tecnologias Utilizadas

* **Java 25** (OpenJDK)
* **Spring Boot 4.1.0**
  * `spring-boot-starter-web` (APIs RESTful e Apache Tomcat embutido)
  * `spring-boot-starter-data-jpa` (Persistência com Hibernate/JPA)
  * `spring-boot-starter-security` (Autenticação, Autorização e Criptografia BCrypt)
  * `spring-boot-starter-validation` (Validações `@NotNull`, `@NotBlank`, `@Size`, `@DecimalMin`, etc.)
* **PostgreSQL Driver**: Conexão com banco relacional em produção e desenvolvimento.
* **H2 Database**: Banco relacional em memória para isolamento e agilidade nos testes unitários e integrados.
* **Maven**: Gerenciamento de dependências e automação de build.
* **JUnit 5 / Spring Security Test**: Testes automatizados de segurança, serviços e controladores.

---

## 🔐 Perfis de Acesso e Segurança

A API adota **HTTP Basic Authentication** sem estado (*stateless*), protegendo as operações conforme a tabela de permissões:

| Recurso / Rota | Verbo HTTP | Acesso Permitido | Descrição |
| :--- | :--- | :--- | :--- |
| `/api/destinos/**` | `GET` | **Público** (Livre) | Consulta e listagem de destinos turísticos. |
| `/api/destinos/{id}/avaliar` | `PATCH` | `ROLE_USER` ou `ROLE_ADMIN` | Registro de notas/avaliações e recálculo da média. |
| `/api/destinos` | `POST` | `ROLE_ADMIN` | Cadastro de novos destinos. |
| `/api/destinos/{id}` | `PUT` | `ROLE_ADMIN` | Atualização cadastral de destinos. |
| `/api/destinos/{id}` | `DELETE` | `ROLE_ADMIN` | Exclusão de destinos. |
| `/api/usuarios/**` | `GET`, `POST` | `ROLE_ADMIN` | Consulta e cadastro de novos usuários/operadores. |

---

## 👤 Credenciais Padrão (Carga Inicial)

Ao iniciar a aplicação pela primeira vez com o banco vazio, o componente `DataInitializer` cadastra automaticamente as credenciais de teste com hash BCrypt:

| Perfil | Usuário (`username`) | Senha (`password`) | Permissões |
| :--- | :--- | :--- | :--- |
| **Administrador** | `admin` | `admin123` | Acesso irrestrito (CRUD de destinos e gestão de usuários) |
| **Usuário Comum** | `user` | `user123` | Consulta pública e avaliação de destinos |

---

## 🚀 Como Configurar e Executar

### Pré-requisitos
- **Java JDK 17+** (recomendado Java 21 ou Java 25).
- **PostgreSQL 14+** instalado e em execução (porta 5432).
- **Maven** (ou o wrapper `./mvnw` incluso no projeto).

---

### Configuração do Banco de Dados PostgreSQL

1. Crie a base de dados no PostgreSQL:
```sql
CREATE DATABASE viagens_db;
```

2. As propriedades de conexão padrão estão em [application.properties](file:///C:/Users/Roma/IdeaProjects/API-viagens/src/main/resources/application.properties):
```properties
spring.datasource.url=jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:viagens_db}
spring.datasource.username=${DB_USER:postgres}
spring.datasource.password=${DB_PASSWORD:postgres}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
server.port=8081
```

---

### Executando a Aplicação

Compile e inicie o servidor:
```bash
./mvnw spring-boot:run
```
*(No Windows PowerShell: `mvn spring-boot:run` ou `.\mvnw.cmd spring-boot:run`)*

A API estará acessível em `http://localhost:8081`.

---

### Executando a Suíte de Testes

Os testes são executados automaticamente em banco H2 isolado:
```bash
./mvnw test
```

---

## 📡 Documentação Completa de Endpoints

### 1. Destinos Turísticos (`/api/destinos`)

#### `GET /api/destinos`
- **Acesso**: Público
- **Descrição**: Retorna a lista de todos os destinos cadastrados.
- **Resposta**: `200 OK`

#### `GET /api/destinos/{id}`
- **Acesso**: Público
- **Descrição**: Retorna os detalhes de um destino por ID.
- **Resposta**: `200 OK` (ou `404 Not Found`)

#### `GET /api/destinos/pesquisar?nome={nome}&localizacao={localizacao}`
- **Acesso**: Público
- **Descrição**: Busca destinos por correspondência parcial de nome ou localização (ambos opcionais).
- **Resposta**: `200 OK`

#### `POST /api/destinos`
- **Acesso**: `ROLE_ADMIN`
- **Descrição**: Cadastra um novo destino turístico.
- **Corpo da Requisição (JSON)**:
```json
{
  "nome": "Fernando de Noronha",
  "localizacao": "Pernambuco, Brasil",
  "descricao": "Arquipélago vulcânico com praias paradisíacas e rica vida marinha."
}
```
- **Resposta**: `201 Created`

#### `PUT /api/destinos/{id}`
- **Acesso**: `ROLE_ADMIN`
- **Descrição**: Atualiza todos os dados descritivos de um destino existente.
- **Corpo da Requisição (JSON)**:
```json
{
  "nome": "Fernando de Noronha - Atualizado",
  "localizacao": "Pernambuco, Brasil",
  "descricao": "Parque Nacional Marinho protegido com ecoturismo sustentável."
}
```
- **Resposta**: `200 OK` (ou `404 Not Found`)

#### `PATCH /api/destinos/{id}/avaliar`
- **Acesso**: `ROLE_USER` ou `ROLE_ADMIN`
- **Descrição**: Insere uma nova avaliação (nota entre 0 e 10) e recalcula a média e a quantidade de avaliações.
- **Corpo da Requisição (JSON)**:
```json
{
  "nota": 9.5
}
```
- **Resposta**: `200 OK` (ou `400 Bad Request` se nota < 0 ou nota > 10, ou `404 Not Found`)

#### `DELETE /api/destinos/{id}`
- **Acesso**: `ROLE_ADMIN`
- **Descrição**: Remove um destino do sistema.
- **Resposta**: `204 No Content` (ou `404 Not Found`)

---

### 2. Gestão de Usuários (`/api/usuarios`)

#### `GET /api/usuarios`
- **Acesso**: `ROLE_ADMIN`
- **Descrição**: Lista todos os usuários cadastrados (sem expor as senhas).
- **Resposta**: `200 OK`

#### `POST /api/usuarios`
- **Acesso**: `ROLE_ADMIN`
- **Descrição**: Cadastra um novo usuário no sistema. A senha é automaticamente criptografada com BCrypt.
- **Corpo da Requisição (JSON)**:
```json
{
  "username": "operador_guia",
  "password": "senhaForte@123",
  "role": "ROLE_USER"
}
```
- **Resposta**: `201 Created` (ou `400 Bad Request` se o username já existir)

---

## 💻 Exemplos de Uso com cURL

### 1. Listar todos os destinos (Público)
```bash
curl -X GET http://localhost:8081/api/destinos
```

### 2. Pesquisar destinos por nome ou localização (Público)
```bash
curl -X GET "http://localhost:8081/api/destinos/pesquisar?nome=Noronha"
```

### 3. Cadastrar destino (Requer ADMIN)
```bash
curl -X POST http://localhost:8081/api/destinos \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d "{\"nome\":\"Bonito\",\"localizacao\":\"Mato Grosso do Sul, Brasil\",\"descricao\":\"Capital do ecoturismo com rios de águas cristalinas.\"}"
```

### 4. Avaliar um destino (Permitido para USER e ADMIN)
```bash
curl -X PATCH http://localhost:8081/api/destinos/1/avaliar \
  -u user:user123 \
  -H "Content-Type: application/json" \
  -d "{\"nota\": 10.0}"
```

### 5. Excluir destino (Requer ADMIN)
```bash
curl -X DELETE http://localhost:8081/api/destinos/1 \
  -u admin:admin123
```

### 6. Criar novo usuário (Requer ADMIN)
```bash
curl -X POST http://localhost:8081/api/usuarios \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"agente_joao\",\"password\":\"viagem2026\",\"role\":\"ROLE_USER\"}"
```
