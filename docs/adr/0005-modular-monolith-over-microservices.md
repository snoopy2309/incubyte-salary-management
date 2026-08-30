# ADR-0005: Modular monolith over microservices (and micro-frontends)

- **Status:** Accepted
- **Date:** 2026-08-30

## Context

Salary management could be decomposed into services (employees, salaries, insights) and the UI into micro-frontends. Microservices and micro-frontends solve *organisational* scaling — many teams shipping independently — and very high, unevenly-distributed load. This project is a single domain, built by one person, over ~10,000 records: none of those forces are present.

## Decision

Build a **modular monolith** backend and a **single React app**. Preserve clean internal boundaries (package-by-feature, layered services) so a module *could* be extracted into a service later if a real need emerged.

## Consequences

- Avoids the operational tax of microservices — multiple deployments, inter-service networking, distributed data, and harder end-to-end testing — that would buy nothing here.
- Retains the benefits usually sought from services (modularity, testability, clear seams) through disciplined internal structure.
- Directly reflects the brief's "judgment over complexity"; choosing the monolith *deliberately* (not from unawareness) is the intended signal.
- If the system ever needed independent scaling of, say, insights, the existing boundaries make extraction straightforward.
