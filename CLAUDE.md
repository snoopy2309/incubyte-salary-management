# CLAUDE.md — working agreement for this repo

Instructions for AI assistance on this project. This file is both a **guardrail** (how the AI should work here) and an **artifact** (a record of how AI was used intentionally, as the assessment asks).

## What this project is

Employee salary management for a multi-country org (~10,000 employees). The HR Manager manages salary data and **answers questions about how the org pays people**. See [`docs/requirements.md`](docs/requirements.md) and [`docs/architecture.md`](docs/architecture.md). Significant decisions are in [`docs/adr/`](docs/adr/).

## Stack

- **Backend:** Java 17, Spring Boot, Gradle, PostgreSQL, Flyway. Layered + package-by-feature (`employee`, `salary`, `insights`, `currency`).
- **Frontend:** React + Vite, a component library, React Query for data fetching.
- **Local:** `docker-compose` provides Postgres so the whole stack runs with one command.

## How to work here — non-negotiables

### Test-Driven Development
- **Write the failing test first, watch it fail, then write the minimum code to pass, then refactor.** No production code without a test that required it.
- **Run the tests, don't just compile.** Compilation passing is not "done"; the relevant test suite must be green.
- Tests must be **fast, deterministic, and readable**. No reliance on wall-clock time, random values, network, or test-ordering. Use a **fixed seed** for any data-dependent test.
- Test names read as behaviour: `averageSalaryByDepartment_convertsEachSalaryToUsd_beforeAveraging`.
- The highest-value unit tests are the **currency-normalisation** and **insight calculations** — pure logic, test them thoroughly.

### Money & correctness
- Money is **`BigDecimal` + currency code — never `double`/`float`**.
- Any cross-currency figure is converted to the USD base **before** aggregation (see ADR-0003).

### Commits
- **Small, incremental, meaningful** commits — the history should show the solution evolving.
- Conventional style: `feat:`, `test:`, `refactor:`, `docs:`, `chore:`, `fix:`.
- A feature commit **includes its tests**. Keep each commit green.
- Commits carry a `Co-Authored-By: Claude` trailer (intentional-AI-use transparency).

### Scope discipline
- The brief rewards **judgment over complexity**. Do not add auth, salary history, bulk import, live FX, microservices, or micro-frontends — these are deliberate cuts (see requirements + ADRs). If a change seems to need one, flag it rather than building it.
- Prefer the simplest thing that satisfies the requirement well.

### Security & hygiene
- **No secrets in the repo.** Config comes from environment variables; `.env` is git-ignored, with a committed `.env.example`.
- Validate inputs at the API boundary; return meaningful errors.

## Definition of done for a change
1. A test existed that required the change, and the suite is green.
2. Code is readable and matches surrounding conventions.
3. Docs/ADRs updated if a decision changed.
4. Committed in a small, well-messaged step.
