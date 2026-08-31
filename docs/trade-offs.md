# Trade-offs, performance & scaling

What was chosen for the stated requirement (~10,000 employees, internal HR tool), what the ceiling is, and how it would scale. The guiding principle is the brief's own: **good engineering judgment over complexity** — build the right-sized thing, and know the path beyond it.

## Pagination: offset now, keyset at scale

**Choice:** offset pagination (`page`/`size` with `totalElements`/`totalPages`, plus `hasNext`/`hasPrevious`).

**Why here:** the user is an HR manager browsing a ~10k-row table. Offset gives them a **total count** ("1,679 employees") and **jump-to-page**, both genuine UX value. At 10k rows, `OFFSET`/`COUNT` run in single-digit milliseconds.

**Ceiling:** offset degrades as the table grows — `OFFSET 900000 LIMIT 20` makes the database scan and discard every skipped row, so deep pages get linearly slower. Somewhere in the low millions this becomes noticeable.

**Scaling path (deliberately not built now):**
- Switch to **keyset / cursor pagination** ("seek method"): `WHERE (sort_key, id) > (:last_key, :last_id) ORDER BY sort_key, id LIMIT n`. With an index on the sort key it is O(log n) per page regardless of depth — this is what large APIs (Stripe, GitHub, Slack) use, returning an opaque `next` cursor.
- It is a **contained change**: pagination lives behind the repository/service boundary, so it would be exposed as a **v2 cursor endpoint** without rewriting v1.
- Trade-off accepted at that point: cursors drop jump-to-page and (usually) the exact total — acceptable for very large datasets, not desirable for a 10k HR table today.

## Count queries

`COUNT(*)` with the current filters is negligible at 10k. At large scale an exact count over millions of rows with arbitrary filters is expensive; the mitigations then are an **approximate count** (`pg_class.reltuples`), a **maintained counter**, or simply **dropping the total** (which keyset pagination does anyway).

## Search: SQL LIKE now, an index/engine at scale

**Choice:** case-insensitive `LOWER(col) LIKE '%q%'` over name/email.

**Ceiling — the bigger wall than pagination:** a leading-wildcard `LIKE '%q%'` **cannot use a normal B-tree index**, so it is a full scan. Fine at 10k, not at millions.

**Scaling path:** a **PostgreSQL trigram index** (`pg_trgm` GIN) to make substring search indexable, or a dedicated **search engine** (Elasticsearch/OpenSearch) if ranking/typo-tolerance is needed.

## Currency rates: reference data, not live FX

Rates are stored and treated as versioned reference data. Deliberately **not** a live FX feed: it would add an external dependency and non-determinism (bad for tests and reproducible demos) with no benefit to demonstrating the core idea. A scheduled job could refresh the table if real rates were needed.

## Aggregation performance

Insights are computed **in SQL** (`GROUP BY`, `AVG`, `percentile_cont`), never by loading rows into the app and looping — this avoids N+1 and keeps them fast. Indexes exist on the filtered/grouped columns (`country`, `department`). At much larger scale, insight queries would move to **pre-aggregated / materialised views** refreshed periodically, since HR reporting tolerates slightly stale numbers.

## Deliberately deferred features

- **Authentication / roles** — documented design only (see ADR-0004); not required by the brief.
- **Salary history** — schema is history-ready (`effective_date`); v1 shows the current salary.
- **Server-side sorting** — the list has a stable order (by id); column sorting is a straightforward extension.
- **Bulk import** — a seed script covers the demo; import is well-understood.

Each is additive on the current design, not a rewrite — which is the point of keeping the system small but evolvable.
