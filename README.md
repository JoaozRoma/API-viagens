# API REST de Destinos — Desafio 2 de Desenvolvimento de Sistemas Web

Versão evoluída da API de uma agência de viagens, com persistência em **PostgreSQL**, acesso a dados com **Spring Data JPA** e autenticação/autorização com **Spring Security**.

## 1. Objetivo e evolução do projeto

A primeira versão armazenava os destinos apenas em memória, portanto os registros eram perdidos quando a aplicação era encerrada. Nesta versão, destinos e usuários são gravados no PostgreSQL e permanecem disponíveis após reinicializações. A API também passa a controlar o acesso por meio de dois perfis:

- `ROLE_ADMIN`: gerencia destinos e usuários;
- `ROLE_USER`: consulta os recursos públicos e registra avaliações.

A autenticação é feita por **HTTP Basic**, sem sessão no servidor. As senhas são armazenadas com hash **BCrypt**. Em ambiente real, HTTP Basic deve ser usado somente por conexão HTTPS.

## 2. Arquitetura

O projeto utiliza arquitetura em camadas para separar responsabilidades:

```text
src/main/java/com/agencia/viagens/
├── config/       # Spring Security e carga inicial de dados
├── controller/   # Rotas HTTP e códigos de resposta
├── dto/          # Objetos de entrada e saída da API
├── exception/    # Exceções e tratamento global de erros
├── model/        # Entidades JPA e enum de perfis
├── repository/   # Acesso ao banco com Spring Data JPA
└── service/      # Regras de negócio e transações
```

Fluxo principal:

```text
requisição HTTP → controller → service → repository → PostgreSQL
                         ↓
                 regras de negócio
```

Essa divisão evita acesso direto ao banco nos controladores, facilita testes e permite evoluir cada camada sem concentrar todas as responsabilidades em uma única classe.

## 3. Tecnologias

- Java 17;
- Spring Boot 4.1.1;
- Spring Web MVC;
- Spring Data JPA / Hibernate;
- PostgreSQL;
- Spring Security;
- BCrypt;
- Jakarta Bean Validation;
- Maven Wrapper;
- JUnit 5, MockMvc, Spring Security Test e H2 para testes automatizados.

## 4. Pré-requisitos

- JDK entre 17 e 26 (o projeto compila para Java 17; recomenda-se JDK 17 ou 21);
- Docker com Docker Compose **ou** PostgreSQL instalado localmente;
- acesso à internet no primeiro build para o Maven baixar as dependências.

Não é necessário instalar Maven globalmente, pois o repositório contém `mvnw`, `mvnw.cmd` e `.mvn/wrapper/maven-wrapper.properties`.

## 5. Configuração do PostgreSQL

### Opção A — Docker Compose

Na raiz do projeto:

```bash
docker compose up -d
```

O arquivo `compose.yaml` cria um banco de desenvolvimento local:

- banco: `viagens_db`;
- usuário: `postgres`;
- senha: `postgres`;
- porta: `5432`.

Para encerrar:

```bash
docker compose down
```

Para apagar também o volume de dados:

```bash
docker compose down -v
```

### Opção B — PostgreSQL local

Crie o banco:

```sql
CREATE DATABASE viagens_db;
```

A aplicação usa os seguintes valores padrão:

```properties
DB_URL=jdbc:postgresql://localhost:5432/viagens_db
DB_USERNAME=postgres
DB_PASSWORD=postgres
SERVER_PORT=8081
APP_SEED_ENABLED=true
```

As variáveis podem ser definidas no sistema operacional sem alterar o código. Exemplos:

