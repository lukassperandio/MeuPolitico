# MeuPolítico Web

Frontend Angular do **MeuPolítico** — aplicação de transparência política focada no Brasil.

Consulta deputados federais, despesas parlamentares (CEAP), presença, patrimônio declarado (TSE), rankings e comparação lado a lado. Os dados vêm de fontes públicas oficiais através da API MeuPolítico.

## Stack

| Camada | Tecnologia |
|--------|------------|
| Framework | Angular 21 |
| Linguagem | TypeScript |
| Estilos | SCSS |
| Cliente HTTP | HttpClient + proxy de desenvolvimento |

## Pré-requisitos

- Node.js 20+ (LTS recomendado)
- npm 10+
- **API MeuPolítico** em execução em `http://localhost:8080`

## Configuração

```bash
npm install
```

O proxy de desenvolvimento (`proxy.conf.json`) encaminha `/api` para o Spring:

```json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true
  }
}
```

No `angular.json`, em `serve` → `options`, deve existir:

```json
"proxyConfig": "proxy.conf.json"
```

## Executar

```bash
ng serve
```

Abre [http://localhost:4200](http://localhost:4200).

A API precisa estar na porta **8080**; caso contrário, as chamadas de políticos/despesas falham.

## Rotas principais

| Caminho | Descrição |
|---------|-----------|
| `/politicians` | Busca e lista |
| `/politicians/:id` | Perfil (gastos, presença, patrimônio) |
| `/rankings` | Rankings top 50 |
| `/compare` | Comparar até 3 políticos |

## Funcionalidades

- Busca por nome
- Tabela de gastos com filtros (fornecedor, período, valor mínimo) e ordenação
- Secções de presença e patrimônio com avisos de fonte oficial
- Rankings: gastos, assiduidade, patrimônio
- Comparação entre políticos

## Notas sobre os dados (limites honestos)

- **Gastos:** CEAP da Câmara dos Deputados (ficheiros oficiais anuais)
- **Presença:** eventos da Câmara (o ficheiro lista quem esteve presente; não é lista completa de faltas)
- **Patrimônio:** declaração de bens no **TSE** no registo de candidatura (ex.: 2022 e 2026) — **não** atualiza anualmente durante o mandato
- Âmbito atual: **deputados federais**

## Estrutura do projeto

```text
src/app/
  core/           # models, services HTTP
  features/       # políticos, rankings, comparação
  shared/         # layout, pipes
```

## Relacionado

Repositório da API: **meupolitico-api** (Spring Boot + PostgreSQL).

## Licença / uso

Projeto de transparência cívica e uso académico (TCC).  
O uso dos dados deve respeitar os termos dos dados abertos da Câmara dos Deputados e do TSE.
