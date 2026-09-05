# MeuPolítico Web

Angular frontend for **MeuPolítico** — a political transparency app focused on Brazil.

Browse federal deputies, parliamentary expenses (CEAP), attendance, declared assets (TSE), rankings, and side-by-side comparison. Data comes from official public sources via the MeuPolítico API.

## Stack

| Layer | Technology |
|--------|------------|
| Framework | Angular 21 |
| Language | TypeScript |
| Styling | SCSS |
| API client | HttpClient + dev proxy |

## Prerequisites

- Node.js 20+ (LTS recommended)
- npm 10+
- Running **MeuPolítico API** on `http://localhost:8080`

## Setup

```bash
npm install
```

Dev proxy (`proxy.conf.json`) forwards `/api` to the Spring API:

```json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true
  }
}
```

Ensure `angular.json` → `serve` → `options` includes:

```json
"proxyConfig": "proxy.conf.json"
```

## Run

```bash
ng serve
```

Open [http://localhost:4200](http://localhost:4200).

The API must be running on port **8080**, otherwise politician/expense requests will fail.

## Main routes

| Path | Description |
|------|-------------|
| `/politicians` | Search and list |
| `/politicians/:id` | Profile (expenses, attendance, assets) |
| `/rankings` | Top 50 rankings |
| `/compare` | Compare up to 3 politicians |

## Features

- Name search for politicians
- Expense table with filters (supplier, date range, min amount) and sort
- Attendance and asset sections with official source disclaimers
- Rankings: expenses, attendance, assets
- Comparison view

## Data notes (honest limits)

- **Expenses:** Chamber of Deputies CEAP (official yearly files)
- **Attendance:** presence at Chamber events (official files list who was present; they are not a full absence roster)
- **Assets:** TSE declarations at **election registration** (e.g. 2022, 2026) — not updated yearly during the term
- Current scope: **federal deputies**

## Project layout

```text
src/app/
  core/           # models, HTTP services
  features/       # politicians, rankings, comparison
  shared/         # layout, pipes
```

## Related

Backend API repository: **meupolitico-api** (Spring Boot + PostgreSQL).

## License / use

Built for civic transparency and academic use (TCC).  
Official data must respect Câmara dos Deputados and TSE open-data terms.
