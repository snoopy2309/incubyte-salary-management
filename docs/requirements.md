# Requirements — Employee Salary Management

*One-page requirements for the ACME salary management tool. Written before implementation to fix scope and make the deliberate cuts explicit.*

## Goal

Give ACME's HR team a web-based tool to **manage employee salary data** and, more importantly, **answer questions about how the organisation pays its people** — replacing today's error-prone, hard-to-query spreadsheets.

## User & context

- **Primary user:** HR Manager at ACME.
- **Scale:** ~10,000 employees across **multiple countries**, and therefore **multiple currencies**.
- **Today:** salary data lives in spreadsheets — tedious to maintain and nearly impossible to query for org-wide answers ("what do we pay, on average, per country?").

## Key questions this tool answers

As the HR Manager, I can now answer — in seconds, not spreadsheet hours:

- *What does the organisation spend on salaries in total?*
- *What do we pay on average — and at the median — overall, per country, and per department?*
- *How is pay distributed — how many people sit in each salary band?*
- *How does pay compare across countries once currencies are normalised to a common base?*
- *Where is our headcount and salary cost concentrated?*

Each question maps directly to a feature below — the product exists to answer these.

## In scope

1. **Employee & salary records** — each employee has a country, department, job title, join date, and a current salary held in that employee's **local currency**.
2. **Browse & find** — a paginated, searchable, filterable, sortable list of employees, plus an individual employee view. The list never loads all 10,000 rows at once.
3. **Pay insights (the core value)** — a dashboard that answers real HR questions, with **every monetary figure normalised to a single base currency (USD)** so cross-country numbers are meaningful:
   - Total payroll cost, headcount, and organisation-wide average & median salary.
   - Average **and median** salary, headcount, and cost **by country** and **by department**.
   - Salary **distribution** across bands (how many people sit in each pay band).
4. **Manage employees (confirmed with Incubyte)** — HR can **create** a new employee, **update** their salary and details, and **deactivate (soft-delete)** an employee. The list and insights reflect only active employees.
5. **Seed data** — a deterministic script generating 10,000 realistic employees across ~6 countries/currencies, so the tool and its tests are reproducible.

*Nice-to-have (only if core is solid and time allows):* top earners, per-department pay range (min/max/spread).

## Out of scope — and why

| Cut | Reasoning |
|-----|-----------|
| **Authentication / roles / user management** | Not required by the brief; the persona is a single HR role. Adding it is security-sensitive surface that pulls effort from the graded core (insights, tests, clean code) without touching it. The design for it is documented as a future step, which demonstrates the judgment without the risk. |
| **Salary history / audit trail** | v1 shows one *current* salary per employee. The schema keeps an `effectiveDate` on salary, so history is a **data-additive** change later — designed for, not built now. |
| **Excel / bulk import** | The brief's pain point is *querying* the data, not migrating it. A seed script covers the demo need; import is a well-understood extension. |
| **Live foreign-exchange rates** | Rates are stored in a table and treated as reference data. Real-time FX adds an external dependency and non-determinism (bad for tests) for no gain in demonstrating the core idea. |
| **Hard delete of employee records** | Deletion is a **soft-delete (deactivate)** so records are recoverable and history is preserved — the norm for HR data. Permanent deletion is deliberately not offered. |
| **Internationalisation (UI language)** | Multi-*currency* matters for correctness; multi-*language UI* does not serve the HR-Manager persona for this exercise. |

## Key assumptions

- Each employee has exactly **one current salary**, stored in their **local currency** with an explicit currency code.
- Cross-country comparisons are made by converting to **USD** via a stored `CurrencyRate` table (documented, deterministic). *Confirmed by Incubyte.*
- 10,000 employees is a **modest** dataset for a relational database; correctness, clean pagination, indexed filters, and SQL-side aggregation matter more than distributed-systems concerns. We deliberately do **not** over-engineer for scale.
- The HR Manager both **manages** employee records (create, update, deactivate) and **questions** pay data (analytics). *Confirmed by Incubyte — full CRUD is in scope.*

## What "done" means

- Fully functional backend + UI, runnable locally with a single command and **deployed to a public URL**, with a short demo video.
- A meaningful, **fast and deterministic** unit-test suite covering the core logic — especially currency normalisation and the insight calculations.
- A commit history that shows the solution evolving in small, intentional steps.
- Supporting artifacts (this document, architecture notes, ADRs, and a record of how AI tooling was used).

*Non-functional:* list and insight queries respond quickly on 10k rows (indexed filters, SQL-side aggregation); the seed is deterministic so behaviour and tests are reproducible.

## Guiding principle

> The brief explicitly rewards **good engineering judgment over complexity.** This scope is deliberately lean: a right-sized system with one genuinely thoughtful capability — **currency-normalised pay insights, reported with both average and median** — built cleanly and tested well.
