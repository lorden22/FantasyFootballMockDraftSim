# CLAUDE.md

Guidance for Claude Code when working in this repository.

## Layout
Maven multi-module build (root `pom.xml`, Spring Boot 3.5 BOM, Java 17):
- `core/` — **all** business logic and SQL, shared by both clients. Package `com.mockdraft.core`.
  - `MockDraftCore` wires everything from a `DataSource` and runs migrations.
  - `service/DraftService`, `service/AuthService` — the only entry points clients should call.
  - `draft/` — `SnakeOrder`, `DraftState`, `DraftStrategy` (CPU picks), `RosterBuilder` (lineups).
  - `db/Database` — JDBC helper with thread-bound transactions; `db/Migrator` + `resources/.../db/V*.sql`.
- `WebApp/Backend/` — Spring Boot API, thin controllers over core (`com.mockdraft.web`). Contract: `docs/API.md`.
- `WebApp/frontend/` — React/TS/Tailwind SPA (Vite). Built `dist/` is bundled into the backend jar as static files.
- `consolebased/` — terminal client over core (`com.mockdraft.console`), shaded jar.

## Rules
- New behaviour goes in `core` first; clients only translate input/output. Keep web and console in parity.
- SQL must run on both MariaDB and H2 (`MODE=MariaDB`). Schema changes = new `V<n>__*.sql` + entry in `Migrator.MIGRATIONS`.
- Every draft query is scoped by owner; never take a username from request parameters — use the session principal.
- Validation rules live in `core/service/Validators` so both clients enforce the same constraints.

## Commands
```bash
mvn test                                         # all Java tests (H2, no Docker needed)
mvn -pl WebApp/Backend -am test                  # one module (+ what it depends on)
./devStartupWebApp.sh                            # backend (dev profile, H2) :8080 + Vite :5173
./devStartupConsole.sh                           # console app, embedded DB in ~/.mockdraft
(cd WebApp/frontend && npm run typecheck && npm run build)
docker compose up -d --build                     # production-style stack (needs .env)
```
