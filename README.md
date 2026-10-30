# MockDraft Pro — Fantasy Football Mock Draft Simulator

Run realistic snake drafts against CPU managers, from the browser or the terminal. Both clients run on the
**same core engine and the same database schema**: a draft started in the console can be resumed on the web,
and vice-versa (when they share a database).

```
┌──────────────────┐   ┌──────────────────────┐
│ React web app    │   │ Console app          │
│ WebApp/frontend  │   │ consolebased/        │
└────────┬─────────┘   └──────────┬───────────┘
         │ JSON /api              │ direct calls
┌────────▼─────────┐              │
│ Spring Boot API  │              │
│ WebApp/Backend   │              │
└────────┬─────────┘              │
         └──────────┬─────────────┘
           ┌────────▼────────┐
           │  mockdraft-core │  draft engine · CPU strategy · rosters · auth · all SQL · migrations
           │  core/          │
           └────────┬────────┘
                    │ JDBC
        MariaDB (production)  or  H2 embedded (console default, web "dev" profile, tests)
```

| Module | What it is |
|---|---|
| `core/` | Shared Java library: domain model, snake-order math, CPU draft strategy, lineup builder, auth (BCrypt, hashed session/reset tokens), JDBC repositories, schema migrations. |
| `WebApp/Backend/` | Spring Boot 3 REST API ([docs/API.md](docs/API.md)); also serves the built frontend. |
| `WebApp/frontend/` | React + TypeScript + Tailwind single-page app (Vite). |
| `consolebased/` | Terminal client; single self-contained jar. |
| `WebScraping/` | Rankings scraper; its output `PlayerData.txt` is bundled into `core` and seeded on first run. |

## Quick start

### Console (nothing to install but Java 17+ and Maven)
```bash
./devStartupConsole.sh          # builds on first run, stores data in ~/.mockdraft
./devStartupConsole.sh --help   # connect to MariaDB instead, disable colors, etc.
```

### Web app, local development (Java 17+, Maven, Node 20+)
```bash
./devStartupWebApp.sh
# App: http://localhost:5173   API: http://localhost:8080/api/health
```
The backend runs with the `dev` profile: an embedded H2 database under `WebApp/Backend/data/`, and password
reset links printed to the terminal instead of emailed.

### Production (Docker)
```bash
cp .env.example .env            # set strong DB passwords, APP_BASE_URL, SMTP settings
docker compose up -d --build    # MariaDB + app on http://127.0.0.1:8080
docker compose --profile https up -d   # optional: Caddy with automatic HTTPS for $DOMAIN
docker compose run --rm console        # console client sharing the production database
```
The app container is non-root, read-only, and drops all Linux capabilities. MariaDB and the app listen
only on `127.0.0.1`; put the `https` profile (or your own TLS proxy) in front for public access.

## Testing
```bash
mvn test                                    # core engine, auth, migrations, HTTP API, console flows
(cd WebApp/frontend && npm run typecheck && npm run build)
```

## Security notes
- Passwords: BCrypt (cost 12). Accounts from the old app (salted SHA-256) still sign in and are upgraded
  to BCrypt automatically.
- Sessions: random 256-bit tokens in an `HttpOnly; Secure; SameSite=Strict` cookie; only SHA-256 hashes are
  stored. Logout, password change and password reset revoke sessions server-side.
- CSRF: double-submit token (`XSRF-TOKEN` cookie → `X-XSRF-TOKEN` header) on every state-changing request.
- Every draft query is scoped to the signed-in user; other users' drafts return 404.
- Rate limits on sign-in, registration, password reset and draft actions.
- Strict Content-Security-Policy (no inline or third-party scripts), HSTS, `X-Frame-Options: DENY`,
  `nosniff`, Referrer and Permissions policies.
- Password reset: single-use, 30-minute tokens (hashed at rest); responses never reveal whether an email
  is registered; links are built from `APP_BASE_URL`, never the request `Host` header.

## Upgrading from the 1.x app
On first start against an old database, the schema migrator detects the old `teams.team_array` layout,
renames the old tables to `legacy_*`, and copies users, players and every draft into the new schema.
Nothing is deleted; drop the `legacy_*` tables once you're satisfied.

## Updating player rankings
Run `WebScraping/getRankingScript.py` to regenerate `WebScraping/PlayerData.txt`. Rankings are seeded into
an empty `players` table; existing databases keep their rankings so past drafts stay accurate.
