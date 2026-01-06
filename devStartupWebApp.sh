#!/bin/bash

# Fantasy Football Mock Draft Sim - Development Web App Startup
# This script builds and runs the backend in a container with embedded database
# while allowing frontend to be served locally for development

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

echo "🚀 Fantasy Football Mock Draft Sim - Development Web App Startup"
echo "================================================================"

# Detect operating system
UNAME_OUT="$(uname -s)"
case "${UNAME_OUT}" in
    Linux*)     machine=Linux;;
    Darwin*)    machine=Mac;;
    CYGWIN*)    machine=Cygwin;;
    MINGW*)     machine=MinGw;;
    *)          machine="UNKNOWN:${UNAME_OUT}"
esac

if [ "$machine" = "Mac" ]; then
    print_status "Running on Mac"
    MAIN_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
elif [ "$machine" = "Linux" ]; then
    print_status "Running on Linux"
    MAIN_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
elif [ "$machine" = "Cygwin" ] || [ "$machine" = "MinGw" ]; then
    print_status "Running on Windows"
    MAIN_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
else
    print_error "Running on Unknown OS: ${UNAME_OUT}"
    print_error "Please run this script on a Mac, Linux, or Windows machine"
    exit 1
fi

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    print_error "Docker is not installed. Please install Docker first."
    exit 1
fi

print_status "Docker will handle shared files and Maven compilation..."

print_status "Stopping and removing any existing containers..."
if docker ps -a --format "table {{.Names}}" | grep -q "^app-backend$"; then
    docker stop app-backend || true
    docker rm app-backend || true
fi

print_status "Removing any existing images..."
if docker images --format "table {{.Repository}}:{{.Tag}}" | grep -q "^app-backend:latest$"; then
    docker rmi app-backend:latest || true
fi

print_status "Skipping local Maven compilation - Docker will handle the build..."

print_status "Building backend container with embedded database..."
docker pull openjdk:17-jdk-slim
docker build -f WebApp/Backend/Dockerfile -t app-backend:latest .

print_status "Starting backend server with embedded database..."
# Use different ports to avoid conflict with existing services
# Backend API: 8080 (HTTP), 8443 (HTTPS), Database: 33306 (to avoid conflict with local MySQL)
docker run -d --name app-backend \
    -p 8080:8080 \
    -p 8443:8443 \
    -p 33306:3306 \
    app-backend:latest

print_status "Waiting for backend services to start..."
sleep 15

# Check if backend is healthy
print_status "Checking backend health..."
if curl -f -k https://localhost:8443/api/health > /dev/null 2>&1; then
    print_status "Backend is healthy!"
else
    print_warning "Backend health check failed. It may still be starting up..."
fi

echo
echo "🎉 Development Backend Deployment Complete!"
echo "=========================================="
echo
echo "Backend is running in container:"
echo "  🔌 Backend API (HTTP): http://localhost:8080/api/"
echo "  🔐 Backend API (HTTPS): https://localhost:8443/api/"
echo "  🗄️  Database: localhost:33306 (mapped from container port 3306)"
echo "  ❤️  Health Check: https://localhost:8443/api/health"
echo
echo "Frontend Development:"
echo "  📁 Frontend files: WebApp/webpages/"
echo "  🌐 Starting Live Server with HTTPS..."
echo

# Generate SSL certificates for Live Server if they don't exist
if [ ! -f "WebApp/webpages/cert.pem" ] || [ ! -f "WebApp/webpages/key.pem" ]; then
    print_status "Generating SSL certificates for Live Server..."
    cd WebApp/webpages
    openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -days 365 -nodes \
        -subj "/C=US/ST=State/L=City/O=Organization/OU=OrgUnit/CN=localhost"
    cd ../..
fi

# Check if Live Server is available
if command -v live-server &> /dev/null; then
    print_status "Starting Live Server with HTTPS on port 5500..."
    cd WebApp/webpages
    live-server --port=5500 --https=cert.pem --https-key=key.pem --cors &
    LIVE_SERVER_PID=$!
    cd ../..
    echo "  🌐 Live Server: https://localhost:5500/"
    echo "  📋 Dev Guide: https://localhost:5500/dev-server.html"
elif command -v npx &> /dev/null; then
    print_status "Live Server not found, using npx http-server with HTTPS..."
    cd WebApp/webpages
    npx http-server . -p 5500 -S -C cert.pem -K key.pem --cors &
    HTTP_SERVER_PID=$!
    cd ../..
    echo "  🌐 HTTP Server: https://localhost:5500/"
    echo "  📋 Dev Guide: https://localhost:5500/dev-server.html"
else
    print_warning "Neither Live Server nor npx found. Please install one:"
    echo "  npm install -g live-server"
    echo "  # OR #"
    echo "  npm install -g http-server"
    echo "  Then manually serve: cd WebApp/webpages && live-server --port=5500 --https=cert.pem --https-key=key.pem"
fi

echo
echo "Useful commands:"
echo "  📋 View backend logs: docker logs -f app-backend"
echo "  🛑 Stop backend: docker stop app-backend"
echo "  🔄 Restart backend: docker restart app-backend"
echo "  🐛 Debug backend: docker exec -it app-backend bash"
if [ ! -z "$LIVE_SERVER_PID" ]; then
    echo "  🛑 Stop Live Server: kill $LIVE_SERVER_PID"
elif [ ! -z "$HTTP_SERVER_PID" ]; then
    echo "  🛑 Stop HTTP Server: kill $HTTP_SERVER_PID"
fi
echo
print_status "Development environment ready!"
print_warning "You may see browser security warnings for self-signed certificates. Click 'Advanced' and 'Proceed'." 