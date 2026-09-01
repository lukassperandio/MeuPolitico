# MeuPolítico API

API de transparência política focada no Brasil.  
Agrega dados públicos de parlamentares (inicialmente **deputados federais** da Câmara dos Deputados): cadastro, despesas (CEAP), votações, presença, patrimônio, rankings e comparação.

Projeto acadêmico / TCC — backend em evolução.

---

## Stack

| Camada | Tecnologia |
|--------|------------|
| Linguagem | Java 17+ |
| Framework | Spring Boot 3 |
| Persistência | Spring Data JPA + PostgreSQL |
| Documentação | springdoc-openapi (Swagger UI) |
| Build | Maven |
| Fonte de dados | [Dados Abertos da Câmara](https://dadosabertos.camara.leg.br/) + arquivos CEAP |

---

## Pré-requisitos

- JDK 17 ou superior
- Maven 3.9+ (ou use o `mvnw` do projeto)
- PostgreSQL 14+ em execução
- Conexão com a internet (sync/import da Câmara)

---

## Configuração

### Banco de dados

O arquivo `application.properties` usa as variáveis `DB_NAME`, `DB_USERNAME` e `DB_PASSWORD`.

Crie o database no PostgreSQL:

```sql
CREATE DATABASE meupolitico;
```

Para desenvolvimento, use `application-local.properties` (**não versionado**) com:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/meupolitico
spring.datasource.username=postgres
spring.datasource.password=SUA_SENHA
```

Suba a API com o profile `local`:

**Windows**

```bash
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
```

**Linux / macOS**

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

### Swagger

- UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api-docs

---

## Sincronização de dados (Câmara)

Ordem recomendada na **primeira carga**:

### 1. Deputados federais

```http
POST /api/sync/camara/deputies
```

Cria/atualiza os políticos (`externalId` = id da Câmara).

### 2. Despesas CEAP (arquivos oficiais anuais)

```http
POST /api/sync/camara/expenses/ceap/2024
POST /api/sync/camara/expenses/ceap/2025
POST /api/sync/camara/expenses/ceap/2026
```

- Download de `Ano-{year}.json.zip`
- Importação em batch
- Duplicatas evitadas via `externalId` (`camara-{idDocumento}`)
- Pode demorar vários minutos

### 3. Recategorizar despesas

```http
POST /api/sync/camara/expenses/recategorize
```

Aplica mapeamentos e heurísticas nas categorias (corrige registros `OTHER`).

### Preview (debug, não grava)

```http
GET /api/sync/camara/deputies/preview
GET /api/sync/camara/expenses/preview/{externalId}?year=2024
```

> **Nota:** o endpoint REST `/deputados/{id}/despesas` da Câmara pode retornar vazio.  
> A fonte estável de gastos é o **arquivo CEAP** anual.

---

## Principais endpoints

| Método | Path | Descrição |
|--------|------|-----------|
| GET | `/api/politicians` | Lista paginada |
| GET | `/api/politicians/{id}` | Detalhe |
| GET | `/api/politicians/search/name?name=` | Busca por nome |
| GET | `/api/expenses?page=0&size=20` | Despesas paginadas |
| GET | `/api/expenses/politician/{id}` | Gastos de um político |
| GET | `/api/expenses/search` | Filtros combinados |
| GET | `/api/votes` | Votações |
| GET | `/api/attendances/politician/{id}/summary` | % de presença |
| GET | `/api/assets/politician/{id}/evolution` | Evolução patrimonial |
| GET | `/api/rankings/expenses` | Ranking de gastos |
| GET | `/api/rankings/attendance` | Ranking de assiduidade |
| GET | `/api/rankings/assets` | Ranking de patrimônio |
| GET | `/api/comparisons?ids=1,2,3` | Compara 1 a 3 políticos |

Documentação interativa completa: **Swagger UI**.

---

## Modelo de domínio (resumo)

- **Politician** — parlamentar (`externalId` liga à Câmara)
- **Expense** — despesa (CEAP), com categoria padronizada
- **Vote** — voto em proposição
- **Attendance** — presença em sessão
- **Asset** — patrimônio declarado por ano
- **ExpenseCategoryMapping** — texto bruto CEAP → enum interno

---

## Estrutura de pastas

```text
com.meupolitico
├── config              # RestClient Câmara, OpenAPI
├── controller          # REST
├── dto                 # Request / Response
├── entity
├── enums
├── exception
├── integration.camara  # Client + DTOs externos
├── mapper
├── repository
└── service
```

---

## Status do projeto

**Feito**

- CRUD e buscas do domínio
- Rankings e comparação
- Sync de deputados federais
- Import CEAP 2024–2026
- Categorização de despesas
- Swagger
- README

**Próximos**

- Frontend Angular
- Enriquecimento de perfil (detalhe da Câmara)
- Outras fontes (Senado, etc.) — fora do MVP federal

---

## Licença / uso

O MeuPolítico nasceu com um objetivo principal: **facilitar o acesso da população a dados públicos sobre políticos**, de forma clara e útil no dia a dia.

O projeto também é desenvolvido no contexto acadêmico (TCC / faculdade), mas a prioridade é o uso cidadão.

Os dados vêm de fontes oficiais de transparência (em especial os [Dados Abertos da Câmara dos Deputados](https://dadosabertos.camara.leg.br/)). O uso desses dados deve respeitar os termos e a legislação aplicáveis.