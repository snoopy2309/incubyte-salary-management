# ADR-0001: PostgreSQL over SQLite

- **Status:** Accepted
- **Date:** 2026-08-30

## Context

The brief allows any relational database and names SQLite as an example. SQLite is attractive for zero-setup local runs. However, the software must also be **deployed to a public URL**, where hosting containers have ephemeral disks — a file-based SQLite database would not persist reliably.

## Decision

Use **PostgreSQL** in every environment. Provide a `docker-compose` file so a reviewer still gets a one-command local run without installing a database.

## Consequences

- Production-like locally and in the cloud; the same database engine everywhere avoids "works on SQLite, breaks on Postgres" surprises.
- Enables Postgres features we rely on for insights (e.g. `percentile_cont` for median).
- Slightly more local setup than a bare file — mitigated by `docker-compose`.
- Cloud database runs on a managed free tier (Neon), which persists across deploys.
