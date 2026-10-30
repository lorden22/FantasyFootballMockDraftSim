# syntax=docker/dockerfile:1.7
# Build from the repository root:  docker build -f consolebased/Dockerfile -t mockdraft-console .
# Run standalone (embedded DB kept in a volume):
#   docker run -it --rm -v mockdraft-console:/home/app/.mockdraft mockdraft-console
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /src
COPY pom.xml ./
COPY core/pom.xml core/
COPY consolebased/pom.xml consolebased/
COPY WebApp/Backend/pom.xml WebApp/Backend/
COPY core/src core/src
COPY WebScraping/PlayerData.txt WebScraping/
COPY consolebased/src consolebased/src
RUN --mount=type=cache,target=/root/.m2 mvn -B -q -pl consolebased -am package -DskipTests

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S app && adduser -S -G app -h /home/app app
COPY --from=build --chown=app:app /src/consolebased/target/mockdraft-console.jar /app/mockdraft-console.jar
USER app
WORKDIR /home/app
ENTRYPOINT ["java", "-jar", "/app/mockdraft-console.jar"]
