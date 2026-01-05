# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

### Production Deployment
```bash
./deploy.sh              # Full deployment (build + start)
./deploy.sh build        # Build only
./deploy.sh restart      # Restart services
./deploy.sh stop         # Stop all services
./deploy.sh logs         # View logs
./deploy.sh health       # Check health status
```

### Development Mode
```bash
./devStartupWebApp.sh    # Start backend (Docker) + frontend (Live Server)
                         # Backend API: https://localhost:8443/api/
                         # Frontend: https://localhost:5500/
                         # DB exposed: localhost:33306

./devStartupConsole.sh   # Start console app in Docker
                         # Test credentials: username=a, password=a
```

### Building Individual Components
```bash
# Backend (from WebApp/Backend/)
mvn clean package -DskipTests

# TypeScript compilation (from WebApp/webpages/)
tsc
```

### Docker Commands
```bash
docker-compose up -d              # Start all services
docker-compose down               # Stop all services
docker-compose logs -f            # Follow logs
docker exec fantasy-football-mock-draft mysql -u root -ppassword db  # DB shell
```

## Architecture

```
┌─────────────────┐     HTTPS (443)     ┌────────────────┐
│  Browser/Client │ ◄─────────────────► │     nginx      │
└─────────────────┘                     └───────┬────────┘
                                                │
                              ┌─────────────────┴─────────────────┐
                              │                                   │
                    Static files (/*.html)           API (/api/*)
                              │                                   │
                              ▼                                   ▼
                    ┌─────────────────┐              ┌────────────────────┐
                    │ WebApp/webpages │              │ Spring Boot (8443) │
                    │ (HTML/JS/CSS)   │              │ Controllers/Services│
                    └─────────────────┘              └──────────┬─────────┘
                                                               │
                                                          JDBC │
                                                               ▼
                                                     ┌─────────────────┐
                                                     │ MariaDB (3306)  │
                                                     │ Database: db    │
                                                     └─────────────────┘
```

### Component Overview

| Directory | Purpose |
|-----------|---------|
| `WebApp/Backend/` | Spring Boot 2.7.4 REST API (Java 17) |
| `WebApp/webpages/` | Frontend (HTML/TypeScript/CSS) |
| `consolebased/` | Standalone console-based draft simulator |
| `shared/` | Common Java classes used by both apps |
| `WebScraping/` | Python script for player ranking data |

### Backend Structure (WebApp/Backend/src/main/java/com/example/Mock/)
- `API/` - REST Controllers: DraftController, LoginController, HealthController
- `Service/` - Business logic: DraftServices, LoginServices
- `DAO/` - Data access objects and database queries
- `config/` - Security configuration (CORS, SSL)

### Key API Endpoints
- `POST /api/teams/startDraft` - Initialize new draft
- `POST /api/teams/userDraftPlayer` - User makes a pick
- `POST /api/teams/simTo` - Simulate to next user turn
- `GET /api/login/attemptLogin` - User authentication
- `GET /api/health` - Health check

## Database

**Schema file:** `mockDraft.sql`

**Tables:**
- `users` - Accounts with SHA-256 hashed passwords + salt
- `drafts` - Draft sessions with progress tracking
- `teams` - Team rosters per draft
- `players` - Player database (540 players from WebScraping/PlayerData.txt)
- `draft_logs` - Audit history

**Connection (dev):** `localhost:33306` | User: `root` | Password: `password`

## Tech Stack

- **Backend:** Java 17, Spring Boot 2.7.4, Spring Security, Spring Data JPA
- **Frontend:** HTML5, TypeScript (ES2022), CSS3
- **Database:** MariaDB 10.11
- **Infrastructure:** Docker, Docker Compose, nginx
- **Build:** Maven 3.x, TypeScript compiler

## Configuration Files

| File | Purpose |
|------|---------|
| `docker-compose.yml` | Multi-container orchestration |
| `nginx.conf` | Reverse proxy, SSL, security headers |
| `WebApp/Backend/src/main/resources/application.properties` | Dev Spring config |
| `env.production` | Production environment variables |
| `tsconfig.json` | TypeScript compiler options |

## Testing

```bash
# Run backend tests (from WebApp/Backend/)
mvn test

# Maven test configuration uses Surefire Plugin v3.2.1 with forkCount=0
```
