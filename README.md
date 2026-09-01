# Employee Salary Management

A web tool for ACME's HR team to manage salary data across **10,000 employees in multiple countries**, and to **answer questions about how the organisation pays its people** — with every figure normalised to a common currency (USD).

> Built as a take-home assessment, emphasising clear thinking, right-sized design, clean tested code, and an honest record of how it was built with AI.

<!-- Add once deployed -->
**Live demo:** _to be added_ · **Demo video:** _to be added_

**Demo login:** `hr@acme.com` / `acme1234`

## Features

- **Employees** — searchable, filterable (country / department), paginated table; each salary shown in its **local currency and in USD**.
- **Pay insights dashboard** — total payroll, headcount, **average & median** salary; average vs median **by country** and **by department**; a **salary-distribution** histogram. All USD-normalised.
- **10,000-employee seed** across six countries/currencies, deterministic and reproducible.

## Tech stack

| Layer | Choice |
|---|---|
| Backend | Java 17, Spring Boot, PostgreSQL, Flyway |
| Frontend | React (Vite), MUI, Recharts, React Query, React Router |
| Tests | JUnit 5 + Mockito + MockMvc · Vitest + React Testing Library |
| API docs | OpenAPI / Swagger UI |

Architecture, decisions, and trade-offs are documented in [`docs/`](docs/) — start with the [requirements](docs/requirements.md) and [architecture](docs/architecture.md).

## Getting started

**Prerequisites:** Docker, **JDK 17**, Node 20.

```bash
# 1. Start PostgreSQL (creates the app + test databases)
docker compose up -d

# 2. Backend — seed once, then run
cd backend
./gradlew bootRun --args='--spring.profiles.active=seed'   # inserts 10,000 employees, then exits
./gradlew bootRun                                          # starts the API on :8080

# 3. Frontend (in another terminal)
cd frontend
npm install
npm run dev                                                # opens on :5173
```

Then open **http://localhost:5173** and sign in with the demo login above.

- API docs (Swagger UI): **http://localhost:8080/swagger-ui/index.html**

> Note: the Gradle wrapper runs on **JDK 17**. If your default JDK is newer, set `JAVA_HOME` to a JDK 17 for the backend commands.

## Running the tests

```bash
cd backend && ./gradlew test     # unit + integration (integration needs the DB from step 1)
cd frontend && npm test          # component + logic tests
```

Tests are fast and deterministic; the database-backed tests skip automatically if the local Postgres is not running, so the unit suite always runs.

## Project structure

```
├── backend/    Spring Boot API (feature packages: employee, salary, currency, insights, seed)
├── frontend/   React app (pages, components, api client)
├── docs/       requirements, architecture, ADRs, concepts, trade-offs, AI collaboration
└── docker-compose.yml
```

## How it was built

This project was built with the Claude Code agentic assistant using strict TDD and small, incremental commits. See [`docs/ai-collaboration.md`](docs/ai-collaboration.md) for how AI was used — and where its suggestions were overridden — and [`CLAUDE.md`](CLAUDE.md) for the working agreement it followed.
