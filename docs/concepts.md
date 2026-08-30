# Concepts & justifications

A living cheat-sheet of every significant concept used in this project — *what it is*, *why we chose it*, and *what to say if asked*. Grows as the build progresses. Companion to the [ADRs](adr/), which cover the largest decisions in more depth.

## Architecture & design

| Concept | What it is | Why here / if asked |
|---|---|---|
| **Modular monolith** | One deployable backend, internally split by feature with clear layers | Single domain, one developer, 10k rows — microservices would add operational cost for no benefit. Kept internal seams so a piece could be extracted later. ([ADR-0005](adr/0005-modular-monolith-over-microservices.md)) |
| **Package-by-feature** | Code grouped by domain (`employee`, `salary`, `insights`) not by layer | Related code lives together; easier to navigate and to extract later than package-by-layer |
| **Layered (web → service → repository)** | Controllers handle HTTP, services hold logic, repositories talk to the DB | Separation of concerns; the business logic stays testable without HTTP or a database |
| **DTOs (Data Transfer Objects)** | Plain response/request objects, separate from JPA entities | Decouples the API shape from the DB schema; avoids leaking entities and lazy-loading surprises |

## Backend stack

| Concept | What it is | Why here / if asked |
|---|---|---|
| **Spring Boot** | Convention-over-configuration Java web framework | Industry standard, matches the JD, minimal boilerplate, huge ecosystem |
| **Java 17** | LTS Java version we compile against | Fully supported by Spring Boot and the current tooling; broad compatibility (chosen over Java 25, which the build tool doesn't yet support) |
| **Gradle + wrapper** | Build tool; the wrapper (`./gradlew`) pins the exact Gradle version | Reviewers build with no Gradle install; identical version everywhere = reproducible |
| **JPA / Hibernate** (`spring-data-jpa`) | ORM mapping Java objects ↔ DB tables | Removes hand-written CRUD SQL; we still drop to explicit SQL for the insight aggregations where it matters |
| **Flyway** | Versioned, ordered SQL migrations checked into git | Schema is reproducible and reviewable across environments; safer than letting Hibernate auto-create tables |
| **`spring.jpa.hibernate.ddl-auto: validate`** | Hibernate only verifies entities match the schema | Flyway stays the single source of truth for schema; Hibernate never silently changes it |
| **Environment-variable config** | Settings injected from the environment, not committed | 12-factor; no secrets in the repo; the same build runs locally and in the cloud |

## Money & data

| Concept | What it is | Why here / if asked |
|---|---|---|
| **`BigDecimal` for money** | Exact decimal type (not `double`/`float`) | Floating point introduces rounding error — unacceptable for salaries |
| **Currency normalisation** | Convert each salary to a USD base via a rates table before aggregating | Averaging mixed currencies is meaningless; conversion happens in SQL from one source of truth ([ADR-0003](adr/0003-currency-normalisation.md)) |
| **`percentile_cont` (median)** | Postgres function for a continuous percentile | Median resists skew from a few very high salaries — a more honest "typical pay" than the average |
| **Database indexes** | Lookup structures on filtered/grouped columns (`country`, `department`) | Keeps list filtering and insight grouping fast; the right-sized answer to 10k rows |

## Testing

| Concept | What it is | Why here / if asked |
|---|---|---|
| **TDD (red → green → refactor)** | Write the failing test first, then the code | Drives simple designs and guarantees every line is covered by an intent |
| **Unit tests (pure)** | Tests of logic with no Spring/DB | Fast and deterministic; the highest-value place to prove the currency/insight maths |
| **MockMvc** | Spring's tool to test controllers without a running server | Verifies the HTTP/JSON contract quickly |
| **Integration tests vs docker-compose Postgres** | DB-backed tests run against the real local Postgres, guarded by an availability check so they skip cleanly when it is down | Verifies migrations and SQL (e.g. `percentile_cont`) against real Postgres, not a fake. (Testcontainers was the first choice but the local Docker 29 engine has a client-negotiation incompatibility; running against the compose DB is reliable and equally real — a pragmatic trade-off.) |

*Frontend concepts are added when the frontend build begins.*
