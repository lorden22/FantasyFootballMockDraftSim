# FantasyFootballMockDraftSim Aliases

# Set the base directory to the location of this script
export FFB_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

alias ffbRoot='cd "$FFB_ROOT"'
alias buildConsoleDraft='docker build -f "$FFB_ROOT/consolebased/Dockerfile" -t app-console:latest "$FFB_ROOT"'
alias buildWebbackendDraft='docker build -f "$FFB_ROOT/WebApp/Backend/Dockerfile" -t app-backend:latest "$FFB_ROOT"'
alias runConsoleDraft='docker run -it app-console'
alias runWebbackendDraft='docker run -p 80:8080 app-backend'
alias ffbDevStartupWebApp='"$FFB_ROOT/devStartupWebApp.sh"'
alias ffbDevStartupConsole='"$FFB_ROOT/devStartupConsole.sh"'
alias mvnCleanFFB='mvn clean -f "$FFB_ROOT/WebApp/Backend/pom.xml"'

# Database aliases
alias ffbSetupDB='mysql -u root -p < "$FFB_ROOT/mockDraft.sql"'
alias ffbSetupEmptyDB='mysql -u root -p < "$FFB_ROOT/emptyMockDraftDBTables.sql"'
alias ffbConnectDB='mysql -u root -p db'
alias ffbShowTables='mysql -u root -p -e "USE db; SHOW TABLES;"' 