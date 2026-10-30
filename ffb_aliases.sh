# MockDraft Pro shell aliases:  source ffb_aliases.sh
export FFB_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

alias ffbRoot='cd "$FFB_ROOT"'

# Local development (no Docker)
alias ffbDevStartupWebApp='"$FFB_ROOT/devStartupWebApp.sh"'
alias ffbDevStartupConsole='"$FFB_ROOT/devStartupConsole.sh"'
alias ffbTest='mvn -q -f "$FFB_ROOT/pom.xml" test && (cd "$FFB_ROOT/WebApp/frontend" && npm run typecheck)'
alias ffbBuild='(cd "$FFB_ROOT/WebApp/frontend" && npm ci && npm run build) && mvn -q -f "$FFB_ROOT/pom.xml" package -DskipTests'

# Docker
alias buildWebbackendDraft='docker build -f "$FFB_ROOT/WebApp/Backend/Dockerfile" -t mockdraft-web "$FFB_ROOT"'
alias buildConsoleDraft='docker build -f "$FFB_ROOT/consolebased/Dockerfile" -t mockdraft-console "$FFB_ROOT"'
alias ffbUp='docker compose -f "$FFB_ROOT/docker-compose.yml" up -d --build'
alias ffbDown='docker compose -f "$FFB_ROOT/docker-compose.yml" down'
alias ffbLogs='docker compose -f "$FFB_ROOT/docker-compose.yml" logs -f app'
alias runConsoleDraft='docker compose -f "$FFB_ROOT/docker-compose.yml" run --rm console'
alias runConsoleDraftStandalone='docker run -it --rm -v mockdraft-console:/home/app/.mockdraft mockdraft-console'
alias ffbConnectDB='docker compose -f "$FFB_ROOT/docker-compose.yml" exec db mariadb -u mockdraft -p mockdraft'
