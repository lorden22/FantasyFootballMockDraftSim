FROM openjdk:17-jdk-slim

RUN apt-get update -y && \
    apt-get install -y mariadb-server mariadb-client && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

ARG JAR_FILE=WebApp/Backend/target/WebBackend-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} app.jar
ADD WebApp/Backend/src/main/java/com/example/Mock/StartingClasses/WebScraping/PlayerData.txt PlayerData.txt
ADD WebApp/Backend/mockDraft2.sql mockDraft2.sql
ADD WebApp/Backend/startup.sh startup.sh
RUN chmod +x startup.sh

EXPOSE 8080
EXPOSE 3306

ENTRYPOINT ["/bin/bash", "/startup.sh"]

