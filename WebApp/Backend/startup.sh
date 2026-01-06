#!/bin/bash

echo "Starting Fantasy Football Mock Draft Sim container..."

# Set environment variables (use env vars with defaults)
USER="${DB_USER:-root}"
PASSWORD="${DB_PASSWORD:-changeme}"
DATABASE="${DB_NAME:-db}"

# Function to handle cleanup on exit
cleanup() {
    echo "Shutting down services..."
    kill $MYSQL_PID $NGINX_PID $JAVA_PID 2>/dev/null
    exit 0
}

# Set up signal handlers
trap cleanup SIGTERM SIGINT

echo "Initializing MariaDB..."
# Initialize MariaDB data directory if it doesn't exist
if [ ! -d "/var/lib/mysql/mysql" ]; then
    echo "Installing MariaDB system tables..."
    mysql_install_db --user=mysql --datadir=/var/lib/mysql
fi

echo "Starting MariaDB server..."
mysqld_safe --user=mysql &
MYSQL_PID=$!

echo "Waiting for MariaDB to be ready..."
for i in {1..30}; do
    # Try connecting without password first
    if mysql -u root -e "SELECT 1" >/dev/null 2>&1; then
        echo "MariaDB is ready (no password)!"
        # Database is fresh, set it up
        echo "Setting up database..."
        mysql -u root << EOF
CREATE DATABASE IF NOT EXISTS $DATABASE;
ALTER USER 'root'@'localhost' IDENTIFIED BY '$PASSWORD';
FLUSH PRIVILEGES;
EOF
        echo "Loading database schema..."
        mysql -u root --password="$PASSWORD" $DATABASE < /app/mockDraft.sql
        break
    # Try connecting with password
    elif mysql -u root --password="$PASSWORD" -e "SELECT 1" >/dev/null 2>&1; then
        echo "MariaDB is ready (with password)!"
        # Database already exists with password
        echo "Database already configured."
        break
    fi
    
    if [ $i -eq 30 ]; then
        echo "ERROR: MariaDB failed to start within 60 seconds"
        ps aux | grep mysql
        exit 1
    fi
    echo "Waiting for MariaDB... attempt $i/30"
    sleep 2
done

echo "Database setup completed successfully."

echo "Starting Spring Boot application..."
java -jar /app/app.jar &
JAVA_PID=$!

echo "Waiting for Spring Boot to be ready..."
for i in {1..30}; do
    if curl -f -k https://localhost:8443/api/health >/dev/null 2>&1; then
        echo "Spring Boot is ready!"
        break
    fi
    if [ $i -eq 30 ]; then
        echo "WARNING: Spring Boot may not be fully ready, continuing anyway..."
        break
    fi
    echo "Waiting for Spring Boot... attempt $i/30"
    sleep 2
done

echo "Starting nginx..."
nginx -g "daemon off;" &
NGINX_PID=$!

echo "All services started successfully!"
echo "Application is now running:"
echo "  - Frontend: https://localhost (or your domain)"
echo "  - Backend API: https://localhost/api/"
echo "  - Health Check: https://localhost/health"

# Wait for all background processes
wait
