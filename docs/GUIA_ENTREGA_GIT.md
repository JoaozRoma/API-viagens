# Guia final de Git e entrega

O critério de Git só pode ser atendido no repositório real. Não invente datas nem altere o histórico existente. Registre as correções com commits claros a partir do estado atual.

## Conferência inicial

```bash
git status
git remote -v
git branch --show-current
```

## Sugestão de commits por assunto

Adapte os comandos ao histórico que já existe.

### 1. Persistência e configuração

```bash
git add pom.xml compose.yaml .mvn/ mvnw mvnw.cmd .gitattributes .gitignore \
  src/main/resources/application.properties \
  src/main/java/com/agencia/viagens/model \
  src/main/java/com/agencia/viagens/repository \
  src/main/java/com/agencia/viagens/service/DestinoService.java
git commit -m "feat: consolidar persistencia PostgreSQL com Spring Data JPA"
```

### 2. Autenticação e autorização

```bash
git add src/main/java/com/agencia/viagens/config \
  src/main/java/com/agencia/viagens/service/UsuarioService.java \
  src/main/java/com/agencia/viagens/controller \
  src/main/java/com/agencia/viagens/dto \
  src/main/java/com/agencia/viagens/exception
git commit -m "feat: proteger endpoints por perfis ADMIN e USER"
```

### 3. Testes

```bash
git add src/test
git commit -m "test: validar CRUD autenticacao autorizacao e media"
```

### 4. Documentação

```bash
git add README.md api-exemplos.http docs/
git commit -m "docs: documentar banco usuarios endpoints e execucao"
```

## Validação e envio

```bash
./mvnw clean test
git status
git log --oneline --decorate --graph -n 15
git push
```

Abra o endereço remoto em uma janela anônima para confirmar que o professor conseguirá acessar o repositório. Depois, envie exatamente esse link no AVA.
