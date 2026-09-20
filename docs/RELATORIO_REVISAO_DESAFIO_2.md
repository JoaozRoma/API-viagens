# Relatório de revisão rigorosa — Desafio 2

## Parecer

O projeto original atendia à maior parte do enunciado, mas não estava pronto para ser tratado como entrega de nota máxima. A revisão encontrou inconsistências capazes de reduzir a nota ou impedir a execução em outro computador. Esta versão corrige os pontos técnicos verificáveis no código e na documentação.

## Falhas críticas encontradas na versão original

1. **Maven Wrapper incompleto:** os scripts `mvnw` e `mvnw.cmd` dependiam de `.mvn/wrapper/maven-wrapper.properties`, mas o arquivo não constava na estrutura entregue.
2. **Versão de Java contraditória:** o `pom.xml` exigia Java 25, enquanto o README informava que Java 17+ seria suficiente.
3. **Configuração do banco divergente:** o README mostrava `DB_HOST`, `DB_PORT`, `DB_NAME` e `DB_USER`, enquanto a aplicação lia `DB_URL` e `DB_USERNAME`.
4. **Link local inválido:** o README apontava para um caminho `file:///C:/...`, que só existia no computador do autor.
5. **Cálculo de média com deriva:** a média era arredondada a cada nova nota, o que produzia resultados incorretos em determinadas sequências.
6. **Validação de avaliação incompleta:** o DTO possuía anotações de validação, mas o endpoint não aplicava `@Valid` ao corpo.
7. **Teste de segurança insuficiente:** `@WithMockUser` simulava perfis e não comprovava autenticação com usuários carregados do banco.
8. **Tratamento genérico de exceções:** qualquer exceção não prevista virava `500` e a mensagem interna era exposta ao cliente.
9. **Documentação e dependências divergentes:** o README mencionava um starter diferente daquele declarado no `pom.xml`.
10. **Ausência de evidência sobre histórico Git e execução real:** esses critérios não podem ser comprovados por um arquivo de código consolidado.

## Correções realizadas

- inclusão completa do Maven Wrapper;
- padronização do alvo de compilação em Java 17 e atualização para Spring Boot 4.1.1;
- dependências de testes adequadas ao Spring Boot 4;
- configuração única e coerente das variáveis PostgreSQL;
- `compose.yaml` para reprodução do banco;
- entidades JPA revisadas;
- repositories usados somente pela camada de serviço;
- DTO específico de resposta de destino;
- senha protegida por DTO e `@JsonIgnore`;
- endpoints com validação e códigos HTTP coerentes;
- cabeçalho `Location` nos cadastros;
- cálculo de média sem arredondamento intermediário;
- testes com HTTP Basic real e usuários persistidos no H2;
- 36 métodos de teste cobrindo serviços, controllers, autenticação, autorização, validação, BCrypt e não exposição de senha;
- tratamento de erros 400, 404 e 409 sem vazamento de detalhes internos;
- README refeito com arquitetura, banco, execução, credenciais, endpoints e exemplos;
- `.gitignore`, coleção HTTP e checklist final.

## Avaliação por critério

| Critério do desafio | Situação desta versão | Evidência principal |
|---|---|---|
| Integração com PostgreSQL | Atendido no código | driver, `application.properties` e `compose.yaml` |
| Entidades JPA | Atendido | `Destino`, `Usuario` e `Role` |
| Spring Data JPA | Atendido | repositories e uso exclusivo nos services |
| Camada de serviço persistente | Atendido | transações e CRUD por repository |
| Autenticação | Atendido | `UsuarioService` como `UserDetailsService`, HTTP Basic e BCrypt |
| Autorização por perfil | Atendido | regras por método/rota em `SecurityConfig` |
| Proteção de operações sensíveis | Atendido | POST/PUT/DELETE e usuários restritos a ADMIN |
| Funcionalidade geral | Coberta por testes | serviços, controllers e segurança |
| Qualidade do código | Atendido | camadas, DTOs, validação e exceções |
| Documentação | Atendido | README completo e exemplos |
| Repositório Git e histórico | Depende da entrega do estudante | verificar commits e link no AVA |
| Execução real com PostgreSQL | Requer validação no computador de entrega | executar checklist do README |

## Nota simulada

O enunciado não informa pesos numéricos por categoria; portanto, a nota abaixo é uma simulação criteriosa, não uma nota oficial.

- **Versão original:** aproximadamente **82/100** em uma correção rigorosa, podendo cair mais se o avaliador tentasse usar o Maven Wrapper incompleto ou Java incompatível.
- **Versão revisada:** conteúdo técnico alinhado aos critérios para **100/100**, condicionado à confirmação de `BUILD SUCCESS`, teste manual contra PostgreSQL e entrega correta do repositório com histórico Git.

## Validações realizadas nesta revisão

Foram executadas verificações estáticas de estrutura e consistência:

- leitura e validação XML do `pom.xml`;
- validação sintática do `compose.yaml`;
- conferência de pacotes, caminhos, classes e imports internos;
- análise de delimitadores e strings em todos os arquivos Java;
- compilação estrutural de todas as fontes e testes com `javac --release 17` contra stubs das APIs externas, para detectar erros de sintaxe, incompatibilidades de linguagem e referências internas;
- execução adicional da lógica de negócio com repositories em memória controlados, cobrindo normalização, CRUD, pesquisa, média, validação, codificação de senha e usuário duplicado;
- validação do script `mvnw` com `bash -n`;
- busca por padrões antigos problemáticos, como caminho `file:///`, `@WithMockUser`, arredondamento intermediário e configuração divergente.

O build Maven real não pôde ser concluído neste ambiente de revisão porque o acesso externo para baixar Maven e dependências estava bloqueado. Por isso, `./mvnw clean test` e a inicialização contra PostgreSQL continuam sendo verificações obrigatórias no computador que fará a entrega.

## Validação obrigatória antes do envio

```bash
docker compose up -d
./mvnw clean test
./mvnw spring-boot:run
```

Em seguida, executar os exemplos de `api-exemplos.http`, reiniciar a aplicação e confirmar que os registros continuam no PostgreSQL. Por fim, verificar o histórico com:

```bash
git log --oneline --decorate --graph -n 15
```

Nenhum relatório estático pode substituir essas três evidências finais: build, persistência real e histórico Git.
