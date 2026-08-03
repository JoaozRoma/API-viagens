# API de Gerenciamento de Destinos - Agência de Viagens

## Visão Geral do Problema
Como parte do processo de modernização digital da nossa agência de viagens, este projeto consiste na primeira versão funcional (MVP) de uma API RESTful. O objetivo é permitir a integração de nossos serviços com aplicativos de turismo, parceiros comerciais e plataformas futuras. Neste momento, a API foca no gerenciamento de destinos turísticos, operando em memória (sem persistência em banco de dados) para validar a estrutura, os contratos das rotas e as regras de negócio iniciais, como o recálculo de avaliações.

## Arquitetura Proposta
A aplicação adota uma **Arquitetura em Camadas (Layered Architecture)**, amplamente utilizada no mercado por facilitar a manutenção, a testabilidade e o desacoplamento do código. A divisão foi feita em:
* **Controller (Controladores):** Responsável por interceptar as requisições HTTP, delegar as tarefas para os serviços e devolver a resposta adequada (códigos de status HTTP).
* **Service (Serviços):** Centraliza as regras de negócio (ex: cálculo de médias de avaliação) e gerencia o armazenamento de dados.
* **Model/Entity (Modelos):** Representa as entidades do domínio (neste caso, a classe `Destino`).

## Justificativa Tecnológica
* **Linguagem: Java (versão 17+):** Escolhida por sua robustez, forte tipagem, ampla adoção no mercado corporativo e vasto ecossistema.
* **Framework: Spring Boot:** Facilita a configuração inicial de projetos Java (Convenção sobre Configuração). Ele provê um servidor web embutido (Tomcat) e anotações simplificadas para criação imediata de APIs REST (`spring-boot-starter-web`), sendo altamente escalável para futuras migrações para banco de dados (Spring Data JPA) ou microsserviços.

## Principais Endpoints da API
A API roda por padrão em `http://localhost:8080/api/destinos`.

| Método HTTP | Endpoint | Descrição |
| :--- | :--- | :--- |
| `POST` | `/` | Cadastra um novo destino turístico. |
| `GET` | `/` | Lista todos os destinos cadastrados. |
| `GET` | `/pesquisar` | Pesquisa destinos. Parâmetros na URL: `?nome=` ou `?localizacao=`. |
| `GET` | `/{id}` | Retorna os detalhes de um destino específico. |
| `PUT` | `/{id}` | Atualiza todas as informações de um destino. |
| `PATCH` | `/{id}/avaliar` | Adiciona uma nota ao destino e recalcula a média. |
| `DELETE` | `/{id}` | Exclui um destino do sistema. |
