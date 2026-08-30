# ADR-0002: React for the frontend

- **Status:** Accepted
- **Date:** 2026-08-30

## Context

The brief permits either "ReactJS or NextJS" or "AngularJS with Java" for the UI. The role's job description leans Java/Angular. The UI here is a single dashboard-style app: an employee list and a pay-insights view.

## Decision

Build the frontend as a **single React app (Vite)**.

## Consequences

- React is explicitly allowed by the brief and is well suited to this small, component-driven UI.
- A single-page app is sufficient; **no micro-frontend** architecture (that solves multi-team frontend scaling, which does not apply — see ADR-0005).
- Next.js is not needed: there is no server-side-rendering or SEO requirement for an internal HR tool, so its added surface would not earn its place.
- Trade-off: a lighter Angular signal for an Angular-leaning role, accepted in favour of a cleaner, well-tested implementation.