**PowerShell**

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/viagens_db"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="sua_senha"
```

**Linux/macOS**

```bash
export DB_URL="jdbc:postgresql://localhost:5432/viagens_db"
export DB_USERNAME="postgres"
export DB_PASSWORD="sua_senha"
```

O mapeamento das propriedades está em `src/main/resources/application.properties`.

## 6. Execução

### Linux/macOS

```bash
chmod +x mvnw
./mvnw spring-boot:run
```

### Windows PowerShell

```powershell
.\mvnw.cmd spring-boot:run
```

A API ficará disponível em:

```text
http://localhost:8081
```

Na primeira inicialização, o Hibernate cria/atualiza as tabelas `tb_destinos` e `tb_usuarios`. O `DataInitializer` inclui usuários e destinos de demonstração somente quando os registros correspondentes ainda não existem. Para desativar essa carga acadêmica, defina `APP_SEED_ENABLED=false`.

## 7. Usuários de teste

| Perfil | Usuário | Senha | Uso |
|---|---|---|---|
| `ROLE_ADMIN` | `admin` | `admin123` | CRUD de destinos e gestão de usuários |
| `ROLE_USER` | `user` | `user123` | Avaliação de destinos |

Essas credenciais são apenas para demonstração acadêmica. Devem ser alteradas ou removidas antes de qualquer implantação real.

### Fluxo da autenticação

1. O cliente envia usuário e senha no cabeçalho `Authorization: Basic ...` em cada requisição protegida.
2. O Spring Security encaminha o nome de usuário ao `UsuarioService`, que implementa `UserDetailsService`.
3. O serviço consulta `tb_usuarios` por meio do `UsuarioRepository`.
4. O `PasswordEncoder` compara a senha recebida com o hash BCrypt armazenado; a senha original não é recuperada nem retornada.
5. Após autenticar, o perfil `ROLE_ADMIN` ou `ROLE_USER` é usado pela `SecurityFilterChain` para autorizar ou negar a rota.

Não existe endpoint próprio de “login” porque o HTTP Basic autentica cada requisição. A codificação Base64 do cabeçalho não é criptografia; por isso, HTTPS é obrigatório fora do ambiente acadêmico local.

## 8. Regras de acesso

| Método e rota | Acesso | Resultado principal |
|---|---|---|
| `GET /api/destinos` | Público | Lista destinos |
| `GET /api/destinos/{id}` | Público | Detalha um destino |
| `GET /api/destinos/pesquisar` | Público | Pesquisa por nome e/ou localização |
| `POST /api/destinos` | `ROLE_ADMIN` | Cadastra destino (`201 Created`) |
| `PUT /api/destinos/{id}` | `ROLE_ADMIN` | Atualiza destino (`200 OK`) |
| `PATCH /api/destinos/{id}/avaliar` | `ROLE_USER` ou `ROLE_ADMIN` | Registra nota de `0.0` a `10.0` |
| `DELETE /api/destinos/{id}` | `ROLE_ADMIN` | Exclui destino (`204 No Content`) |
| `GET /api/usuarios` | `ROLE_ADMIN` | Lista usuários sem expor senhas |
| `GET /api/usuarios/{id}` | `ROLE_ADMIN` | Detalha usuário sem expor senha |
| `POST /api/usuarios` | `ROLE_ADMIN` | Cadastra usuário com senha BCrypt |

Comportamento esperado da segurança:

- `401 Unauthorized`: credenciais ausentes ou inválidas;
- `403 Forbidden`: usuário autenticado sem o perfil necessário;
- `400 Bad Request`: corpo ou campos inválidos;
- `404 Not Found`: recurso inexistente;
- `409 Conflict`: violação de restrição de integridade.

## 9. Exemplos com cURL

### Consulta pública

```bash
curl http://localhost:8081/api/destinos
```

### Pesquisa pública

```bash
curl "http://localhost:8081/api/destinos/pesquisar?nome=Noronha&localizacao=Pernambuco"
```

### Cadastro de destino como ADMIN

```bash
curl -X POST http://localhost:8081/api/destinos \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{"nome":"Bonito","localizacao":"Mato Grosso do Sul, Brasil","descricao":"Destino de ecoturismo."}'
```

### Avaliação como USER

```bash
curl -X PATCH http://localhost:8081/api/destinos/1/avaliar \
  -u user:user123 \
  -H "Content-Type: application/json" \
  -d '{"nota":9.5}'
