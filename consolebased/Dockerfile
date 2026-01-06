FROM openjdk:17-jdk-slim

# Install required packages including MariaDB
RUN apt-get update -y && \
    apt-get install -y \
    mariadb-server \
    mariadb-client \
    curl \
    wget \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Create application directory
WORKDIR /app

# Copy console source
COPY consolebased/src/main/java/com/example /app/src/main/java/com/example

# Copy shared classes
COPY shared/src/main/java/com/example/common /app/src/main/java/com/example/common
COPY shared/src/main/java/com/example/console /app/src/main/java/com/example/console

# Download MySQL connector
RUN wget https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar -O /app/mysql-connector-java.jar

# Create necessary directories
RUN mkdir -p /app/bin /app/logs

# Compile Java sources
RUN find src/main/java -name "*.java" > sources.txt && \
    javac -cp /app/mysql-connector-java.jar -d bin @sources.txt

# Copy required database files
COPY mockDraft.sql mockDraft.sql
COPY emptyMockDraftDBTables.sql emptyMockDraftDBTables.sql

# Create startup script for console with embedded database
RUN echo '#!/bin/bash' > /app/startup-console.sh && \
    echo 'set -e' >> /app/startup-console.sh && \
    echo 'DB_PASSWORD="${DB_PASSWORD:-changeme}"' >> /app/startup-console.sh && \
    echo 'echo "Initializing MariaDB..."' >> /app/startup-console.sh && \
    echo 'mysql_install_db --user=mysql --datadir=/var/lib/mysql' >> /app/startup-console.sh && \
    echo 'echo "Starting MariaDB..."' >> /app/startup-console.sh && \
    echo 'mysqld_safe --user=mysql --datadir=/var/lib/mysql &' >> /app/startup-console.sh && \
    echo 'echo "Waiting for MariaDB to start..."' >> /app/startup-console.sh && \
    echo 'until mysqladmin ping -h localhost --silent; do sleep 2; done' >> /app/startup-console.sh && \
    echo 'echo "Setting up database..."' >> /app/startup-console.sh && \
    echo 'mysql -uroot -e "CREATE DATABASE IF NOT EXISTS db;"' >> /app/startup-console.sh && \
    echo 'mysql -uroot -e "ALTER USER '\''root'\''@'\''localhost'\'' IDENTIFIED BY '\''$DB_PASSWORD'\'';"' >> /app/startup-console.sh && \
    echo 'mysql -uroot -p"$DB_PASSWORD" -e "FLUSH PRIVILEGES;"' >> /app/startup-console.sh && \
    echo 'echo "Loading database schema..."' >> /app/startup-console.sh && \
    echo 'mysql -uroot -p"$DB_PASSWORD" db < emptyMockDraftDBTables.sql' >> /app/startup-console.sh && \
    echo 'echo "Loading sample data..."' >> /app/startup-console.sh && \
    echo 'mysql -uroot -p"$DB_PASSWORD" db < mockDraft.sql' >> /app/startup-console.sh && \
    echo 'echo "Database ready! Starting console application..."' >> /app/startup-console.sh && \
    echo 'java -cp bin:/app/mysql-connector-java.jar -Ddb.host=localhost com.example.MockDraftDriver' >> /app/startup-console.sh

# Make script executable
RUN chmod +x /app/startup-console.sh

# Create necessary directories for MariaDB
RUN mkdir -p /var/lib/mysql /var/run/mysqld /var/log/mysql

# Set permissions for MariaDB
RUN chown -R mysql:mysql /var/lib/mysql /var/run/mysqld /var/log/mysql

# Expose database port (for potential external access)
EXPOSE 3306

ENTRYPOINT ["/bin/bash", "/app/startup-console.sh"]