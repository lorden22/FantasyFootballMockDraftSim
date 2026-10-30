#!/usr/bin/env bash
# Build (if needed) and run the console app. Extra arguments are passed through, e.g.:
#   ./devStartupConsole.sh                       # embedded database in ~/.mockdraft
#   MOCKDRAFT_DB_PASSWORD=... ./devStartupConsole.sh --db-url jdbc:mariadb://127.0.0.1:3306/mockdraft --db-user mockdraft
#   ./devStartupConsole.sh --help
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JAR="$ROOT/consolebased/target/mockdraft-console.jar"

if [[ ! -f "$JAR" || -n "$(find "$ROOT/core/src" "$ROOT/consolebased/src" "$ROOT/WebScraping/PlayerData.txt" -newer "$JAR" -print -quit)" ]]; then
    echo "▶ Building console app..."
    (cd "$ROOT" && mvn -q -B -pl consolebased -am package -DskipTests)
fi

exec java -jar "$JAR" "$@"
