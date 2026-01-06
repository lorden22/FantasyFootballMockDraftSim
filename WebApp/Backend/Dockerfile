FROM openjdk:17-jdk-slim

# Install required packages including Maven
RUN apt-get update -y && \
    apt-get install -y \
    mariadb-server \
    mariadb-client \
    openssl \
    curl \
    maven \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Create application directory
WORKDIR /app

# Copy backend source code
COPY WebApp/Backend/ /app/backend/

# Copy shared classes to backend source
COPY shared/src/main/java/com/example/ /app/backend/src/main/java/com/example/

# Generate SSL certificate and copy to resources
COPY WebApp/Backend/generate-ssl.sh /app/generate-ssl.sh
RUN chmod +x /app/generate-ssl.sh && /app/generate-ssl.sh

# Copy the keystore to backend resources for Spring Boot
RUN mkdir -p /app/backend/src/main/resources && \
    cp /app/keystore.p12 /app/backend/src/main/resources/keystore.p12

# Build the backend JAR
WORKDIR /app/backend
RUN mvn clean package -DskipTests

# Copy the built JAR to app directory
WORKDIR /app
RUN cp /app/backend/target/WebBackend-0.0.1-SNAPSHOT.jar /app/app.jar

# Copy required files
COPY WebScraping/PlayerData.txt PlayerData.txt
COPY mockDraft.sql mockDraft.sql
COPY WebApp/Backend/startup.sh startup.sh

# Make scripts executable
RUN chmod +x startup.sh

# SSL certificates are already in place from generation step

# Create necessary directories for MariaDB
RUN mkdir -p /var/lib/mysql /var/run/mysqld /var/log/mysql

# Set permissions for MariaDB
RUN chown -R mysql:mysql /var/lib/mysql /var/run/mysqld /var/log/mysql

# Expose ports
EXPOSE 8080 8443 3306

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f -k https://localhost:8443/api/health || exit 1

ENTRYPOINT ["/bin/bash", "/app/startup.sh"]

