# How this was built with AI

The brief asks for the prompts and approach used with AI tools. This is an honest account of how the solution was built with an agentic assistant (Claude Code) — the workflow, the intentional choices, and, importantly, **where the AI was overridden**.

## Working method

1. **Discuss before building.** The requirements, scope, and every significant decision were talked through *before* code — the reasoning lives in [requirements](requirements.md), [architecture](architecture.md), and the [ADRs](adr/).
2. **A written spec for the AI.** [`CLAUDE.md`](../CLAUDE.md) is the standing instruction the assistant worked under: strict TDD, small commits, `BigDecimal` money, scope discipline, no secrets.
3. **Strict TDD.** Every feature was driven test-first — write the failing test, watch it fail, write the minimum to pass, refactor. The commit history shows this progression.
4. **Small, incremental commits.** Documentation and design first, then a runnable skeleton, then one tested feature at a time.

## Where the AI was directed or overridden

Good AI use is not accepting every suggestion. Key human decisions that changed the AI's default:

- **React over Angular.** The AI first leaned toward Angular (to match the job description). Overridden in favour of React — it is allowed by the brief and is the more confidently *defensible* choice here.
- **PostgreSQL only.** The AI proposed SQLite for zero-setup (as the brief exemplifies). Overridden to Postgres everywhere for a production-realistic setup; `docker-compose` restores the easy local run.
- **Deployment options were fact-checked, not trusted.** Rather than accept the AI's memory of "free tiers", it was made to look them up and report current, sourced options.
- **Pagination trade-off.** Pushed on how this behaves at 10M rows. Result: keep offset for the 10k HR use case (counts + jump-to-page), but **document the keyset scaling path** rather than over-engineer — captured in [trade-offs](trade-offs.md).
- **Product direction.** The plain first dashboard was rejected in favour of a richer one (charts, styled cards); a static demo login was added on request, with real auth explicitly deferred (ADR-0004).
- **Verify before committing.** UI work was reviewed in the browser before being committed, not accepted from tests alone.

## Intentional-use highlights

- **Computed, not eyeballed.** The chart colours were validated for colour-blind safety with a script (Average/Median blue-green, CVD ΔE 26.5), not chosen by taste.
- **Real databases in tests.** Integration tests run against real PostgreSQL (the migration and `percentile_cont` median are verified against the actual engine).
- **Debugging, honestly.** Several things the AI got wrong were caught and fixed: Testcontainers was incompatible with the local Docker 29 engine (pivoted to the compose DB), Recharts bars froze mid-animation under React StrictMode (disabled the animation), and a null search parameter needed an explicit SQL `CAST`. These are recorded where relevant.

## Result

~40 tests (backend + frontend), a clean feature-by-feature commit history, and a small, right-sized system with one genuinely thoughtful capability — currency-normalised pay insights with median — built and verified end-to-end.