```

### Cadastro de usuário como ADMIN

```bash
curl -X POST http://localhost:8081/api/usuarios \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{"username":"operador","password":"senhaSegura123","role":"ROLE_USER"}'
```

O arquivo `api-exemplos.http` contém uma coleção adicional para IntelliJ IDEA, VS Code com REST Client ou ferramenta compatível.

## 10. Testes automatizados

Os testes usam H2 em memória e não alteram o banco PostgreSQL local:

```bash
./mvnw clean test
```

No Windows:

```powershell
.\mvnw.cmd clean test
```

A suíte verifica, entre outros pontos:

- persistência e operações da camada de serviço;
- pesquisa por nome e localização;
- cálculo correto da média das avaliações;
- hash BCrypt das senhas;
- autenticação com usuários realmente carregados do banco de teste;
- respostas `401`, `403`, `400`, `404`, `201` e `204`;
- regras distintas de `ROLE_ADMIN` e `ROLE_USER`;
- ausência de senha nas respostas da API.

Para gerar o pacote executável:

```bash
./mvnw clean package
```

## 11. Estrutura das tabelas

### `tb_destinos`

- `id`: chave primária gerada automaticamente;
- `nome`: obrigatório, até 150 caracteres;
- `localizacao`: obrigatória, até 150 caracteres;
- `descricao`: opcional, até 1000 caracteres;
- `media_avaliacoes`: média acumulada;
- `quantidade_avaliacoes`: total de avaliações recebidas.

### `tb_usuarios`

- `id`: chave primária gerada automaticamente;
- `username`: obrigatório e único;
- `password`: hash BCrypt, nunca retornado pela API;
- `role`: `ROLE_ADMIN` ou `ROLE_USER`.

Não há relacionamento obrigatório entre essas duas entidades no escopo proposto: a atividade exige o registro da média do destino, mas não exige histórico individual de avaliações por usuário.

## 12. Decisões técnicas

- **PostgreSQL** garante persistência após reinicializações e atende ao requisito de banco relacional.
- **Spring Data JPA** reduz código repetitivo de acesso a dados e mantém o acesso ao banco concentrado nos repositories.
- **Spring Security** centraliza autenticação e autorização por perfil.
- **HTTP Basic stateless** é suficiente para demonstrar autenticação em uma API acadêmica; em produção, deve operar sobre HTTPS.
- **CSRF desativado no escopo acadêmico:** a API não usa formulário, cookie de sessão nem estado no servidor. Em uma aplicação voltada a navegadores, a estratégia deve ser reavaliada em conjunto com o mecanismo de autenticação.
- **Carga inicial configurável** facilita a correção acadêmica e pode ser desativada com `APP_SEED_ENABLED=false`.
- **`ddl-auto=update`** foi mantido para facilitar a atividade; em produção, o ideal é versionar alterações de esquema com Flyway ou Liquibase.
- **BCrypt** impede o armazenamento de senhas em texto puro.
- **DTOs** separam o contrato HTTP das entidades e evitam exposição da senha.
- **Tratamento global de exceções** mantém os erros de validação e negócio consistentes.
- **H2 nos testes** torna a suíte repetível e independente do PostgreSQL local; o modo de compatibilidade PostgreSQL reduz diferenças de sintaxe.

## 13. Checklist de entrega

Antes de enviar o link no Ambiente Virtual de Aprendizagem:

- [ ] Executar `./mvnw clean test` e confirmar `BUILD SUCCESS`;
- [ ] subir o PostgreSQL e executar `./mvnw spring-boot:run`;
- [ ] testar um `GET` público, um acesso `401`, um acesso `403`, um cadastro ADMIN e uma avaliação USER;
- [ ] confirmar no PostgreSQL que os dados permanecem após reiniciar a aplicação;
- [ ] consultar `SELECT id, nome FROM tb_destinos;` no PostgreSQL para registrar evidência da persistência;
- [ ] conferir que `.env`, senhas pessoais, `.idea` e `target` não foram enviados;
- [ ] manter `README.md`, `compose.yaml`, `.mvn/`, `mvnw`, `mvnw.cmd`, `pom.xml` e `src/` no repositório;
- [ ] realizar commits claros e verificar o histórico no Git;
- [ ] enviar o endereço correto do repositório no AVA dentro do prazo.

O relatório detalhado da revisão está em `docs/RELATORIO_REVISAO_DESAFIO_2.md`. O procedimento de commits e envio está em `docs/GUIA_ENTREGA_GIT.md`.
