# ADR-0003: Currency normalisation for cross-country insights

- **Status:** Accepted
- **Date:** 2026-08-30

## Context

Employees span multiple countries, so salaries are held in different currencies. The core product value — "answer questions about how the org pays people" — requires aggregating across them (e.g. average salary by department). Averaging mixed currencies is meaningless, so amounts must be normalised to a common base before aggregation.

## Decision

- Store each salary in its **local currency** as a `BigDecimal` plus an explicit currency code. **Never use floating-point** for money.
- Keep a **`CurrencyRate`** table mapping each currency to a rate against a single base currency (**USD**), treated as versioned reference data.
- **Convert to USD at query time**, inside the SQL aggregation, from that single source of truth. Do **not** denormalise a stored USD amount onto each salary row.

## Consequences

- One source of truth for rates; changing a rate is a data update, with no stale derived columns to rebuild.
- `BigDecimal` gives exact monetary arithmetic (no rounding drift).
- At 10k rows the conversion join is trivially fast; denormalisation would add maintenance burden for no meaningful performance gain.
- Rates are static reference data in v1 (not live FX) — deliberate, to keep behaviour deterministic and testable.
- The normalisation logic is pure and isolated, making it the highest-value target for fast, deterministic unit tests.
