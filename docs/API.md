# Mock Draft API (v1)

All endpoints live under `/api`. Requests and responses are JSON (`Content-Type: application/json`).

## Conventions

**Authentication** — `POST /api/auth/login` sets an `md_session` cookie (`HttpOnly`, `Secure`, `SameSite=Strict`).
JavaScript cannot read it; call `GET /api/auth/me` to learn who is signed in.

**CSRF** — every `POST`/`PUT`/`PATCH`/`DELETE` must send header `X-XSRF-TOKEN` whose value equals the
`XSRF-TOKEN` cookie. The cookie is set on any response; call `GET /api/auth/csrf` (204) once on app start
if it is missing.

**Errors** — any non-2xx response has this body:

```json
{ "status": 400, "error": "Bad Request", "message": "Human readable message", "fields": { "username": "..." } }
```

`fields` is present only for validation errors. Status codes used: 400 validation, 401 not signed in /
bad credentials, 403 CSRF failure, 404 not found (also used for drafts owned by someone else), 409 conflict,
429 rate limited, 500 unexpected.

## Types

```ts
type Position = "QB" | "RB" | "WR" | "TE" | "K" | "DST";

type User = { username: string; email: string | null; createdAt: string /* ISO-8601 */ };

type Player = {
  rank: number;            // overall consensus rank, 1 = best
  name: string;
  position: Position;
  projectedPoints: number; // season projection
  adp: string;             // rank expressed as "round.pick" for this draft's size, e.g. "2.07"
};

type Team = { slot: number; name: string; isUser: boolean };

type Pick = {
  overall: number;      // 1-based overall pick number
  round: number;
  pickInRound: number;
  slot: number;         // team draft slot that made the pick
  teamName: string;
  player: Player;
};

type DraftStatus = "IN_PROGRESS" | "COMPLETE";

type DraftSummary = {
  id: number; teamName: string; numTeams: number; rounds: number; userSlot: number;
  status: DraftStatus; createdAt: string; completedAt: string | null;
  picksMade: number; totalPicks: number;
};

type DraftState = DraftSummary & {
  teams: Team[];               // ordered by slot
  picks: Pick[];               // ordered by overall
  currentPick: null | { overall: number; round: number; pickInRound: number; slot: number; teamName: string; isUser: boolean };
  nextUserPick: null | { overall: number; round: number; pickInRound: number; picksUntil: number }; // picksUntil 0 = on the clock
};

type RosterSlot = { label: "QB" | "RB1" | "RB2" | "WR1" | "WR2" | "TE" | "FLEX" | "K" | "DST" | "BN"; pick: Pick | null };

type Roster = {
  slot: number; teamName: string; isUser: boolean;
  starters: RosterSlot[];        // always 9 entries in the order above (pick null = empty)
  bench: RosterSlot[];           // label "BN"
  projectedStarterPoints: number;
};
```

## Auth

| Method | Path | Body | Response |
|---|---|---|---|
| GET | `/api/auth/csrf` | – | 204, sets `XSRF-TOKEN` cookie |
| POST | `/api/auth/register` | `{ username, password, email?: string \| null }` | 201 `User` (also signs in) |
| POST | `/api/auth/login` | `{ username, password }` | 200 `User` |
| POST | `/api/auth/logout` | – | 204 |
| GET | `/api/auth/me` | – | 200 `User` or 401 |
| POST | `/api/auth/password-reset` | `{ email }` | 202 (always, even if no such email) |
| POST | `/api/auth/password-reset/validate` | `{ token }` | 200 `{ valid: boolean }` |
| POST | `/api/auth/password-reset/confirm` | `{ token, password }` | 204 |

Rules: username 3–30 chars `[A-Za-z0-9_]`; password 8–72 chars, not the username, not a common password;
email optional, max 254 chars.

## Account (signed in)

| Method | Path | Body | Response |
|---|---|---|---|
| PUT | `/api/account/email` | `{ email: string \| null, currentPassword }` | 200 `User` |
| PUT | `/api/account/password` | `{ currentPassword, newPassword }` | 204 (signs out other sessions) |
| POST | `/api/account/delete` | `{ password }` | 204 (deletes user + all drafts, clears cookie) |

## Drafts (signed in)

| Method | Path | Body | Response |
|---|---|---|---|
| GET | `/api/drafts` | – | `DraftSummary[]` newest first |
| POST | `/api/drafts` | `{ teamName, numTeams /* 4–20 */, draftPosition: number \| null /* null = random */ }` | 201 `DraftState`; 409 if a draft is already in progress |
| GET | `/api/drafts/{id}` | – | `DraftState` |
| DELETE | `/api/drafts/{id}` | – | 204 |
| GET | `/api/drafts/{id}/available` | – | `Player[]` sorted by rank (all undrafted players) |
| POST | `/api/drafts/{id}/picks` | `{ playerRank }` | `DraftState` (409 if not your turn / player gone) |
| POST | `/api/drafts/{id}/auto-pick` | – | `DraftState` (makes the user's pick using the CPU strategy) |
| POST | `/api/drafts/{id}/simulate` | – | `DraftState` (CPU teams pick until it is the user's turn or the draft ends) |
| GET | `/api/drafts/{id}/teams/{slot}/roster` | – | `Roster` |

A draft becomes `COMPLETE` automatically when the last pick is made. Drafts are 15 rounds.

## Health

`GET /api/health` → `{ "status": "UP" }` (503 `{ "status": "DOWN" }` if the database is unreachable).
