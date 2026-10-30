#!/usr/bin/env bash
# Local development for the web app — no Docker needed.
#   Backend:  Spring Boot "dev" profile on http://localhost:8080 (embedded H2 DB in WebApp/Backend/data/)
#   Frontend: Vite dev server on http://localhost:5173 with hot reload; /api is proxied to the backend
# Requires: Java 17+, Maven 3.9+, Node 20+.  Ctrl-C stops both.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT"

for tool in java mvn npm; do
    command -v "$tool" >/dev/null || { echo "Missing required tool: $tool" >&2; exit 1; }
done

echo "▶ Building core module..."
mvn -q -B -pl core -am install -DskipTests

echo "▶ Installing frontend dependencies..."
(cd WebApp/frontend && npm install --no-audit --no-fund)

cleanup() {
    echo
    echo "▶ Stopping..."
    jobs -p | xargs -r kill 2>/dev/null || true
}
trap cleanup EXIT INT TERM

echo "▶ Starting backend (dev profile) on http://localhost:8080 ..."
(cd WebApp/Backend && mvn -q -B spring-boot:run -Dspring-boot.run.profiles=dev) &

echo "▶ Waiting for the API to come up..."
for _ in $(seq 1 90); do
    if curl -fsS http://localhost:8080/api/health >/dev/null 2>&1; then
        break
    fi
    sleep 1
done

echo "▶ Starting frontend on http://localhost:5173 ..."
(cd WebApp/frontend && npm run dev) &

cat <<MSG

  App:            http://localhost:5173
  API health:     http://localhost:8080/api/health
  Reset emails:   printed in this terminal (dev profile)
  Dev database:   WebApp/Backend/data/  (delete it to start fresh)

MSG
wait
