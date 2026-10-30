# syntax=docker/dockerfile:1.7
# Build from the repository root:  docker build -f WebApp/Backend/Dockerfile -t mockdraft-web .

# ---- 1. React frontend ----
FROM node:22-alpine AS frontend
WORKDIR /src/WebApp/frontend
COPY WebApp/frontend/package.json WebApp/frontend/package-lock.json ./
RUN npm ci --no-audit --no-fund
COPY WebApp/frontend/ ./
RUN npm run build

# ---- 2. Java (core + web), with the built frontend bundled as static resources ----
FROM maven:3.9-eclipse-temurin-21 AS backend
WORKDIR /src
COPY pom.xml ./
COPY core/pom.xml core/
COPY consolebased/pom.xml consolebased/
COPY WebApp/Backend/pom.xml WebApp/Backend/
COPY core/src core/src
COPY WebScraping/PlayerData.txt WebScraping/
COPY WebApp/Backend/src WebApp/Backend/src
COPY --from=frontend /src/WebApp/frontend/dist WebApp/frontend/dist
RUN --mount=type=cache,target=/root/.m2 mvn -B -q -pl WebApp/Backend -am package -DskipTests

# ---- 3. Minimal runtime: JRE only, non-root ----
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S app && adduser -S -G app -h /app app
WORKDIR /app
COPY --from=backend --chown=app:app /src/WebApp/Backend/target/mockdraft-web.jar app.jar
USER app
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
    CMD wget -qO- http://127.0.0.1:8080/api/health >/dev/null || exit 1
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-XX:+ExitOnOutOfMemoryError", "-Djava.security.egd=file:/dev/urandom", "-jar", "/app/app.jar"]
