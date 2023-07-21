# Fantasy-Football-Mock-Draft-Simulator
A personal project to build a fantasy football mock draft simulator

## Project Status
- Consolebased version is finished
- WebApp ongiong

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


