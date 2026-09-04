# Product

<!-- impeccable:product-schema 1 -->

## Platform

web

## Users

Primary user: a Brazilian citizen overseeing a sitting mandate in everyday life — not a specialist, journalist, or campaign staffer. They open MeuPolítico to check a federal deputy (and, as the product grows, a senator): how the office spends public money, whether the parliamentarian shows up, what was declared to the TSE, and how that compares with peers.

They are not primarily “shopping for a vote” in election season. The job is ongoing civic oversight: *who do I want to hold to account today?*

The project is also a TCC / academic work, but the product user is the citizen. Academic evaluation is an operating constraint, not the audience.

## Product Purpose

MeuPolítico exists so the public can use official data about Brazilian parliamentarians without having to become a data clerk. Success is a non-specialist who can inspect a mandate — expenses, attendance, assets, and (as sources allow) legislative activity — in a single place, understand what they are looking at, and act on it (compare, rank, follow, get alerted).

The product is civic infrastructure, not a newsroom and not a party tool.

## Positioning

Every figure carries the official source that proves it. Transparency is the product, not a footer. A neighboring dump (Câmara, TSE, Senado, Portal da Transparência) can republish the same tables; MeuPolítico cannot truthfully exist if a number appears without its origin.

Scanability is how that claim is delivered: the citizen should be able to oversee a mandate as quickly as checking the weather. Unsourced composite scores, invented politicians, and anonymous “proof” are out of bounds.

## Operating Context

- Language and locale: Brazilian Portuguese (`pt-BR`), currency BRL, UF abbreviations for states.
- Live slice today: **deputados federais** of the Câmara dos Deputados.
- Data actually ingested: [Dados Abertos da Câmara](https://dadosabertos.camara.leg.br/) (cadastro, CEAP annual files, recorded presence); TSE candidate asset declarations (`dados-candidatos/`, elections such as 2022 and 2026). CEAP REST on the Câmara API can return empty — the annual CEAP zip is the stable expense source.
- Attendance files list who was **present**. They are not a complete absence roll; copy must keep saying so.
- TSE assets are declared at candidacy, typically every four years, not annually during the mandate; copy must keep saying so.
- Current citizen workflows in the Angular app: search/list politicians → profile (attendance, assets, filterable CEAP expenses) → rankings (gastos / assiduidade / patrimônio) → compare 1–3 by internal id.
- Target workflows (mockups are the destination product, not decoration): Início, Explorar, Comparar (up to four), Rankings, Alertas, Favoritos, Sobre, Ajuda, and a signed-in account. Comparison is side-by-side on sourced indicators.
- Environment: web app (`meupolitico-web`, Angular) talking to `meupolitico-api` (Spring Boot, PostgreSQL). Dev: `ng serve` at `http://localhost:4200/`, API on port 8080 with Swagger.

## Capabilities and Constraints

**Shipped (treat as real, do not regress):**

- Politician directory with photo, party, state, position, Câmara source chip; search by name (UI also invites party/state).
- Profile: attendance summary with the presence-file caveat; TSE asset evolution across election years; CEAP expenses with supplier / date / amount filters, category labels in Portuguese, pagination.
- Rankings: total expenses, attendance %, latest declared assets.
- Comparison API/UI: 1–3 politicians on total expenses, attendance %, latest asset.
- Backend also models votes and has vote CRUD; the UI does not yet surface voting.
- Expense recategorization (CEAP raw text → internal `ExpenseCategory`) exists so “Outros” shrinks; it is implementation, not the positioning claim.

**Committed destination (mockups are the product-alvo; current code is the start):**

- Senado alongside Câmara.
- User account, favorites, alerts.
- Legislative activity: projects presented, proposal-approval style indicators — **only if each number has an official source**.
- Composite “transparência” ratings — same rule: no score without a documented official basis.
- Compare up to four parliamentarians.
- Portal da Transparência as an additional cited source, once ingested.

**Must preserve:**

- Do not invent politicians, quotes, testimonials, press, users (“Ana”), or figures.
- Do not present TSE assets as yearly mandate updates.
- Do not present Câmara presence files as a full attendance/absence census.
- Senate, login, alerts, favorites, bills, and composite scores are destination — never fake them as live data.
- Open: auth provider; exact official sources for “aprovação das propostas” and any transparency score; when Senado and Portal da Transparência land.

## Brand Commitments

- Name: **MeuPolítico**. Wordmark splits *Meu* + *Político*.
- Voice: `pt-BR`, civic, direct, non-partisan. Explain the data; do not editorialize the politician.
- Product lines already in use (keep unless the user replaces them): “Quem você quer fiscalizar hoje?”; “Política clara. Escolhas melhores.”; “transparência que se lê de relance”; “Informação confiável para decisões conscientes.”
- Visual world is **not** specified here. Mockups at `mockups/meupolitico-mockup.html` and `mockups/Captura de ecrã 2026-09-03 193529.png` are product-scope evidence (IA, features, copy), not a frozen visual system.

## Evidence on Hand

- Official TSE extracts: `dados-candidatos/consulta_cand_2022`, `consulta_cand_2026`, `bem_candidato_2022`, `bem_candidato_2026` (and BR aggregates under `meupolitico-api/.../data/tse/`).
- Câmara CEAP import path for 2024–2026; deputy sync from Dados Abertos.
- Working Angular surfaces: `/politicians`, `/politicians/:id`, `/rankings`, `/compare`.
- Mockups with **fictional** parliamentarians and metrics — layout and IA only. Do not reuse those names, photos, or numbers as proof.
- No real testimonials, case studies, press, or named users. Future work must not fabricate them.

## Product Principles

1. **Source on the figure.** If it cannot cite Câmara, TSE, Senado, or Portal da Transparência (once wired), it does not ship.
2. **Citizen, not clerk.** Design and copy assume someone overseeing a mandate between work and dinner, not a researcher dumping CSVs.
3. **Say what the data is not.** Presence ≠ full absence roll; TSE assets ≠ annual wealth; CEAP categories are our reading of official files.
4. **Public data only.** No invented people, quotes, or scores. Mock content stays labeled as mock.
5. **Destination is the mockup’s product, not its fake numbers.** Accounts, alerts, favorites, Senado, and sourced legislative indicators are in scope; they wait on real ingestion.
