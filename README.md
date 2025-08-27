# Fantasy-Football-Mock-Draft-Simulator
A personal project to build a fantasy football mock draft simulator

## Project Status
- Consolebased version is finished
- WebApp ongoing

## Project Structure

The project has been organized with shared components to reduce duplication:

```
FantasyFootballMockDraftSim/
├── shared/
│   └── src/main/java/com/example/
│       ├── common/                  # Shared components for both applications
│       │   ├── Logger.java          # Shared logging utility
│       │   ├── PlayerDataObject.java # Shared player data model
│       │   ├── VaribleOddsPicker.java # Shared random picker utility
│       │   └── PlayerModels/        # Shared player position models
│       └── console/                 # Console-specific models
│           ├── PlayerModel.java     # Console player wrapper
│           └── TeamModel.java       # Console team wrapper
├── WebApp/Backend/                  # Spring Boot web application
│   └── src/main/java/com/example/Mock/
│       ├── API/                     # REST Controllers
│       ├── DAO/                     # Data Access Objects
│       └── Service/                 # Business Logic Services
├── consolebased/                    # Console-based application
│   └── src/main/java/com/example/
│       ├── Mock/
│       │   ├── DAO/                 # Data Access Objects
│       │   └── Service/             # Business Logic Services
│       ├── MockDraftDriver.java     # Main console application
│       └── DraftHandler.java        # Draft simulation logic
├── mockDraft.sql                    # Full database with sample data
├── emptyMockDraftDBTables.sql       # Empty database structure
└── ffb_aliases.sh                   # Convenience aliases
```

## Shared Components

The following components have been consolidated into the `shared/` directory:

- **Logger**: Comprehensive logging system with daily categorized log files (auth, draft, application)
- **PlayerDataObject**: Core player data model used by both applications
- **VaribleOddsPicker**: Random selection utility for draft simulations
- **PlayerModels**: Position-specific player models (QB, RB, WR, TE, K, DST)

Both applications use these shared components to ensure consistency and reduce code duplication.

## Database Setup

The project includes SQL files at the root level for easy database setup:

- **`mockDraft.sql`**: Complete database with sample data (players, teams, etc.)
- **`emptyMockDraftDBTables.sql`**: Database structure only (empty tables)

Use the database aliases to quickly set up your local MySQL database:

```bash
# Set up database with sample data
ffbSetupDB

# Or set up empty database structure
ffbSetupEmptyDB

# Connect to database
ffbConnectDB

# View tables
ffbShowTables
```

## Shared File Management

To maintain the single source of truth in the `shared/` directory while supporting different build systems:

- **WebApp**: Temporarily syncs shared files for Maven compilation, then removes them before Docker build
- **Consolebased**: Uses shared files directly in Docker builds (no local copying needed)
- **DevStartup Scripts**: Automatically handle the sync and cleanup process for development
- **Git Safety**: Shared files are compiled into JAR, then temporary copies are removed to prevent Git tracking

The `ffbDevStartupWebApp` script automatically syncs shared files for Maven compilation, compiles them into the JAR file, then removes the temporary files before Docker build. This ensures the latest shared components are always used while preventing Git from tracking any temporary copies.

## Recent Reorganization

The project has been restructured for better organization:

- **Consolebased**: Reorganized to match WebApp structure with Mock/DAO and Mock/Service directories
- **WebApp**: Removed duplicate PlayerModels directory (now uses shared PlayerModels)
- **Shared Components**: All common code (Logger, PlayerDataObject, PlayerModels, VaribleOddsPicker) consolidated in shared/ directory
- **Consistent Structure**: Both applications now follow similar architectural patterns
- **DevStartup Scripts**: Moved to project root with sync functionality

## Requirements

WebScraper:
- Python 3.9 or higher
- ChromeDriver
- Google Chrome


WebApp:
- Java 17 or higher
- npm
- tsc
- mysql
- Docker

## Installation/How to Run

WebScraper:
- `pip install selenium`
- python3 'WebScraper/scraper.py'


consolebased via Docker: 
- `docker build -f consolebased/Dockerfile -t app-console:latest .'
- `docker run -it app-console:latest`

webapp via Docker:
- `docker build -f webapp/Backend/Dockerfile -t app-web:latest .'
- `docker run -it app-web:latest`


consolebased via local:
- `javac consolebased/src/main/java/com/example/MockDraftDriver.java'

webapp via local:
- `javac WebApp/Backend/src/main/java/com/example/Mock/Application.java'

## Usage
This project is opensource so feel to fork to host your host your own version of the webapp and consolebased version. However I open to PRs to improve the project.

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## License
[MIT]

## Acknowledgements
- [FantasyPros](https://www.fantasypros.com/) for providing the data
- [Selenium](https://www.selenium.dev/) for providing the web scraping tools
- [Spring](https://spring.io/) for providing the webapp framework
- [Docker](https://www.docker.com/) for providing the containerization tools
- [Maven](https://maven.apache.org/) for providing the build tools
- [MySQL](https://www.mysql.com/) for providing the database

## Contact
Created by [Bryan Lorden] (
    github: https://github.com/lorden22
    LinkedIn: https://www.linkedin.com/in/blorden/
    email: blorden.dev@gmail.com
)

## Project Aliases

To simplify building, running, and developing the project, a set of helpful aliases is provided in the `ffb_aliases.sh` file at the project root.

**To use these aliases:**

1. After cloning the repo, run:
   
   ```bash
   source /path/to/FantasyFootballMockDraftSim/ffb_aliases.sh
   ```
   Replace `/path/to/` with the actual path to your cloned repo.

2. To load these aliases automatically in every terminal session, add the above line to your `~/.bashrc` or `~/.zshrc`.

**Provided Aliases:**

*Build & Run:*
- `ffbRoot` — Change directory to the project root
- `buildConsoleDraft` — Build the console app Docker image
- `buildWebbackendDraft` — Build the web backend Docker image
- `runConsoleDraft` — Run the console app
- `runWebbackendDraft` — Run the web backend
- `ffbDevStartupWebApp` — Run the web app dev startup script (includes shared file sync)
- `ffbDevStartupConsole` — Run the console app dev startup script
- `mvnCleanFFB` — Clean the Maven project

*Database:*
- `ffbSetupDB` — Import full database with sample data (mockDraft.sql)
- `ffbSetupEmptyDB` — Import empty database structure (emptyMockDraftDBTables.sql)
- `ffbConnectDB` — Connect to the local database
- `ffbShowTables` — Show all tables in the database


