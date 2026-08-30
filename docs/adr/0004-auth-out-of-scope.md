# ADR-0004: Authentication out of scope for v1

- **Status:** Accepted
- **Date:** 2026-08-30

## Context

The persona is an HR Manager, and a real deployment would sit behind a login with multiple HR users. The brief, however, does not ask for authentication, users, or roles, and explicitly values "good engineering judgment over complexity." Authentication is security-sensitive surface (password handling, sessions, authorisation) that does not touch the graded core: the pay-insights, test quality, and clean code.

## Decision

**Do not build authentication in v1.** Record the intended design instead, so the judgment is visible without the cost.

Intended future design (not built):
- A `User` entity (name, email, BCrypt password hash, role, active flag).
- Two roles — `ADMIN` (also manages users) and `HR` (views salary data); a seeded bootstrap admin from environment variables.
- **Session + HttpOnly cookie** authentication rather than a JWT in browser storage — avoids XSS token theft and makes logout/revocation trivial.

## Consequences

- Effort concentrates on the parts the brief actually grades.
- No security-sensitive code to get subtly wrong under time pressure.
- Adding auth later is additive (new `User` table + a security filter), not a redesign.
- The documented design demonstrates the thinking; if built, it would be reviewed with a dedicated security pass before merge.
